package net.lugocorp.kingdom.game.events;
import java.util.ArrayList;
import java.util.List;

/**
 * Captures all Events that get processed by EventReceivers so the AI can assess
 * the possible consequences of its actions.
 */
public class CapturedEvents {
    private static boolean active = false;
    static final List<Event> log = new ArrayList<>();

    /**
     * Adds an Event to the log
     */
    static void capture(Event e) {
        if (!CapturedEvents.active) {
            throw new RuntimeException("Cannot capture Events on an inactive log");
        }
        CapturedEvents.log.add(e);
    }

    /**
     * Returns true if the log is active
     */
    static boolean isActive() {
        return CapturedEvents.active;
    }

    /**
     * Turns on capturing
     */
    public static void on() {
        CapturedEvents.active = true;
    }

    /**
     * Turns off capturing and returns a copy of the log
     */
    public static List<Event> off() {
        List<Event> copy = new ArrayList<>();
        for (Event e : CapturedEvents.log) {
            copy.add(e);
        }
        CapturedEvents.active = false;
        CapturedEvents.log.clear();
        return copy;
    }
}
