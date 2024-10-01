package net.lugocorp.kingdom.game;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import java.util.Optional;
import net.lugocorp.kingdom.events.StratifiedEventReceiver;
import net.lugocorp.kingdom.math.Point;
import net.lugocorp.kingdom.world.World;

/**
 * Stores all the data for a single ongoing game
 */
public class Game {
    public Optional<Point> hoveredTile = Optional.empty();
    public final StratifiedEventReceiver buildingHandlers = new StratifiedEventReceiver();
    public final StratifiedEventReceiver abilityHandlers = new StratifiedEventReceiver();
    public final StratifiedEventReceiver itemHandlers = new StratifiedEventReceiver();
    public final StratifiedEventReceiver unitHandlers = new StratifiedEventReceiver();
    public final StratifiedEventReceiver tileHandlers = new StratifiedEventReceiver();
    public final World world;

    public Game(World world) {
        this.world = world;
    }

    /**
     * Used by the {@link GameCameraController} to set the currently hovered tile
     */
    public Void setHoveredTile(Point p) {
        if (this.world.isInBounds(p)) {
            this.hoveredTile = Optional.of(p);
        } else {
            this.hoveredTile = Optional.empty();
        }
        return null;
    }
}
