package net.lugocorp.kingdom.events;
import java.util.HashMap;
import java.util.Map;
import net.lugocorp.kingdom.game.Game;

/**
 * Handles arbitrary incoming Events on arbitrary members of some group
 */
public class StratifiedEventReceiver {
    private Map<String, EventReceiver> receivers = new HashMap<>();

    /**
     * Registers a new EventHandler to some name and channel on this EventReceiver
     */
    public void addEventHandler(String name, String channel, EventHandler handler) {
        if (!this.receivers.containsKey(name)) {
            this.receivers.put(name, new EventReceiver());
        }
        this.receivers.get(name).addEventHandler(channel, handler);
    }

    /**
     * Runs the relevant EventHandler logic for a given name and Event
     */
    public void handle(Game g, String name, Event e) {
        if (!this.receivers.containsKey(name)) {
            return;
        }
        this.receivers.get(name).handle(g, e);
    }
}
