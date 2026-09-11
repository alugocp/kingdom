package net.lugocorp.kingdom.prediction;
import net.lugocorp.kingdom.gameplay.events.Event;
import net.lugocorp.kingdom.gameplay.events.EventReceiver;
import net.lugocorp.kingdom.utils.Log;
import java.util.ArrayList;
import java.util.List;

/**
 * Stores a running log of all Events that get fired in a turn
 */
public abstract class EventLog {
    private static final List<Event> events = new ArrayList<>();

    /**
     * Resets the state of the EventLog
     */
    public static void reset() {
        EventLog.events.clear();
    }

    /**
     * Adds an incoming Event to the log
     */
    public static void log(Event e, EventReceiver receiver) {
        Log.log(String.format("%s received %s", receiver.getStratifier(), e.getClass().getSimpleName()));
        EventLog.events.add(e);
    }

    /**
     * Returns a handle ID that allows us to retrieve Events after a certain point
     * later
     */
    public static int getHandle() {
        return EventLog.events.size();
    }

    /**
     * Retrieves all Events after the given handle
     */
    public static List<Event> getEvents(int handle) {
        final List<Event> copy = new ArrayList<>();
        for (int a = handle; a < EventLog.events.size(); a++) {
            copy.add(EventLog.events.get(a));
        }
        return copy;
    }
}
