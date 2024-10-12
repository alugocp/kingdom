package net.lugocorp.kingdom.game.model;
import net.lugocorp.kingdom.game.events.Event;
import net.lugocorp.kingdom.game.events.EventReceiver;
import net.lugocorp.kingdom.ui.views.GameView;
import java.util.Optional;

/**
 * An in-game pickup to be used by Units
 */
public class Item implements EventReceiver {
    public Optional<String> icon = Optional.empty();
    public final String name;
    public String desc = "";

    Item(String name) {
        this.name = name;
    }

    /** {@inheritdoc} */
    @Override
    public void handleEvent(GameView view, Event e) {
        view.game.events.item.handle(view, this, e);
    }

    /** {@inheritdoc} */
    @Override
    public String getStratifier() {
        return this.name;
    }
}
