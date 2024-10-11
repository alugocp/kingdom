package net.lugocorp.kingdom.game.model;
import net.lugocorp.kingdom.game.events.Event;
import net.lugocorp.kingdom.game.events.EventTarget;
import net.lugocorp.kingdom.game.Game;
import java.util.Optional;

/**
 * An in-game pickup to be used by Units
 */
public class Item implements EventTarget {
    public Optional<String> icon = Optional.empty();
    public final String name;
    public String desc = "";

    Item(String name) {
        this.name = name;
    }

    /** {@inheritdoc} */
    @Override
    public void handleEvent(Game g, Event e) {
        g.events.item.handle(g, this.name, e);
    }
}
