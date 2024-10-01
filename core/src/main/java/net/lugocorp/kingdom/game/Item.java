package net.lugocorp.kingdom.game;
import net.lugocorp.kingdom.events.Event;
import net.lugocorp.kingdom.events.EventTarget;

/**
 * An in-game pickup to be used by Units
 */
public class Item implements EventTarget {
    public final String name;
    public String desc = "";

    Item(String name) {
        this.name = name;
    }

    /** {@inheritdoc} */
    @Override
    public void handleEvent(Game g, Event e) {
        g.events.item.handle(this.name, e);
    }
}
