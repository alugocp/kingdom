package net.lugocorp.kingdom.prediction;
import net.lugocorp.kingdom.gameplay.events.Event;
import net.lugocorp.kingdom.math.Path;
import net.lugocorp.kingdom.math.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * This class tracks boundaries in the EventLog based on branching paths of
 * possible selection options (see CompPlayer.select())
 */
public class SelectionTree {
    private final SelectionNode root = new SelectionNode(EventLog.getHandle());
    private Path pointer = new Path();

    /**
     * Retrieves the SelectionNode at the given Path in the tree
     */
    private SelectionNode getNode(Path path) {
        SelectionNode node = this.root;
        for (Point p : path) {
            node = node.children.get(p);
        }
        return node;
    }

    /**
     * Adds a new SelectionNode as a child of the current node and moves our pointer
     * to it
     */
    public void add(Point p, int eventHandle) {
        final SelectionNode node = new SelectionNode(eventHandle);
        this.getNode(this.pointer).recordEvents().add(p, node);
        this.pointer.add(p);
    }

    /**
     * Moves our pointer up one level in the tree towards the root
     */
    public void moveUp() {
        this.getNode(this.pointer).recordEvents();
        this.pointer.pop();
    }

    /**
     * Iterate through the leaf nodes, calculate their Event logs, and generate
     * Predictions based on that
     */
    public void iteratePredictions(Consumer<Prediction> callback) {
        for (Path path : this.root.getLeafPaths()) {
            callback.accept(new Prediction(path, this.getPathLogs(path)));
        }
    }

    /**
     * Returns the full Event logs for the given Path through this instance
     */
    private List<Event> getPathLogs(Path path) {
        final List<Event> log = new ArrayList<>();
        SelectionNode node = this.root;
        log.addAll(node.log);
        for (Point p : path) {
            node = node.children.get(p);
            log.addAll(node.log);
        }
        return log;
    }
}
