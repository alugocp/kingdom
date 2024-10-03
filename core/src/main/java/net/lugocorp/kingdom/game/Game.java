package net.lugocorp.kingdom.game;
import net.lugocorp.kingdom.engine.GameGraphics;
import net.lugocorp.kingdom.events.EventHandlerBundle;
import net.lugocorp.kingdom.world.World;

/**
 * Stores all the data for a single ongoing game
 */
public class Game {
    public final EventHandlerBundle events;
    public final GameGraphics graphics;
    public final Generator generator;
    public final World world;

    public Game(GameGraphics graphics, EventHandlerBundle events, World world) {
        this.generator = new Generator(this);
        this.graphics = graphics;
        this.events = events;
        this.world = world;
    }
}
