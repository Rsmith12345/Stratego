import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Objects;

/**
 *  Author: Rich Smith
 *  Last Update: 7/18/2024
 *
 *  Establishes the game board itself plus some status-related
 *  variables that describe the game state for all players
 */

public class Board {

    //field is the 10x10 board (or battlefield) itself
    private Piece[][] field;

    //the number of players connected to the game (how many connected to the server)
    private int playerCount;

    //the int number of the player whose turn it currently is
    private int turn;

    //the number of players who have completed the setup phase
    private int numReady;

    //boolean saying if the game is still in the setup stage or not
    private boolean settingStage;

    //boolean saying if the game is over or not
    private boolean ongoingGame;


    /**
     * constructor that sets up the initial 10x10 field and the
     * other object fields
     */

    public Board() {

        this.field = new Piece[10][10];
        this.playerCount = 0;
        this.numReady = 0;

        //randomizing whether player 1 or 2 goes first
        this.turn = (Math.random() <= 0.5) ? 1 : 2;

        this.settingStage = true;
        this.ongoingGame = true;

        for (int j = 0; j < 10; j++) {
            for (int k = 0; k <10; k++) {
                this.field[j][k] = null;
            }
        }

        //creating the spaces on the board no player can move into

        this.field[4][2] = new Piece("NoMove", -1, -1);
        this.field[5][2] = new Piece("NoMove", -1, -1);
        this.field[4][3] = new Piece("NoMove", -1, -1);
        this.field[5][3] = new Piece("NoMove", -1, -1);

        this.field[4][6] = new Piece("NoMove", -1, -1);
        this.field[5][6] = new Piece("NoMove", -1, -1);
        this.field[4][7] = new Piece("NoMove", -1, -1);
        this.field[5][7] = new Piece("NoMove", -1, -1);

    }


    /**
     *  checkGameContinues checks whether the player
     *  matching the input playerNum is still "in the game",
     *  meaning they still have a movable piece and their flag
     *  on the board.  If this is the case, the method returns true
     *  indicating that they have not lost. Otherwise, returns false.
     *  (This must be checked for both players to determine if game
     *   is actually over or not)
     */

    public boolean checkGameContinues(int playerNum) {

        boolean pieceExists = false;
        boolean flagExists = false;

        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {

                if ((this.field[row][col] != null)
                        && (this.field[row][col].getPlayer() == playerNum)) {

                    if (this.field[row][col].getName().equals("Flag")) {
                        flagExists = true;
                    } else if (!this.field[row][col].getName().equals("Bomb")) {
                        pieceExists = true;
                    }

                }

            }
        }

