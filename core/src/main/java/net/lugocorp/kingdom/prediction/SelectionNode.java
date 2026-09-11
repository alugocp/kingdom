package net.lugocorp.kingdom.prediction;
import net.lugocorp.kingdom.gameplay.events.Event;
import net.lugocorp.kingdom.math.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A node in the SelectionTree which allows us to analyze the EventLog within
 * the context of different select() options
 */
public class SelectionNode {
    private final Map<Point, SelectionNode> children = new HashMap<>();
    private final List<Event> log = new ArrayList<>();
    private final int eventHandle;

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
     * Returns the child node at the given Point
     */
    SelectionNode getChild(Point p) {
        return this.children.get(p);
    }
}
