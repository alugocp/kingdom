package net.lugocorp.kingdom.prediction;
import net.lugocorp.kingdom.gameplay.events.Event;
import net.lugocorp.kingdom.math.Path;
import net.lugocorp.kingdom.math.Point;
import net.lugocorp.kingdom.utils.Lambda;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A node in the SelectionTree which allows us to analyze the EventLog within
 * the context of different select() options
 */
class SelectionNode {
    private final int eventHandle;
    final Map<Point, SelectionNode> children = new HashMap<>();
    final List<Event> log = new ArrayList<>();

    public SelectionNode(int eventHandle) {
        this.eventHandle = eventHandle;
    }

    /**
     * Adds a child node at the given select() option
     */
    void add(Point p, SelectionNode node) {
        this.children.put(p, node);
    }

    /**
     * Solidifies the section of the EventLog associated with this SelectionNode
     */
    SelectionNode recordEvents() {
        this.log.addAll(EventLog.getEvents(this.eventHandle));
        return this;
    }

    /**
     * Returns a set of Paths to each leaf node beneath this instance
     */
    Set<Path> getLeafPaths() {
        if (this.children.size() == 0) {
            final Set<Path> base = new HashSet<Path>();
            base.add(new Path());
            return base;
        }
        return Lambda.flatten(Lambda.map(
                (Point p) -> Lambda.map((Path suffix) -> suffix.prepend(p), this.children.get(p).getLeafPaths()),
                this.children.keySet()));
    }
}
