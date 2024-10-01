package net.lugocorp.kingdom.game;
import net.lugocorp.kingdom.events.Event;
import net.lugocorp.kingdom.events.EventTarget;

/**
 * A passive or active effect that Units, Buildings and Tiles can use
 */
public class Ability implements EventTarget {
    public final String name;
    public String desc = "";

    public Ability(String name) {
        this.name = name;
    }

    /** {@inheritdoc} */
    @Override
    public void handleEvent(Game g, Event e) {
        g.abilityHandlers.handle(this.name, e);
    }
}
