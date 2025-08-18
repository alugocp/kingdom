package net.lugocorp.kingdom.game.events;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.code.Lambda;
import net.lugocorp.kingdom.utils.code.SideEffect;
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
    private final List<EventHandler<T>> empty = new ArrayList<>(0);
    private Map<String, List<EventHandler<T>>> handlers = new HashMap<>();
    private Map<String, EventHandler<T>> defaults = new HashMap<>();

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
        return this.handlers.containsKey(this.getKey(stratifier, channel)) || this.defaults.containsKey(channel);
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
     * Registers a new default EventHandler to some channel on this EventReceiver
     */
    public void setDefaultHandler(String channel, EventHandler<T> handler) {
        this.defaults.put(channel, handler);
    }

    /**
     * Runs the relevant EventHandler logic for a given name and Event
     */
    public SideEffect handle(GameView view, T receiver, Event e) {
        String key = this.getKey(receiver.getStratifier(), e.channel);
        boolean hasHandler = this.handlers.containsKey(key);
        boolean hasDefault = this.defaults.containsKey(e.channel);
        if (e.panic && !(hasHandler || hasDefault)) {
            throw new RuntimeException(
                    String.format("Did not handle %s event for %s", e.channel, receiver.getStratifier()));
        }
        if (hasHandler) {
            return SideEffect.all(
                    Lambda.map((EventHandler<T> handler) -> handler.handle(view, receiver, e), this.handlers.get(key)));
        }
        if (hasDefault) {
            return this.defaults.get(e.channel).handle(view, receiver, e);
        }
        return SideEffect.none;
    }
}
