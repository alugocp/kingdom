package net.lugocorp.kingdom.game.model;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a human or AI that is playing the game
 */
public class Player {
    private final boolean human;
    public final Set<Artifact> artifacts = new HashSet<>();
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
