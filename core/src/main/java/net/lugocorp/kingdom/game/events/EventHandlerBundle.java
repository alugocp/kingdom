package net.lugocorp.kingdom.game.events;
import net.lugocorp.kingdom.ui.views.GameView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Handles arbitrary incoming Events on arbitrary members of some group
 */
public class EventHandlerBundle<T extends EventReceiver> {
    private Map<String, List<EventHandler<T>>> handlers = new HashMap<>();

    /**
     * Formats the lookup key used in this.handlers
     */
    private String getKey(String stratifier, String channel) {
        return String.format("%s.%s", stratifier, channel);
    }

    /**
     * Returns all registered stratifiers associated with Generate*Event channels
     */
    public Set<String> getStratifiers() {
        Pattern p = Pattern.compile("(.+)\\.Generate[\\w]+Event", Pattern.CASE_INSENSITIVE);
        Set<String> stratifiers = new HashSet<>();
        for (String key : this.handlers.keySet()) {
            Matcher m = p.matcher(key);
            if (m.matches()) {
                stratifiers.add(m.group(1));
            }
        }
        return stratifiers;
    }

    /**
     * Returns true if the EventReceiver with the given stratifier has an
     * EventHandler that listens on the given channel
     */
    public boolean hasEventHandler(String stratifier, String channel) {
        return this.handlers.containsKey(this.getKey(stratifier, channel));
    }

    /**
     * Registers a new EventHandler to some name and channel on this EventReceiver
     */
    public void addEventHandler(String stratifier, String channel, EventHandler<T> handler) {
        String key = this.getKey(stratifier, channel);
        if (!this.handlers.containsKey(key)) {
            this.handlers.put(key, new ArrayList<EventHandler<T>>());
        }
        this.handlers.get(key).add(handler);
    }

    /**
     * Runs the relevant EventHandler logic for a given name and Event
     */
    public void handle(GameView view, T receiver, Event e) {
        String key = this.getKey(receiver.getStratifier(), e.channel);
        if (!this.handlers.containsKey(key)) {
            System.err.println(String.format("Did not handle %s event for %s", e.channel, receiver.getStratifier()));
            return;
        }
        for (EventHandler<T> handler : this.handlers.get(key)) {
            handler.handle(view, receiver, e);
        }
    }
}
