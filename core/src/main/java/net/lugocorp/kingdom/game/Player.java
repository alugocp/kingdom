package net.lugocorp.kingdom.game;

/**
 * Represents a human or AI that is playing the game
 */
public class Player {
    public static final int MAX_UNIT_POINTS = 100;
    private final boolean human;
    public final String name;
    public int unitPoints = 0;
    public int bareTiles = 0;
    public int tiles = 0;
    public int gold = 0;

    public Player(String name, boolean human) {
        this.human = human;
        this.name = name;
    }

    /**
     * Returns true if this Player represents the human
     */
    public boolean isHumanPlayer() {
        return this.human;
    }

    /**
     * Controls how many unit points a Player gets each turn
     */
    public void gainUnitPoints() {
        this.unitPoints += (int) Math.floor(20f * this.bareTiles / this.tiles);
    }
}
