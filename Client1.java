import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 *  Author: Richard Smith
 *  Last Updated: 7/19/2024
 *
 *  This is the core of the client side of the program.
 *  The client class defines the flow of the UIs for the
 *  player and communicates with the server to move along
 *  the flow of the game overall.
 */

public class Client1 {

    private Socket socket;

    /**
     *  The client constructor controls the game flow for the client/UI side
     *  of the program. See the class description for more too.
     */

    public Client1(String address, int port) {

        try {

            //establishing connection

            this.socket = new Socket(address, port);
            DataInputStream in = new DataInputStream(this.socket.getInputStream());
            DataOutputStream out = new DataOutputStream(this.socket.getOutputStream());

            String message;
            boolean settingStage = true;

            //welcome UI
            SimpleGUIs.showWelcome(out);


            //read from server
            message = in.readUTF();

            //setup phase GUIs start
            if (message.equals("wait")) {
                SimpleGUIs.showWaiting();
                message = in.readUTF();
                SimpleGUIs.deleteAllFrames();
                TurnGUIs.showSetup(out, message);
            } else {
                TurnGUIs.showSetup(out, message);
            }

            //setting stage communication/UI flow
            while (settingStage) {

                message = in.readUTF();

                if (message.startsWith("main stage")) {

                    settingStage = false;

                } else {

                    if (message.equals("invalid")) {

                        message = in.readUTF();
                        TurnGUIs.showSetup(out, message);
                        SimpleGUIs.showInvalid();

                    } else if (message.equals("Not ready")){

                        message = in.readUTF();
                        TurnGUIs.showSetup(out, message);
                        SimpleGUIs.showNotReady();

                    } else {
                        TurnGUIs.showSetup(out,message);
                    }

                }

            }

            //starting main phase of the game
            if (message.equals("main stage1")) {
                SimpleGUIs.showWaiting();
                message = in.readUTF();
                SimpleGUIs.deleteAllFrames();
            } else {
                message = in.readUTF();
            }

            //main turn GUIs and server communication
            boolean invalid = false;
            while (true) {

                if (message.equals("Your turn")) {

                    SimpleGUIs.deleteAllFrames();
                    message = in.readUTF();
                    TurnGUIs.showYourTurn(out, message);

                    if (invalid) {
                        SimpleGUIs.showInvalid();
                        invalid = false;
                    }

                } else if (message.equals("Other turn")) {

                    message = in.readUTF();
                    TurnGUIs.showOtherTurn(message);

                //winning and losing GUIs

                } else if (message.equals("You won")) {

                    SimpleGUIs.deleteAllFrames();
                    SimpleGUIs.showWin();
                    in.close();
                    out.close();
                    socket.close();
                    while(true) {
                        Thread.currentThread().sleep(10000);
                    }

                } else if (message.equals("You lost")) {

                    SimpleGUIs.deleteAllFrames();
                    SimpleGUIs.showLost();
                    in.close();
                    out.close();
                    socket.close();
                    while(true) {
                        Thread.currentThread().sleep(10000);
                    }

                } else if (message.equals("invalid")) {
                    invalid = true;

                }

                message = in.readUTF();
            }

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     *  MAIN METHOD: This initiates the client/player side of the program
     */

    public static void main(String[] args) {
        Client1 client1 = new Client1("localhost", 8090);
    }

}
