package net.lugocorp.kingdom.gameplay.events;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.SideEffect;
import java.io.Serializable;

/**
 * This class represents the basic lambda function that powers the Event
 * handling system
 */
public interface EventHandler<T extends EventReceiver> extends Serializable {
    public SideEffect handle(GameView view, T receiver, Event event);

    /**
     * This function allows us to serialize this lambda
     */
    public default Object writeReplace() {
        return this;
    }
}
