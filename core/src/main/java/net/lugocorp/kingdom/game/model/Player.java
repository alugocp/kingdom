package net.lugocorp.kingdom.game.model;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a human or AI that is playing the game
 */
public class Player {
    private final boolean human;
    public final List<Artifact> artifacts = new ArrayList<>();
    public final String name;
    public int numRecruitmentOptions = 3;
    public int auctionChips = 0;
    public int unitPoints = 0;
    public int bareTiles = 0;
    public int tiles = 0;
    public int gold = 0;
    public Fate fate;

    public Player(String name, Fate fate, boolean human) {
        this.human = human;
        this.name = name;
        this.fate = fate;
    }

    /**
     * Returns true if this Player represents the human
     */
    public boolean isHumanPlayer() {
        return this.human;
    }

    /** {@inheritdoc} */
    @Override
    public String toString() {
        return this.name;
    }

    /** {@inheritdoc} */
    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    /** {@inheritdoc} */
    @Override
    public boolean equals(Object o) {
        if (o != null && o instanceof Player) {
            Player p = (Player) o;
            return p.name == this.name;
        }
        return false;
    }
}
