/**
 *  Author: Rich Smith
 *  Last Update: 7/18/2024
 *
 *  Establishes the properties of the game pieces
 */
public class Piece {

    //name of the piece, not currently important for most pieces
    private String name;

    //strength piece, higher is stronger unlike in the classic version
    private int strength;

    //the number of the player it belongs to
    private int player;

    /**
     *  Self-explanatory constructor
     */
    public Piece(String name, int strength, int playerNum) {
        this.name = name;
        this.strength = strength;
        this.player = playerNum;
    }


    /**
     *  makeSet creates a standard starting set of pieces
     *  and assigns them to the player matching the input playerNum
     */

    static public Piece[] makeSet(int playerNum) {
        Piece[] set = new Piece[20];

        set[0] = new Piece("Marshal", 10, playerNum);
        set[1] = new Piece("General", 9, playerNum);
        set[2] = new Piece("Colonel", 8, playerNum);
        set[3] = new Piece("Major", 7, playerNum);
        set[4] = new Piece("Captain", 6, playerNum);
        set[5] = new Piece("Captain", 6, playerNum);
        set[6] = new Piece("Lieutenant", 5, playerNum);
        set[7] = new Piece("Lieutenant", 5, playerNum);
        set[8] = new Piece("Sergeant", 4, playerNum);
        set[9] = new Piece("Sergeant", 4, playerNum);
        set[10] = new Piece("Corporal", 3, playerNum);
        set[11] = new Piece("Corporal", 3, playerNum);
        for (int i = 12; i < 16; i++) {
            set[i] = new Piece("Scout", 2, playerNum);
        }
        set[16] = new Piece("Spy", 1, playerNum);
        set[17] = new Piece("Bomb", 11, playerNum);
        set[18] = new Piece("Bomb", 11, playerNum);
        set[18] = new Piece("Bomb", 11, playerNum);
        set[19] = new Piece("Flag", 0, playerNum);

        return set;
    }


    //THE FOLLOWING ARE GETTERS AND SETTERS

    public String getName() {
        return name;
    }

    public int getStrength() {
        return strength;
    }

    public int getPlayer() {
        return player;
    }

}
