package net.lugocorp.kingdom.game.events;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.SideEffect;
import java.io.Serializable;

/**
 * Like an EventHandler but just for a single Event class
 */
public interface SingleEventHandler<T extends EventReceiver, E extends Event> extends Serializable {
    public SideEffect handle(GameView view, T receiver, E event);

    /**
     * This function allows us to serialize this lambda
     */
    public default Object writeReplace() {
        return this;
    }
}
