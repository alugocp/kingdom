package net.lugocorp.kingdom.ai.prediction;
import net.lugocorp.kingdom.game.events.Event;
import net.lugocorp.kingdom.utils.math.Path;
import net.lugocorp.kingdom.utils.math.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Stores all possible resulting logs from some starting point
 */
public class EventLog {
    private final Map<Path, List<Event>> unincorporated = new HashMap<>();
    private final Map<Path, List<Event>> events = new HashMap<>();

    public EventLog() {
        this.events.put(new Path(), new ArrayList<Event>());
    }

    /**
     * Returns true if there are multiple keys present in this log
     */
    public boolean hasBranches() {
        return this.events.keySet().size() > 1;
    }

    /**
     * Adds a new Event to each path in this log
     */
    public void addEvent(Event e) {
        for (List<Event> list : this.events.values()) {
            list.add(e);
        }
    }

    /**
     * Returns a Set of the Paths that cause our alternate Event logs
     */
    public Set<Path> getTargetPaths() {
        return this.events.keySet();
    }

    /**
     * Returns the Event log associated with the given target Path
     */
    public List<Event> getEvents(Path p) {
        return this.events.get(p);
    }

    /**
     * Saves another EventLog to be folded into this one under a given single-Point
     * Path
     */
    public void addPotentialBranches(Point p, EventLog log) {
        for (Map.Entry<Path, List<Event>> entry : log.events.entrySet()) {
            this.unincorporated.put(entry.getKey().prepend(p), entry.getValue());
        }
    }

    /**
     * Adopts unincorporated EventLogs into this one as branching Paths
     */
    public void foldUnincorporatedBranches() {
        if (this.unincorporated.size() == 0) {
            return;
        }
        final Map<Path, List<Event>> log = new HashMap<>();
        log.putAll(this.events);
        this.events.clear();
        for (Map.Entry<Path, List<Event>> entry : log.entrySet()) {
            for (Map.Entry<Path, List<Event>> suffix : this.unincorporated.entrySet()) {
                Path key = new Path();
                List<Event> value = new ArrayList<>();
                key.concat(entry.getKey());
                key.concat(suffix.getKey());
                value.addAll(entry.getValue());
                value.addAll(suffix.getValue());
                this.events.put(key, value);
            }
        }
    }
}
