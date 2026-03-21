package net.lugocorp.kingdom.utils;
import net.lugocorp.kingdom.gameplay.events.Event;
import java.util.ArrayList;
import java.util.List;

/**
 * SideEffects are callables that are designed for the AI system. The game's AI
 * needs to "look ahead" into the game to predict its best possible move. It
 * does this by triggering an Event and assessing the Events that cascade
 * afterwards. Some EventHandlers mutate game state. They should only do this
 * inside a SideEffect object, otherwise AI predictions would affect the actual
 * game. Here are rules for SideEffects: 1) Methods that mutate state and call a
 * potentially mutating event must use SideEffect; 2) Methods that mutate state
 * but do not call mutating events should be called within SideEffect; 3)
 * Methods that return SideEffect called inside another SideEffect method should
 * have their result propagated
 */
public class SideEffect {
    private final List<Runnable> actions = new ArrayList<>();
    private final List<Event> events = new ArrayList<>();

    /**
     * Registers an Event with this SideEffect
     */
    public SideEffect add(Event event) {
        this.events.add(event);
        return this;
    }

    /**
     * Registers an action with this SideEffect
     */
    public SideEffect add(Runnable action) {
        this.actions.add(action);
        return this;
    }

    /**
     * Registers another SideEffect's Events and actions with this SideEffect
     */
    public SideEffect add(SideEffect child) {
        this.actions.addAll(child.actions);
        this.events.addAll(child.events);
        return this;
    }

    /**
     * Triggers the SideEffect's actions
     */
    public void execute() {
        for (Runnable action : this.actions) {
            action.run();
        }
    }
}
