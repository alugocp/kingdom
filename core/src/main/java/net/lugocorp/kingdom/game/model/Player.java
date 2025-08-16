package net.lugocorp.kingdom.game.model;
import net.lugocorp.kingdom.ai.Actor;
import net.lugocorp.kingdom.utils.Colors;
import com.badlogic.gdx.graphics.Color;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents a human or AI that is playing the game
 */
public class Player {
    // TODO create ComputerPlayer subclass for AI's
    private final boolean human;
    public final List<Artifact> artifacts = new ArrayList<>();
    public final Set<Building> buildings = new HashSet<>();
    public final Set<Unit> units = new HashSet<>();
    public final Color color = Colors.getFromPool();
    public final Actor actor = new Actor();
    public final String name;
    public int numRecruitmentOptions = 3;
    public int auctionChips = 0;
    public int unitPoints = 0;
    public int tiles = 0;
    public int gold = 0;
    public Fate fate;

    public Player(String name, Fate fate, boolean human) {
        this.human = human;
        this.name = name;
        this.fate = fate;
    }

    /**
     * This should only be used in conjunction with Kryo rehydration
     */
    public Player() {
        this.name = null;
        this.human = false;
    }

    /**
     * Returns the number of bare tiles this Player has access to
     */
    public int getBareTiles() {
        return this.tiles - this.buildings.size();
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
            return p.name.equals(this.name);
        }
        return false;
    }
}
