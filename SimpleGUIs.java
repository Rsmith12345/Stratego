import javax.swing.*;
import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 *  Author: Rich Smith
 *  Last Update: 7/19/2024
 *
 *  SimpleGUIs contains all the methods for displaying the more basic
 *  GUIs associated with the game (so not the main turn ones)
 */

public class SimpleGUIs {

    /**
     *  deleteAllFrames disposes of all visible frames at the time it's called
     */

    public static void deleteAllFrames() {

        Window[] pages = JFrame.getWindows();

        for (Window page : pages) {

            if (page instanceof JFrame) {
                JFrame frame = (JFrame) page;
                frame.dispose();
            }

        }

    }


    /**
     *  showWelcome displays the startup welcome page
     *  (needs an output stream "out" for write to server)
     */

    public static void showWelcome(DataOutputStream out) {

        JFrame mainFrame = new JFrame("Stratego");
        mainFrame.getContentPane().setLayout(new BorderLayout());
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        mainFrame.setLocationRelativeTo(null);

        JLabel message = new JLabel("WELCOME TO STRATEGO: LIGHTNING EDITION", JLabel.CENTER);
        message.setAlignmentX(Component.CENTER_ALIGNMENT);
        message.setFont(new Font("Roboto", Font.BOLD, 50));

        //play button functionality

        JButton play = new JButton("PLAY");
        play.setAlignmentX(Component.CENTER_ALIGNMENT);
        play.setMinimumSize(new Dimension(800, 100));
        play.setPreferredSize(new Dimension(800, 100));
        play.setMaximumSize(new Dimension(800, 100));

        play.addActionListener(e -> {
            try {
                mainFrame.dispose();
                out.writeUTF("play");

            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        //rules button functionality

        JButton rules = new JButton("Rules");
        rules.setAlignmentX(Component.CENTER_ALIGNMENT);
        rules.addActionListener(e -> {
            SimpleGUIs.showRules();
        });

        //adding everything to mainPanel and then adding mainPanel to maineFrame

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        mainPanel.add(Box.createVerticalGlue());
        mainPanel.add(message);
        mainPanel.add(Box.createVerticalGlue());
        mainPanel.add(play);
        mainPanel.add(Box.createVerticalGlue());
        mainPanel.add(rules);
        mainPanel.add(Box.createVerticalGlue());

        mainFrame.getContentPane().add(mainPanel, BorderLayout.CENTER);

        mainFrame.setVisible(true);

    }


    /**
     *  showWaiting shows a waiting page for when a player
     *  clicks play but there is no one to play against yet
     *  or a player clicks ready after setup and the other player
     *  isn't ready yet. (no buttons on this one)
     */

    public static void showWaiting() {

        JFrame mainFrame = new JFrame("Stratego");
        mainFrame.getContentPane().setLayout(new BorderLayout());
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        mainFrame.setLocationRelativeTo(null);

        JLabel message = new JLabel("Waiting for player 2...", JLabel.CENTER);
        message.setFont(new Font("Roboto", Font.BOLD, 50));

        mainFrame.getContentPane().add(message, BorderLayout.CENTER);

        mainFrame.setVisible(true);

    }


    /**
     *  showInvalid displays the invalid move popup when a player
     *  tries to do something not allowed by the rules
     */

    public static void showInvalid() {

        JFrame mainFrame = new JFrame("Stratego");
        mainFrame.getContentPane().setLayout(new BorderLayout());
        mainFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        mainFrame.setPreferredSize(new Dimension(1000, 400));
        mainFrame.setMaximumSize(new Dimension(1000, 400));
        mainFrame.setMinimumSize(new Dimension(1000, 400));
        mainFrame.pack();
        mainFrame.setLocationRelativeTo(null);

        JLabel message = new JLabel("Invalid Move: read the rules if necessary", JLabel.CENTER);
        message.setFont(new Font("Roboto", Font.BOLD, 30));

        mainFrame.getContentPane().add(message, BorderLayout.CENTER);

        mainFrame.setVisible(true);

    }


    /**
     *  showNotReady displays a popup when the player hits the "ready" button
     *  but hasn't correctly set up their pieces
     */

    public static void showNotReady() {

        JFrame mainFrame = new JFrame("Stratego");
        mainFrame.getContentPane().setLayout(new BorderLayout());
        mainFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        mainFrame.setPreferredSize(new Dimension(1100, 400));
        mainFrame.setMaximumSize(new Dimension(1100, 400));
        mainFrame.setMinimumSize(new Dimension(1100, 400));
        mainFrame.pack();
        mainFrame.setLocationRelativeTo(null);

        JLabel message = new JLabel("Setup Incomplete: read the rules if necessary",
                                    JLabel.CENTER);
        message.setFont(new Font("Roboto", Font.BOLD, 30));

        mainFrame.getContentPane().add(message, BorderLayout.CENTER);

        mainFrame.setVisible(true);

    }


    /**
     * showRules displays a popup with all the rules...
     */

    public static void showRules() {

        //these lines are longer than I'd like, but I feel it looks best on the frame
        String text = """
                The following set of rules mostly aligns with standard Stratego. However, some modifications
                from the faster paced "Ultimate Lightning" version and the 1980 game The Generals have also been
                implemented. If you know how to play Stratego, just read the special rule section at the bottom.\n
                
                GOAL: To win the game, a player must either capture their opponent's flag or remove all of their opponent's
                      unmovable pieces from the board.\n
                      
                SETUP: Each player must arrange their pieces on the 2 rows of their side of the board (top or bottom rows).
                       Most pieces are represented with a number for strength (higher is stronger). M stands for Marshal,
                       Sp for spy, Sc for scout, B for bomb, and F for flag.\n
                       
                TURNS: For a player's turn, they must move one of their pieces. Pieces can only move one spot vertically or
                       horizontally (but not diagonally) - with the exception of scouts. A piece can either be moved into
                       an empty space or the space of enemy piece, initiating an attack. For most pieces, the piece with
                       the higher number strength wins and the other loses. The losing piece is removed from the board and
                       if the attacking piece wins, it takes the spot of the piece it battled.  Pieces must not be moved 
                       back and forth between the same two squares in three consecutive turns.\n
                       
                SPECIAL PIECES: 
                        - Flag: Cannot be moved ... Will be captured by any piece that attacks it
                        - Bomb: Cannot be moved ... Only the scout can defeat it in battle ... Will disappear
                                from the board after any battle (win or lose)
                        - Spy: Wins when attacking (not defending against!) a Marshal ... Acts as a piece with 1 strength
                                in all other scenarios
                        - Scout: Wins against a bomb ... Can move any number of spaces in a vertical or horizontal direction
                                  in one turn (like a rook in chess) ... Has a strength of 2
                        - Marshal: Acts as a regular piece with a strength of 10
                             
                SPECIAL RULES:  These rules differentiate this game from standard Stratego rules.
                                   1) Only 20 pieces, no miners - but scouts can "defuse" bombs.
                                   2) Bombs only work once, after one battle they are removed from the board.
                                   3) When two pieces of the same rank/strength fight, the attacking one wins.
                                   4) Neither player reveals the strength/type of their piece in the event of an attack.
                """;

        JFrame mainFrame = new JFrame("Stratego");
        mainFrame.getContentPane().setLayout(new BorderLayout());
        mainFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        mainFrame.setPreferredSize(new Dimension(1100, 700));
        mainFrame.setMaximumSize(new Dimension(1100, 700));
        mainFrame.setMinimumSize(new Dimension(1100, 700));
        mainFrame.pack();
        mainFrame.setLocationRelativeTo(null);

        JLabel title = new JLabel("RULES", JLabel.CENTER);
        title.setFont(new Font("Roboto", Font.BOLD, 30));

        JTextArea message = new JTextArea();
        message.setFont(new Font("Roboto", Font.BOLD, 18));
        message.setText(text);
        message.setEditable(false);
        JScrollPane scroll = new JScrollPane(message);

        mainFrame.getContentPane().add(title, BorderLayout.NORTH);
        mainFrame.getContentPane().add(scroll, BorderLayout.CENTER);

        mainFrame.setVisible(true);

    }


    /**
     *  showWin displays the "You Won" page with an exit button
     */

    public static void showWin() {

        JFrame mainFrame = new JFrame("Stratego");
        mainFrame.getContentPane().setLayout(new BorderLayout());
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);

        JLabel message = new JLabel("YOU WIN!", JLabel.CENTER);
        message.setAlignmentX(Component.CENTER_ALIGNMENT);
        message.setFont(new Font("Roboto", Font.BOLD, 50));

        JButton exit = new JButton("EXIT");
        exit.setAlignmentX(Component.CENTER_ALIGNMENT);
        exit.setMinimumSize(new Dimension(400, 100));
        exit.setPreferredSize(new Dimension(400, 100));
        exit.setMaximumSize(new Dimension(400, 100));
        exit.addActionListener(e -> {
            mainFrame.dispose();
            System.exit(0);
        });


        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        mainPanel.add(Box.createVerticalGlue());
        mainPanel.add(message);
        mainPanel.add(Box.createVerticalGlue());
        mainPanel.add(exit);
        mainPanel.add(Box.createVerticalGlue());

        mainFrame.getContentPane().add(mainPanel, BorderLayout.CENTER);

        mainFrame.setVisible(true);

    }


    /**
     *  showLost displays the "You Lost" page with an exit button
     */

    public static void showLost() {

        JFrame mainFrame = new JFrame("Stratego");
        mainFrame.getContentPane().setLayout(new BorderLayout());
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);

        JLabel message = new JLabel("YOU LOST...", JLabel.CENTER);
        message.setAlignmentX(Component.CENTER_ALIGNMENT);
        message.setFont(new Font("Roboto", Font.BOLD, 50));

        JButton exit = new JButton("EXIT");
        exit.setAlignmentX(Component.CENTER_ALIGNMENT);
        exit.setMinimumSize(new Dimension(400, 100));
        exit.setPreferredSize(new Dimension(400, 100));
        exit.setMaximumSize(new Dimension(400, 100));
        exit.addActionListener(e -> {
            mainFrame.dispose();
            System.exit(0);
        });


        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        mainPanel.add(Box.createVerticalGlue());
        mainPanel.add(message);
        mainPanel.add(Box.createVerticalGlue());
        mainPanel.add(exit);
        mainPanel.add(Box.createVerticalGlue());

        mainFrame.getContentPane().add(mainPanel, BorderLayout.CENTER);

        mainFrame.setVisible(true);

    }

}