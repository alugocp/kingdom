package net.lugocorp.kingdom.game.player;
import net.lugocorp.kingdom.game.model.Artifact;
import net.lugocorp.kingdom.game.model.Building;
import net.lugocorp.kingdom.game.model.Fate;
import net.lugocorp.kingdom.game.model.Tile;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.Colors;
import net.lugocorp.kingdom.utils.code.SideEffect;
import net.lugocorp.kingdom.utils.math.Point;
import com.badlogic.gdx.graphics.Color;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

/**
 * Represents a human or AI that is playing the game
 */
public abstract class Player {
    private Fate fate;
    public static final int INITIAL_GOLD = 50;
    public final List<Artifact> artifacts = new ArrayList<>();
    public final Set<Building> buildings = new HashSet<>();
    public final Set<Unit> units = new HashSet<>();
    public final Color color = Colors.getFromPool();
    public final String name;
    public int gold = Player.INITIAL_GOLD;
    public int numRecruitmentOptions = 3;
    public int auctionChips = 0;
    public int unitPoints = 0;
    public int tiles = 0;

    Player(String name, Fate fate) {
        this.name = name;
        this.fate = fate;
    }

    /**
     * Returns true if this Player represents the human
     */
    public abstract boolean isHumanPlayer();

    /**
     * Adds visibility on the Tile to this Player
     */
    public abstract void incrementVisibility(Tile t);

    /**
     * Removes visibility from the Tile to this Player
     */
    public abstract void decrementVisibility(Tile t);

    /**
     * Dictates what happens when this Player activates an Ability
     */
    public abstract SideEffect select(GameView view, Set<Point> points, String error,
            Function<Point, SideEffect> action);

    /**
     * Returns the number of bare tiles this Player has access to
     */
    public int getBareTiles() {
        return this.tiles - this.buildings.size();
    }

    /**
     * Gets this Player's Fate
     */
    public Fate getFate() {
        return this.fate;
    }

    /**
     * Sets this Player's Fate
     */
    public void setFate(Fate fate) {
        this.fate = fate;
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
