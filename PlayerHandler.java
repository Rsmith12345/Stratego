import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 *  Author: Richard Smith
 *  Last Updated: 7/20/2024
 *
 *  This controls the main game logic / game flow for the
 *  server side. It is run when a client connects to the server.
 *  The client-server connection is multithreaded too.
 */

public class PlayerHandler implements Runnable {

    private Socket socket;
    private Board gameBoard;
    private int playerNum;

    /**
     *  This is the simple constructor for the playerHandler.
     *  You need the socket and the game's gameboard object.
     */

    public PlayerHandler(Socket socket, Board gameBoard) {

        this.socket = socket;
        this.gameBoard = gameBoard;

    }


    /**
     *  This contains the main game logic / control flow for the game.
     *  See the class comment above.
     */

    public void run() {

        try {

            //this establishes the input and output stream
            DataInputStream in = new DataInputStream(this.socket.getInputStream());
            DataOutputStream out = new DataOutputStream(this.socket.getOutputStream());
            String message = "";

            Object gatekeeper = new Object();

            //synchronization is to be safe mainly (for if both join at the same time)
            //unsure if this is really necessary in this case though...
            synchronized (gatekeeper) {
                this.playerNum = gameBoard.getPlayerCount() + 1;
                gameBoard.setPlayerCount(gameBoard.getPlayerCount() + 1);
            }

            //making the player's set of pieces and their move history
            Piece[] set = Piece.makeSet(this.playerNum);
            String[] moveHistory = {"", ""};

            //setting stage server communication and logic

            while(gameBoard.isSettingStage()) {

                message = in.readUTF();

                if (message.equals("play")) {

                    if (gameBoard.getPlayerCount() != 2) {

                        out.writeUTF("wait");
                        while (gameBoard.getPlayerCount() != 2) {
                            Thread.sleep(1000);
                        }
                        gameBoard.sendBoard(out, this.playerNum, set);

                    } else {
                        gameBoard.sendBoard(out, this.playerNum, set);
                    }

                } else {

                    //this interprets the string communication from the client
                    //makes the move if its valid on the board

                    String[] methodStringInput = {"", "", "", ""};
                    int[] methodInput = {0, 0, 0, 0};

                    if (message.startsWith("move")) {

                        message = message.substring(4);
                        methodStringInput = message.split(",");
                        for (int i = 0; i < 4; i++) {
                            methodInput[i] = Integer.parseInt(methodStringInput[i]);
                        }

                        if (gameBoard.checkValid(methodInput[0], methodInput[1], methodInput[2],
                                methodInput[3], this.playerNum, moveHistory)) {

                            gameBoard.movePiece(methodInput[0], methodInput[1], methodInput[2],
                                    methodInput[3]);
                            gameBoard.sendBoard(out, this.playerNum, set);

                        } else {

                            out.writeUTF("invalid");
                            gameBoard.sendBoard(out, this.playerNum, set);

                        }

                    } else if (message.startsWith("set")) {

                        message = message.substring(3);
                        methodStringInput = message.split(",");
                        for (int i = 0; i < 3; i++) {
                            methodInput[i] = Integer.parseInt(methodStringInput[i]);
                        }

                        if (gameBoard.setPiece(set, methodInput[0], methodInput[1],
                                methodInput[2])) {

                            gameBoard.sendBoard(out, this.playerNum, set);

                        } else {

                            out.writeUTF("invalid");
                            gameBoard.sendBoard(out, this.playerNum, set);

                        }

                    } else if (message.startsWith("undo")) {

                        message = message.substring(4);
                        methodStringInput = message.split(",");
                        for (int i = 0; i < 3; i++) {
                            methodInput[i] = Integer.parseInt(methodStringInput[i]);
                        }

                        if (gameBoard.undoSetPiece(set, methodInput[0], methodInput[1],
                                methodInput[2], this.playerNum)) {

                            gameBoard.sendBoard(out, this.playerNum, set);

                        } else {

                            out.writeUTF("invalid");
                            gameBoard.sendBoard(out, this.playerNum, set);

                        }

                    //checking if the player is ready to enter the main phase...

                    } else if (message.equals("ready")) {

                        if (gameBoard.checkReady(set, this.playerNum)) {

                            gameBoard.setNumReady(gameBoard.getNumReady() + 1);
                            message = "main stage" + Integer.toString(gameBoard.getNumReady());
                            out.writeUTF(message);

                            while (gameBoard.getNumReady() != 2) {
                                Thread.sleep(1000);
                            }

                            gameBoard.setSettingStage(false);

                            if (gameBoard.getTurn() == this.playerNum) {

                                //do nothing, next code executed is the while loop for "your turn"
                                gameBoard.setSettingStage(false);

                            } else {

                                out.writeUTF("Other turn");
                                gameBoard.sendBoard(out, playerNum, set);
                                while (gameBoard.getTurn() != this.playerNum) {
                                    Thread.sleep(1000);
                                }

                            }

                        } else {

                            out.writeUTF("Not ready");
                            gameBoard.sendBoard(out, this.playerNum, set);

                        }

                    }

                }

            }

            //main logic / flow control for main phase of the game

            while (true) {

                //check if someone won or lost
                if (!gameBoard.isOngoingGame()) {

                    if (!gameBoard.checkGameContinues(this.playerNum)) {
                        out.writeUTF("You lost");
                        System.exit(0);

                    } else {
                        out.writeUTF("You won");
                        System.exit(0);

                    }

                }

                //your turn communication with client

                out.writeUTF("Your turn");
                gameBoard.sendBoard(out, playerNum, set);

                message = in.readUTF();

                //interprets client message, makes the move if valid

                int[] methodInput = {0, 0, 0, 0};
                for (int i = 0; i < 4; i++) {

                    if (message.indexOf(",") != -1) {
                        methodInput[i] = Integer.parseInt(message.substring(0,
                                                                            message.indexOf(",")));
                        message = message.substring(message.indexOf(",") + 1);
                    } else {
                        methodInput[i] = Integer.parseInt(message);
                        break;
                    }

                }

                if (gameBoard.checkValid(methodInput[0], methodInput[1], methodInput[2],
                        methodInput[3], this.playerNum, moveHistory)) {

                    if (gameBoard.getField()[methodInput[2]][methodInput[3]] != null) {

                        gameBoard.battlePieces(methodInput[0], methodInput[1], methodInput[2],
                                methodInput[3]);

                    } else {

                        gameBoard.movePiece(methodInput[0], methodInput[1], methodInput[2],
                                methodInput[3]);

                    }

                    int otherPlayer = (this.playerNum == 1) ? 2 : 1;
                    int nextTurn = (gameBoard.getTurn() == 1) ? 2 : 1;

                    //check if you won or lost, if not goes to other player's turn

                    if (!gameBoard.checkGameContinues(this.playerNum)) {

                        out.writeUTF("You lost");
                        gameBoard.setTurn(nextTurn);
                        gameBoard.setOngoingGame(false);
                        return;

                    } else if (!gameBoard.checkGameContinues(otherPlayer)) {

                        out.writeUTF("You won");
                        gameBoard.setTurn(nextTurn);
                        gameBoard.setOngoingGame(false);
                        return;

                    } else {

                        out.writeUTF("Other turn");
                        gameBoard.sendBoard(out, this.playerNum, set);
                        gameBoard.setTurn(nextTurn);

                        //waiting here until it's your turn again
                        while (gameBoard.getTurn() != this.playerNum) {
                            Thread.sleep(1000);
                        }

                    }

                } else {

                    out.writeUTF("invalid");

                }

            }


        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

}