        return (pieceExists && flagExists);
    }


    /**
     *  checkValid checks if a client-requested piece move is valid or not.
     *  rowBefore/colBefore are the original position of the piece the player wants to move
     *  rowAfter/colAfter are the board position they want to move the piece to
     *  playerNum is the int of the player requesting the move and moveHistory
     *  stores the last 2 moves by that player
     *
     *  method returns true if the move is allowed by the rules, otherwise returns false
     */

    public boolean checkValid(int rowBefore, int colBefore, int rowAfter, int colAfter,
                              int playerNum, String[] moveHistory) {

        if (this.settingStage) {

            //checkValid for the setup phase

            return (this.field[rowBefore][colBefore] != null)
                    && (this.field[rowBefore][colBefore].getPlayer() == playerNum)
                    && (this.field[rowAfter][colAfter] == null);

        } else {

            //checkValid for the main phase

            String thisMove = Integer.toString(rowBefore) + "," + Integer.toString(colBefore) + ","
                    + Integer.toString(rowAfter) + "," + Integer.toString(colAfter);


            //checks for "back-and-forth" rule in Stratego

            if (thisMove.equals(moveHistory[0])) {
                return false;
            } else {
                moveHistory[0] = moveHistory[1];
                moveHistory[1] = thisMove;
            }

            //checks that spot has a piece in it and it's a movable piece

            if (this.field[rowBefore][colBefore] == null) {
                return false;
            }

            if ((this.field[rowBefore][colBefore].getName().equals("Flag"))
                || (this.field[rowBefore][colBefore].getName().equals("Bomb"))) {

                return false;
            }

            //the following checks that the spot has a piece from to the player requesting the move
            //...and that the space it is being moved to is empty or has another players piece
            //...and that the movement is legal (not diagonal)

            int otherPlayer = (playerNum == 1) ? 2 : 1;

            if ((this.field[rowBefore][colBefore].getPlayer() == playerNum)
                    && ((this.field[rowAfter][colAfter] == null)
                    || (this.field[rowAfter][colAfter].getPlayer() == otherPlayer))) {

                if ((rowBefore == rowAfter) ^ (colBefore == colAfter)) {

                    if (!Objects.equals(this.field[rowBefore][colBefore].getName(), "Scout")) {

                        if (((rowAfter == (rowBefore + 1)) || (rowAfter == (rowBefore - 1)))
                                || ((colAfter == (colBefore + 1))
                                || (colAfter == (colBefore - 1)))) {

                            return true;

                        } else {
                            return false;
                        }
                    } else {
                        return true;
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }

        //that's a lotta else statements :(
    }


    /**
     *  movePiece moves the piece at rowBefore/colBefore position on the board to
     *  the rowAfter/colAfter position
     */

    public synchronized void movePiece(int rowBefore, int colBefore, int rowAfter, int colAfter) {

        Piece moved = this.field[rowBefore][colBefore];
        this.field[rowBefore][colBefore] = null;
        this.field[rowAfter][colAfter] = moved;

    }


    /**
     *  setPiece moves the piece from the starting pieceList (should be the standard set of pieces)
     *  at the input listPosition to the board position input through boardRow/boardColumn.
     *  If successful, returns true. Otherwise, false
     */

    public synchronized boolean setPiece(Piece[] pieceList, int listPosition, int boardRow,
                                         int boardColumn) {

        //internal "checkValid" function

        if ((pieceList[listPosition] != null) && (field[boardRow][boardColumn] == null)) {

            this.field[boardRow][boardColumn] = pieceList[listPosition];
            pieceList[listPosition] = null;

            return true;

        } else {
            return false;
        }

    }


    /**
     *  undoSetPiece does the opposite of setPiece (see documentation for the method above)
     *  basically, it moves a piece from the board to the pieceList
     *  If successful, returns true. Otherwise, false
     */

    public synchronized boolean undoSetPiece(Piece[] pieceList, int boardRow, int boardColumn,
                                             int listPosition, int playerNum) {

        //internal "checkValid" function

        if (field[boardRow][boardColumn] == null) {
            return false;
        }

        if ((field[boardRow][boardColumn].getPlayer() == playerNum)
                && (pieceList[listPosition] == null)) {

            pieceList[listPosition] = this.field[boardRow][boardColumn];
            this.field[boardRow][boardColumn] = null;

            return true;

        } else {
            return false;
        }

    }


    /**
     *  battlePieces takes the piece at attackRow/attackCol position
     *  and "battles" it with the piece at defenseRow/defenseCol position.
     *  If necessary, the method moves the pieces and removes pieces from the board
     *  accordingly.
     */

    public void battlePieces(int attackRow, int attackCol,
                             int defenseRow, int defenseCol) {

        //testing for special bomb scenarios

        if ((this.field[attackRow][attackCol].getName().equals("Scout"))
                && (this.field[defenseRow][defenseCol].getName().equals("Bomb"))) {

            this.field[defenseRow][defenseCol] = null;
            movePiece(attackRow,attackCol, defenseRow, defenseCol);
            return;

        }

        if (this.field[defenseRow][defenseCol].getName().equals("Bomb")) {

            this.field[defenseRow][defenseCol] = null;
            this.field[attackRow][attackCol] = null;
            return;

        }

        //testing for special spy scenarios

        if ((this.field[attackRow][attackCol].getName().equals("Spy"))
                && (this.field[defenseRow][defenseCol].getName().equals("Marshal"))) {

            this.field[defenseRow][defenseCol] = null;
            movePiece(attackRow,attackCol, defenseRow, defenseCol);
            return;

        }

        //standard battle described below

        if (this.field[attackRow][attackCol].getStrength() >=
                this.field[defenseRow][defenseCol].getStrength()) {

            this.field[defenseRow][defenseCol] = null;
            movePiece(attackRow,attackCol, defenseRow, defenseCol);

        } else {
            this.field[attackRow][attackCol] = null;
        }

    }


    /**
     *  sendBoard is used to write a string from the server to the client describing
     *  the state of the board. To do this, the method needs an output stream "out",
     *  the playerNum (this is necessary for showing the player the identity of their
     *  own pieces only), and a standard starting pieceList (starting piece set).  The
     *  pieceList is only necessary for the setup phase of the game. Otherwise, it isn't
     *  used - I continued to use the player's empty starting piece set array after the
     *  setup phase though.
     *
     *  Format of output:
     *  a comma separated list of the characters to be shown in each square on the board
     *  (going row1col1,row1col2,row1col3,etc.) and then (if the setup phase) the characters to
     *  be shown in the piece selection section below the board.  Finally, the list ends with
     *  the number associated with the player
     *
     *  Example output:
     *  M,Sc,Sp,4,6,5, , , ,~,~,?,?,? ... 4,7,1
     *  (space = empty square, ? = enemy, ~ = can't move there, the 1 at the end = playerNum)
     *
     *  The setup phase characters don't alter the format of the list, they just add more to it
     *  after the 100 positions for the 10x10 board but before the last number at the end
     */

    public synchronized void sendBoard(DataOutputStream out, int playerNum,
                                       Piece[] pieceList) throws IOException {

        //message to be output
        String message = "";

        //checking each board position to find what's there, assigns it a character representation
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {

                if (this.field[i][j] == null) {
                    message += " ,";
                } else if (this.field[i][j].getPlayer() == playerNum) {
                    if (this.field[i][j].getName().equals("Spy")) {
                        message += "Sp,";
                    } else if (this.field[i][j].getName().equals("Scout")) {
                        message += "Sc,";
                    } else if (this.field[i][j].getName().equals("Marshal")) {
                        message += "M,";
                    } else if (this.field[i][j].getName().equals("Bomb")) {
                        message += "B,";
                    } else if (this.field[i][j].getName().equals("Flag")) {
                        message += "F,";
                    } else {
                        message += String.valueOf(this.field[i][j].getStrength());
                        message += ",";
                    }
                } else if (this.field[i][j].getPlayer() == -1) {
                    message += "~,";
                } else {
                    message += "?,";
                }

            }
        }

        //gets rid of last comma
        message = message.substring(0, message.length() - 1);

        //this is for the setup phase of the game only

        if (this.settingStage) {

            message += ",";

            for (int i = 0; i < 20; i++) {
                if (pieceList[i] == null) {
                    message += " ,";
                } else if (pieceList[i].getName().equals("Spy")) {
                    message += "Sp,";
                } else if (pieceList[i].getName().equals("Scout")) {
                    message += "Sc,";
                } else if (pieceList[i].getName().equals("Marshal")) {
                    message += "M,";
                } else if (pieceList[i].getName().equals("Bomb")) {
                    message += "B,";
                } else if (pieceList[i].getName().equals("Flag")) {
                    message += "F,";
                } else {
                    message += String.valueOf(pieceList[i].getStrength());
                    message += ",";
                }

            }

            //adding playerNum identifier
            message += playerNum;

        }

        //outputting the message
        out.writeUTF(message);

    }


    /**
     *  checkReady checks if the player has properly set their pieces
     *  in the setup phase.  Returns true if all their pieces are on the
     *  board in valid spots. Otherwise, returns false.
     *
     *  To do this, the initial piece set and the playerNum is needed.
     */

    public boolean checkReady(Piece[] set, int playerNum) {

        //status = ready or not
        boolean status = true;

        //checking the initial piece set is empty (all are on the board at this point)

        for (int a = 0; a < 20; a++) {
            if (set[a] != null) {
                status = false;
                break;
            }
        }

        //checks if pieces are in correct positions
        // (player 1 pieces in top 2 rows, player 2 in bottom)

        if (playerNum == 1) {

            for (int row = 0; row < 2; row++) {
                for (int col = 0; col < 10; col++) {
                    if ((this.field[row][col] == null)
                            || (this.field[row][col].getPlayer() != playerNum)) {

                        status = false;
                        break;
                    }
                }
            }

        } else {

            for (int row = 8; row < 10; row++) {
                for (int col = 0; col < 10; col++) {
                    if ((this.field[row][col] == null)
                            || (this.field[row][col].getPlayer() != playerNum)) {

                        status = false;
                        break;
                    }
                }
            }

        }

        return status;

    }


    //THE FOLLOWING ARE GETTERS AND SETTERS

    public int getPlayerCount() {
        return playerCount;
    }

    public void setPlayerCount(int playerCount) {
        this.playerCount = playerCount;
    }

    public int getTurn() {
        return turn;
    }

    public void setTurn(int startingTurn) {
        this.turn = startingTurn;
    }

    public boolean isSettingStage() {
        return settingStage;
    }

    public void setSettingStage(boolean settingStage) {
        this.settingStage = settingStage;
    }

    public Piece[][] getField() {
        return field;
    }

    public int getNumReady() {
        return numReady;
    }

    public void setNumReady(int numReady) {
        this.numReady = numReady;
    }

    public boolean isOngoingGame() {
        return ongoingGame;
    }

    public void setOngoingGame(boolean ongoingGame) {
        this.ongoingGame = ongoingGame;
    }

}
