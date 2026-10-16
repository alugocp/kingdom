package net.lugocorp.kingdom.gameplay.events;
import net.lugocorp.kingdom.gameplay.events.contexts.GameViewContext;
import net.lugocorp.kingdom.utils.SideEffect;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Handles arbitrary incoming Events on an single type of EventReceiver
 */
public class EventHandlerBundle<T extends EventReceiver> {
    private final Map<String, List<EventHandler<Context, T, Event<Context>>>> handlers = new HashMap<>();
    private final Map<String, EventHandler<Context, T, Event<Context>>> defaults = new HashMap<>();
    private final Supplier<GameViewContext> getContext;

    EventHandlerBundle(Supplier<GameViewContext> getContext) {
        this.getContext = getContext;
    }

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
        final Pattern p = Pattern.compile("(.+)\\.Generate[\\w]+Event", Pattern.CASE_INSENSITIVE);
        final Set<String> stratifiers = new HashSet<>();
        for (String key : this.handlers.keySet()) {
            final Matcher m = p.matcher(key);
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
    public boolean hasEventHandler(String stratifier, String channel) {
        return this.handlers.containsKey(this.getKey(stratifier, channel)) || this.defaults.containsKey(channel);
    }

    /**
     * Calls into hasEventHandler() with an Event class
     */
    public <E extends Event<?>> boolean hasEventHandler(String stratifier, Class<E> eventClass) {
        return this.hasEventHandler(stratifier, eventClass.getSimpleName());
    }

    /**
     * Registers a new EventHandler to some name and channel on this EventReceiver
     */
    <C extends Context, E extends Event<C>> void addEventHandler(String stratifier, String channel,
            EventHandler<C, T, E> handler) {
        String key = this.getKey(stratifier, channel);
        if (!this.handlers.containsKey(key)) {
            this.handlers.put(key, new ArrayList<EventHandler<Context, T, Event<Context>>>());
        }
        this.handlers.get(key).add((EventHandler<Context, T, Event<Context>>) handler);
    }

    /**
     * Registers a new default EventHandler to some channel on this EventReceiver
     */
    public <C extends Context, E extends Event<C>> void setDefaultHandler(String channel,
            EventHandler<C, T, E> handler) {
        // TODO find a way to remove this
        this.defaults.put(channel, (EventHandler<Context, T, Event<Context>>) handler);
    }

    /**
     * Runs the relevant EventHandler logic for a given name and Event
     */
    public <C extends Context, E extends Event<C>> SideEffect handle(T receiver, E e) {
        final Context ctx = AllEventHandlers.instance.getContext();
        final String key = this.getKey(receiver.getStratifier(), e.channel);
        final boolean hasHandler = this.handlers.containsKey(key);
        final boolean hasDefault = this.defaults.containsKey(e.channel);
        if (e.panic && !(hasHandler || hasDefault)) {
            throw new RuntimeException(
                    String.format("Did not handle %s event for %s", e.channel, receiver.getStratifier()));
        }
        if (!ctx.canHandle(e)) {
            throw new RuntimeException(String.format("Cannot handle %s events under %s context", e.channel, ctx.name));
        }

        // Run the actual handler and collect side effects
        final SideEffect effects = new SideEffect();
        if (hasHandler) {
            for (EventHandler<Context, T, Event<Context>> handler : this.handlers.get(key)) {
                effects.add(handler.handle(ctx, receiver, (Event<Context>) e));
            }
        } else if (hasDefault) {
            return this.defaults.get(e.channel).handle(ctx, receiver, (Event<Context>) e);
        }
        return effects;
    }
}
