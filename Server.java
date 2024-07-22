import java.net.*;
import java.io.*;

/**
 *  Author: Richard Smith
 *  Last Updated: 7/19/2024
 *
 *  Server establishes the server side of the game
 */

public class Server {

    private Socket socket;

    private ServerSocket server;

    /**
     *  server constructor constantly looking for clients to connect with
     */

    public Server(int port) throws IOException {

        Board gameBoard = new Board();
        this.server = new ServerSocket(port);

        while (true) {

            this.socket = this.server.accept();

            Thread playerThread = new Thread(new PlayerHandler(this.socket, gameBoard));
            playerThread.start();

            //terminal message for debugging purposes mainly...
            System.out.println("Connected");

        }

    }

    /**
     * MAIN METHOD: this initiates the server side of the game by creating the server
     */

    public static void main(String[] args) throws IOException {
        Server server1 = new Server(8090);
    }

}
