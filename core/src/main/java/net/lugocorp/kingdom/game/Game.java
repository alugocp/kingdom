package net.lugocorp.kingdom.game;
import java.util.Optional;
import net.lugocorp.kingdom.assets.AssetsLoader;
import net.lugocorp.kingdom.events.EventHandlerBundle;
import net.lugocorp.kingdom.math.Point;
import net.lugocorp.kingdom.world.World;

/**
 * Stores all the data for a single ongoing game
 */
public class Game {
    public Optional<Point> hoveredTile = Optional.empty();
    public final EventHandlerBundle events;
    public final AssetsLoader assets;
    public final Generator generator;
    public final World world;

    public Game(AssetsLoader assets, EventHandlerBundle events, World world) {
        this.generator = new Generator(this);
        this.assets = assets;
        this.events = events;
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
