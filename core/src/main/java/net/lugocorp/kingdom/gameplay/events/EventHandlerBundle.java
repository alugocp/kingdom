package net.lugocorp.kingdom.gameplay.events;
import net.lugocorp.kingdom.builtin.Events;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.Lambda;
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
    public void addEventHandler(String stratifier, String channel, EventHandler<T> handler) {
        String key = this.getKey(stratifier, channel);
        if (!this.handlers.containsKey(key)) {
            this.handlers.put(key, new ArrayList<EventHandler<T>>());
        }
        this.handlers.get(key).add(handler);
    }

    /**
     * Calls into this.addEventHandler() using an Event class
     */
    public <E extends Event> void addEventHandler(String stratifier, Class<E> eventClass,
            SingleEventHandler<T, E> handler) {
        this.addEventHandler(stratifier, eventClass.getSimpleName(),
                (GameView view, T receiver, Event event) -> handler.handle(view, receiver, (E) event));
    }

    /**
     * Calls into this.addEventHandler() using an Event class
     */
    public void addEventHandler(String stratifier, String channel,
            SingleEventHandler<T, Events.RepeatedEvent> handler) {
        this.addEventHandler(stratifier, channel, (GameView view, T receiver, Event event) -> handler.handle(view,
                receiver, (Events.RepeatedEvent) event));
    }

    /**
     * Registers a new default EventHandler to some channel on this EventReceiver
     */
    public void setDefaultHandler(String channel, EventHandler<T> handler) {
        this.defaults.put(channel, handler);
    }

    /**
     * Calls into setDefaultHandler() with an Event class
     */
    public <E extends Event> void setDefaultHandler(Class<E> channel, SingleEventHandler<T, E> handler) {
        this.setDefaultHandler(channel.getSimpleName(),
                (GameView view, T receiver, Event event) -> handler.handle(view, receiver, (E) event));
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
        final SideEffect effects = new SideEffect();
        if (hasHandler) {
            for (EventHandler<T> handler : this.handlers.get(key)) {
                effects.add(handler.handle(view, receiver, e));
            }
        }
        if (hasDefault) {
            return this.defaults.get(e.channel).handle(view, receiver, e);
        }
        return effects;
    }
}
