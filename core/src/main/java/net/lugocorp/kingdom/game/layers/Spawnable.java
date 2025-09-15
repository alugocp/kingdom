package net.lugocorp.kingdom.game.layers;
import net.lugocorp.kingdom.ui.views.GameView;

/**
 * This interface represents any object that is physically "spawned" into the
 * Game World
 */
public interface Spawnable {

    /**
     * Spawns this loaded object into the World
     */
    public void spawn(GameView view);
}
