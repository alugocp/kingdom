package net.lugocorp.kingdom.game;
import net.lugocorp.kingdom.events.StratifiedEventReceiver;
import net.lugocorp.kingdom.world.World;

/**
 * Stores all the data for a single ongoing game
 */
public class Game {
    public final StratifiedEventReceiver buildingHandlers = new StratifiedEventReceiver();
    public final StratifiedEventReceiver unitHandlers = new StratifiedEventReceiver();
    public final StratifiedEventReceiver tileHandlers = new StratifiedEventReceiver();
    public final World world;

    public Game(World world) {
        this.world = world;
    }
}
