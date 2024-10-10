package net.lugocorp.kingdom.game.model;
import java.util.Optional;
import net.lugocorp.kingdom.events.Event;
import net.lugocorp.kingdom.events.EventTarget;
import net.lugocorp.kingdom.game.Game;

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
