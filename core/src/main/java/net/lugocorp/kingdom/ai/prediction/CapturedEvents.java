package net.lugocorp.kingdom.ai.prediction;
import net.lugocorp.kingdom.gameplay.events.Event;
import net.lugocorp.kingdom.math.Point;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Captures all Events that get processed by EventReceivers so the AI can assess
 * the possible consequences of its actions.
 */
public class CapturedEvents {
    public static final CapturedEvents instance = new CapturedEvents();
    private Optional<Point> fakePoint = Optional.empty();
    private EventLog log = null;

    /**
     * Adds an Event to the log
     */
    public void capture(Event e) {
        if (!this.isActive()) {
            throw new RuntimeException("Cannot capture Events on an inactive log");
        }
        this.log.addEvent(e);
    }

    /**
     * Returns true if the log is active
     */
    public boolean isActive() {
        return this.log != null;
    }

    /**
     * Turns on capturing
     */
    public void on() {
        this.fakePoint = Optional.empty();
        this.log = new EventLog();
    }

    /**
     * Turns on capturing with the given EventLog
     */
    public void on(EventLog log) {
        this.log = log;
    }

    /**
     * Turns off capturing and returns the log
     */
    public EventLog off() {
        EventLog results = this.log;
        this.log = null;
        return results;
    }

    /**
     * Takes the current captured Events here and splits them off for each possible
     * Point that could be chosen. Events that result from these choices are
     * assigned to the correct Path.
     */
    public void split(Iterable<Point> options, Consumer<Point> lambda) {
        EventLog parent = this.off();
        for (Point p : options) {
            EventLog log = new EventLog();
            this.on(log);
            lambda.accept(p);
            this.off();
            parent.addPotentialBranches(p, log);
        }
        parent.foldUnincorporatedBranches();
        this.on(parent);
    }

    /**
     * Gets the current fake Point
     */
    public Optional<Point> getFakePoint() {
        return this.fakePoint;
    }

    /**
     * Sets the current fake Point
     */
    public void setFakePoint(Point p) {
        this.fakePoint = Optional.of(p);
    }

    /**
     * Removes the current fake Point
     */
    public void clearFakePoint() {
        this.fakePoint = Optional.empty();
    }
}
