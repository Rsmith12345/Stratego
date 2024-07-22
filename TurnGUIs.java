import javax.swing.*;
import java.awt.*;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 *  Author: Richard Smith
 *  Last Updated: 7/19/2024
 *
 *  TurnGUIs contains all the methods that display
 *  the main GUIs for the player turns...
 */

public class TurnGUIs {

    /**
     *  showYourTurn displays the main GUI for when it's the player's
     *  turn. The main component of this GUI is the 10x10 board itself.
     *
     *  output stream "out" needed to write to server and "boardInput"
     *  is needed to create the board display. This should be a message
     *  from the server - sent by sendBoard method.
     */

    public static void showYourTurn(DataOutputStream out, String boardInput) {

        //to be used as output to the server
        String[] output = new String[2];

        JFrame mainFrame = new JFrame("Stratego");
        mainFrame.getContentPane().setLayout(new BorderLayout());
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);

        JPanel outerBoardPanel = new JPanel();
        JPanel boardPanel = new JPanel();
        boardPanel.setSize(700, 700);
        boardPanel.setPreferredSize(new Dimension(700, 700));

        //creating the 10x10 board using the boardInput String

        boardPanel.setLayout(new GridLayout(10,10));
        JButton[][] buttons = new JButton[10][10];

        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {

                if (boardInput.indexOf(',') != -1) {

                    buttons[row][col] = new JButton(boardInput.substring(0,
                                                                         boardInput.indexOf(',')));
                    boardInput = boardInput.substring(boardInput.indexOf(',') + 1);

                } else {

                    buttons[row][col] = new JButton(boardInput);
                    boardInput = "";

                }

                //coloring the buttons :)

                if (boardInput.substring(0, boardInput.indexOf(',')).equals("?")) {
                    buttons[row][col].setBackground(Color.PINK);
                } else if (boardInput.substring(0, boardInput.indexOf(',')).equals("~")) {
                    buttons[row][col].setBackground(Color.BLACK);
                } else if (!boardInput.substring(0, boardInput.indexOf(',')).equals(" ")) {
                    buttons[row][col].setBackground(Color.CYAN);
                }

                //the following variables needed for output assignment using row and col
                int finalRow = row;
                int finalCol = col;
                buttons[row][col].addActionListener(e -> {

                    if (output[0] == null) {

                        output[0] = String.valueOf(finalRow) + "," + String.valueOf(finalCol);

                    } else {

                        output[1] = "," + String.valueOf(finalRow) + ","
                                + String.valueOf(finalCol);

                        try {

                            //writing info to the server to request a move
                            //the requested move is from the (row,col) of output[0] to
                            //the (row,col) of output[1]

                            out.writeUTF(output[0] + output[1]);
                            mainFrame.dispose();

                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }

                    }

                });

                boardPanel.add(buttons[row][col]);

            }
        }

        outerBoardPanel.add(boardPanel);

        //the rest is the messages/buttons at the top and bottom of the GUI

        JPanel topPanel = new JPanel();
        JLabel turnMessage = new JLabel("Your Turn: Make a Move");
        turnMessage.setFont(new Font("Roboto", Font.BOLD, 34));
        topPanel.add(turnMessage, BorderLayout.CENTER);


        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.PAGE_AXIS));

        JLabel instructions = new JLabel("Be warned - once a move has been" +
                " made, it can't be undone!");
        instructions.setFont(new Font("Roboto", Font.BOLD, 22));
        instructions.setAlignmentX(Component.CENTER_ALIGNMENT);
        bottomPanel.add(instructions);

        JButton rules = new JButton("Rules");
        instructions.setAlignmentX(Component.CENTER_ALIGNMENT);
        bottomPanel.add(rules);
        rules.addActionListener(e -> {
            SimpleGUIs.showRules();
        });

        // this is for formatting reasons, adds space before bottom of the page
        JLabel blankLine = new JLabel("             ");
        blankLine.setFont(new Font("Roboto", Font.BOLD, 26));
        blankLine.setAlignmentX(Component.CENTER_ALIGNMENT);
        bottomPanel.add(blankLine);

        mainFrame.add(topPanel, BorderLayout.NORTH);
        mainFrame.add(bottomPanel, BorderLayout.SOUTH);
        mainFrame.add(outerBoardPanel, BorderLayout.CENTER);


        mainFrame.setVisible(true);

    }


    /**
     *  showOtherTurn displays a GUI showing the board for when it's the other player's
     *  turn. Essentially the same as showYourTurn but you can't request piece moves.
     *
     *  Needs the boardInput string to display the board - from server sendBoard method
     */

    public static void showOtherTurn(String boardInput) {

        JFrame mainFrame = new JFrame("Stratego");
        mainFrame.getContentPane().setLayout(new BorderLayout());
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);

        JPanel outerBoardPanel = new JPanel();
        JPanel boardPanel = new JPanel();
        boardPanel.setSize(700, 700);
        boardPanel.setPreferredSize(new Dimension(700, 700));

        //creating 10x10 board

        boardPanel.setLayout(new GridLayout(10,10));
        JButton[][] buttons = new JButton[10][10];

        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {

                if (boardInput.indexOf(',') != -1) {

                    buttons[row][col] = new JButton(boardInput.substring(0,
                                                                         boardInput.indexOf(',')));

                    boardInput = boardInput.substring(boardInput.indexOf(',') + 1);

                } else {

                    buttons[row][col] = new JButton(boardInput);
                    boardInput = "";

                }

                //coloring buttons

                if (boardInput.substring(0, boardInput.indexOf(',')).equals("?")) {
                    buttons[row][col].setBackground(Color.PINK);
                } else if (boardInput.substring(0, boardInput.indexOf(',')).equals("~")) {
                    buttons[row][col].setBackground(Color.BLACK);
                } else if (!boardInput.substring(0, boardInput.indexOf(',')).equals(" ")) {
                    buttons[row][col].setBackground(Color.CYAN);
                }

                boardPanel.add(buttons[row][col]);

            }
        }

        outerBoardPanel.add(boardPanel);

        //the rest is the top and bottom labels/buttons for the GUI

        JPanel topPanel = new JPanel();
        JLabel turnMessage = new JLabel("Other Player's Turn...");
        turnMessage.setFont(new Font("Roboto", Font.BOLD, 34));
        topPanel.add(turnMessage, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.PAGE_AXIS));

        JLabel instructions = new JLabel("You can't make a move now - wait until its your turn.");
        instructions.setFont(new Font("Roboto", Font.BOLD, 22));
        instructions.setAlignmentX(Component.CENTER_ALIGNMENT);
        bottomPanel.add(instructions);

        JButton rules = new JButton("Rules");
        instructions.setAlignmentX(Component.CENTER_ALIGNMENT);
        bottomPanel.add(rules);
        rules.addActionListener(e -> {
            SimpleGUIs.showRules();
        });

        // this is for formatting reasons, adds space before bottom of the page
        JLabel blankLine = new JLabel("             ");
        blankLine.setFont(new Font("Roboto", Font.BOLD, 26));
        blankLine.setAlignmentX(Component.CENTER_ALIGNMENT);
        bottomPanel.add(blankLine);

        mainFrame.add(topPanel, BorderLayout.NORTH);
        mainFrame.add(bottomPanel, BorderLayout.SOUTH);
        mainFrame.add(outerBoardPanel, BorderLayout.CENTER);

        mainFrame.setVisible(true);

    }


    /**
     *  showSetup displays the setup page, which includes the 10x10
     *  board and a list of pieces the player must set up on their side of the board
     *
     *  this needs an output "out" stream to write to the server
     *  and the boardInput string to create the board
     */

    public static void showSetup(DataOutputStream out, String boardInput) {

        //output to be written to the server
        String[] output = new String[2];

        JFrame mainFrame = new JFrame("Stratego");
        mainFrame.getContentPane().setLayout(new BorderLayout());
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        mainFrame.setLocationRelativeTo(null);

        JPanel outerBoardPanel = new JPanel();
        JPanel boardPanel = new JPanel();
        boardPanel.setSize(700, 700);
        boardPanel.setPreferredSize(new Dimension(700, 700));

        //creating the 10x10 board

        boardPanel.setLayout(new GridLayout(10,10));
        JButton[][] buttons = new JButton[10][10];

        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {

                buttons[row][col] = new JButton(boardInput.substring(0, boardInput.indexOf(',')));

                //coloring buttons

                if (boardInput.substring(0, boardInput.indexOf(',')).equals("?")) {
                    buttons[row][col].setBackground(Color.PINK);
                } else if (boardInput.substring(0, boardInput.indexOf(',')).equals("~")) {
                    buttons[row][col].setBackground(Color.BLACK);
                } else if (!boardInput.substring(0, boardInput.indexOf(',')).equals(" ")) {
                    buttons[row][col].setBackground(Color.CYAN);
                }

                //the following variables needed for output assignment using i and j
                int finalRow = row;
                int finalCol = col;
                buttons[row][col].addActionListener(e -> {

                    if (output[0] == null) {

                        output[0] = "move" + String.valueOf(finalRow) + ","
                                + String.valueOf(finalCol);

                    } else {

                        output[1] = "," + String.valueOf(finalRow) + ","
                                + String.valueOf(finalCol);

                        try {

                            //writing to move a piece on the board (at position output[0]) to
                            //...another position on the board (at output[1])

                            out.writeUTF(output[0] + output[1]);
                            mainFrame.dispose();

                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }

                    }

                });

                boardPanel.add(buttons[row][col]);

                if (boardInput.indexOf(',') != -1) {
                    boardInput = boardInput.substring(boardInput.indexOf(',') + 1);
                } else {
                    boardInput = "";
                }

            }
        }

        outerBoardPanel.add(boardPanel);

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.PAGE_AXIS));

        JLabel setupMessage = new JLabel("Set your Pieces");
        setupMessage.setFont(new Font("Roboto", Font.BOLD, 28));
        setupMessage.setAlignmentX(Component.CENTER_ALIGNMENT);
        topPanel.add(setupMessage);

        JButton readyUp = new JButton("Click when Ready!");
        readyUp.setAlignmentX(Component.CENTER_ALIGNMENT);
        readyUp.addActionListener(e -> {

            try {

                out.writeUTF("ready");
                mainFrame.dispose();

            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }


        });

        topPanel.add(readyUp);

        //creating the bottom rows of buttons that need to be placed on the board

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new GridLayout(2,10, 8, 4));
        JButton[][] lowerButtons = new JButton[2][10];

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 10; j++) {

                if (boardInput.indexOf(',') != -1) {

                    lowerButtons[i][j] =new JButton(boardInput.substring(0,
                                                                         boardInput.indexOf(',')));

                } else {
                    lowerButtons[i][j] = new JButton(boardInput);
                }

                lowerButtons[i][j].setBackground(Color.CYAN);

                //the following variables needed for output assignment using i and j
                int finalI = i;
                int finalJ = j;
                lowerButtons[i][j].addActionListener(e -> {

                    if (output[0] == null) {

                        //the math converts the 2 row list used for the GUI to the 1 row list
                        // used for the Server end
                        output[0] = "set" + String.valueOf((finalI * 10) + finalJ);

                    } else {

                        if (output[0].startsWith("set")) {

                            output[0] = "set" + String.valueOf((finalI * 10) + finalJ);

                        } else {

                            output[0] = output[0].replaceFirst("move", "undo");
                            output[1] = "," + String.valueOf((finalI * 10) + finalJ);

                            try {

                                //writing to server for putting a piece from the bottom rows
                                //to the 10x10 board or vice versa

                                out.writeUTF(output[0] + output[1]);
                                mainFrame.dispose();

                            } catch (IOException ex) {
                                throw new RuntimeException(ex);
                            }

                        }

                    }

                });

                bottomPanel.add(lowerButtons[i][j]);

                if (boardInput.indexOf(',') != -1) {
                    boardInput = boardInput.substring(boardInput.indexOf(',') + 1);
                } else {
                    boardInput = "";
                }

            }
        }

        //must create blank label until reassigned
        JLabel instructionsMessage = new JLabel();

        //instructions on where to put the pieces depends on what number player they are
        if (boardInput.equals("1")) {

            instructionsMessage = new JLabel("Click on a piece and then the spot where you want" +
                    " it on the board. They must all be placed in the upper 2 rows of the board " +
                    "and you can rearrange pieces you've already put on the board.");

        } else {

            instructionsMessage = new JLabel("Click on a piece and then the spot where you want" +
                    " it on the board. They must all be placed in the lower 2 rows of the board " +
                    "and you can rearrange pieces you've already put on the board.");

        }

        instructionsMessage.setFont(new Font("Roboto", Font.BOLD, 16));
        instructionsMessage.setAlignmentX(Component.CENTER_ALIGNMENT);
        topPanel.add(instructionsMessage);

        JButton rules = new JButton("Rules");
        rules.setAlignmentX(Component.CENTER_ALIGNMENT);
        topPanel.add(rules);
        rules.addActionListener(e -> {
            SimpleGUIs.showRules();
        });

        //adding it all to the mainFrame

        mainFrame.add(topPanel, BorderLayout.NORTH);
        mainFrame.add(bottomPanel, BorderLayout.SOUTH);
        mainFrame.add(outerBoardPanel, BorderLayout.CENTER);

        mainFrame.setVisible(true);

    }

}
