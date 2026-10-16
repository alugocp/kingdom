package net.lugocorp.kingdom.gameplay.events;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.SideEffect;
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
    private Map<String, List<EventHandler<T, Event>>> handlers = new HashMap<>();
    private Map<String, EventHandler<T, Event>> defaults = new HashMap<>();

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
     * Returns all registered Event channels for the given stratifier
     */
    public Set<String> getChannels(String stratifier) {
        final Pattern p = Pattern.compile(String.format("%s\\.([A-Z]+Event)", stratifier), Pattern.CASE_INSENSITIVE);
        final Set<String> channels = new HashSet<>();
        for (String key : this.handlers.keySet()) {
            final Matcher m = p.matcher(key);
            if (m.matches()) {
                channels.add(m.group(1));
            }
        }
        return channels;
    }

    /**
     * Returns true if the EventReceiver with the given stratifier has an
     * EventHandler that listens on the given channel
     */
    public <E extends Event> boolean hasEventHandler(String stratifier, String channel) {
        return this.handlers.containsKey(this.getKey(stratifier, channel)) || this.defaults.containsKey(channel);
    }

    /**
     * Calls into hasEventHandler() with an Event class
     */
    public <E extends Event> boolean hasEventHandler(String stratifier, Class<E> eventClass) {
        return this.hasEventHandler(stratifier, eventClass.getSimpleName());
    }

    /**
     * Registers a new EventHandler to some name and channel on this EventReceiver
     */
    public <E extends Event> void addEventHandler(String stratifier, String channel, EventHandler<T, E> handler) {
        String key = this.getKey(stratifier, channel);
        if (!this.handlers.containsKey(key)) {
            this.handlers.put(key, new ArrayList<EventHandler<T, Event>>());
        }
        this.handlers.get(key).add((EventHandler<T, Event>) handler);
    }

    /**
     * Calls into this.addEventHandler() using an Event class
     */
    public <E extends Event> void addEventHandler(String stratifier, Class<E> channel, EventHandler<T, E> handler) {
        this.addEventHandler(stratifier, channel.getSimpleName(), handler);
    }

    /**
     * Registers a new default EventHandler to some channel on this EventReceiver
     */
    public <E extends Event> void setDefaultHandler(String channel, EventHandler<T, E> handler) {
        // TODO find a way to remove this
        this.defaults.put(channel, (EventHandler<T, Event>) handler);
    }

    /**
     * Runs the relevant EventHandler logic for a given name and Event
     */
    public <E extends Event> SideEffect handle(GameView view, T receiver, E e) {
        String key = this.getKey(receiver.getStratifier(), e.channel);
        boolean hasHandler = this.handlers.containsKey(key);
        boolean hasDefault = this.defaults.containsKey(e.channel);
        if (e.panic && !(hasHandler || hasDefault)) {
            throw new RuntimeException(
                    String.format("Did not handle %s event for %s", e.channel, receiver.getStratifier()));
        }
        final SideEffect effects = new SideEffect();
        if (hasHandler) {
            for (EventHandler<T, Event> handler : this.handlers.get(key)) {
                effects.add(handler.handle(view, receiver, e));
            }
        } else if (hasDefault) {
            return this.defaults.get(e.channel).handle(view, receiver, e);
        }
        return effects;
    }
}
