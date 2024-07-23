# Stratego
Author: Rich Smith\
Date: 7/22/2024

### Introductory Description

Welcome to Stratego: Lightning edition! This Java program is a 2-player version of the classic board game Stratego. \
It utilizes Graphical User Interfaces (GUIs), socket programming techniques, and threads to create a fun, interactive \
game!  This specific variant uses 20 pieces and has other minor rule modifications to offer a faster-paced experience \
compared to the original version.

### How to Use (Display Version)

This game was programmed in an IDE, but can easily be run using the terminal as well. \
To easily display the game's features, its currently configured to be run on one computer. \
However, given the nature of the game, to actually play with a friend, it needs to be run on 2 computers. \
See the "How to Use (Real Version)" for that setup... 

1) Clone the repository
2) Duplicate the Client.java file (rename to Client1.java and Client2.java)
3) Compile all files (not necessary to do manually if using and IDE)
4) Run the Server file (this will initiate the server-side of the program)
5) Run the Client1 file (this will connect player1 to the server)
6) Run the Client2 file (this will connect player2 to the server)
7) Play the game!

IMPORTANT NOTES:
- If at any point one of the clients disconnects from the server (such as from accidentally exiting out of their main GUI window),
  both the server and client side of the game must be terminated and re-run.  The game is designed such that when 2 clients
  connect - at any point - to the server, the game may commence.  Any more or less than 2 connections is invalid.
- Consequently from point 1, the game is not meant to "loop" after a game has finished.  The server and client side will terminate
  gracefully and you will have to re-run both sides of the program for a new game.
- The game would not, in reality, be played on one computer. Thus, managing the 2 clients on one screen may be a bit clunky
  in multiple respects.  See the version below if you want the game's intended 2 player experience.

### How to Use (Real Version)

Computer 1:
1. Clone the repository
2. Compile all files (not necessary to do manually if using and IDE)
3. Run the Server file (this will initiate the server-side of the program)
4. Run the Client1 file (this will connect player1 to the server)

Computer 2:
1. Clone the repository or just copy the Client, SimpleGUIs, and TurnGUIs files (the rest pertain to the server-side and are unnecessary)
2. Go to the bottom of the code of the Client.java file, and 

















### Special Notes / Considerations
