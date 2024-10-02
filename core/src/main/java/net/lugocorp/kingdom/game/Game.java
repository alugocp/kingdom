package net.lugocorp.kingdom.game;
import net.lugocorp.kingdom.assets.AssetsLoader;
import net.lugocorp.kingdom.events.EventHandlerBundle;
import net.lugocorp.kingdom.world.World;

/**
 * Stores all the data for a single ongoing game
 */
public class Game {
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
}
