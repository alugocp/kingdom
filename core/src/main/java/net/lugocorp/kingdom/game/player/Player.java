package net.lugocorp.kingdom.game.player;
import net.lugocorp.kingdom.game.model.Artifact;
import net.lugocorp.kingdom.game.model.Building;
import net.lugocorp.kingdom.game.model.Fate;
import net.lugocorp.kingdom.game.model.Tile;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.ui.ColorScheme;
import net.lugocorp.kingdom.ui.overlay.ResourceOverlay;
import net.lugocorp.kingdom.ui.views.GameView;
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
    public static final int INITIAL_GOLD = 100;
    private int unitPoints = 0;
    private Fate fate;
    public final List<Artifact> artifacts = new ArrayList<>();
    public final Set<Building> buildings = new HashSet<>();
    public final Set<Unit> units = new HashSet<>();
    public final String name;
    public final Color color;
    public int gold = Player.INITIAL_GOLD; // TODO make private, add Overlay on increase
    public int numRecruitmentOptions = 3;
    public int auctionChips = 0;
    public int tiles = 0;

    Player(String name, Fate fate, Color color) {
        this.color = color;
        this.name = name;
        this.fate = fate;
    }

    /**
     * Returns true if this Player represents the human
     */
    public abstract boolean isHumanPlayer();

    /**
     * Adds vision on the Tile to this Player
     */
    public abstract void incrementVision(Tile t);

    /**
     * Removes vision from the Tile to this Player
     */
    public abstract void decrementVision(Tile t);

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
        fate.setPlayer(this);
    }

    /**
     * Returns this Player's current amount of unit points
     */
    public int getUnitPoints() {
        return this.unitPoints;
    }

    /**
     * Changes this Player's current amount of unit points
     */
    public void addUnitPoints(GameView view, int amount) {
        this.unitPoints += amount;
        view.hud.top.update(view.game);
    }

    /**
     * Changes this Player's current amount of unit points and adds an Overlay
     */
    public void addUnitPoints(GameView view, Point p, int amount) {
        view.overlays.add(new ResourceOverlay(view, p, 0.14f, ColorScheme.GREEN.hex, amount)
                .then(() -> this.addUnitPoints(view, amount)));
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
