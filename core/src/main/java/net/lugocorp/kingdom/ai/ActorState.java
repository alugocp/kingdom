package net.lugocorp.kingdom.ai;
import net.lugocorp.kingdom.prediction.SelectionTree;
import java.util.Optional;

/**
 * Helper class for Actor supporting its deliberation/enactment states
 */
public class ActorState {
    private boolean deliberating = false;
    private Optional<SelectionTree> tree = Optional.empty();
    private Optional<Behavior> behavior = Optional.empty();

    /**
     * Switches the state to deliberation
     */
    public SelectionTree deliberate() {
        final SelectionTree tree = new SelectionTree();
        this.deliberating = true;
        this.behavior = Optional.empty();
        this.tree = Optional.of(tree);
        return tree;
    }

    /**
     * Switches the state to enactment
     */
    public void enact(Behavior behavior) {
        this.deliberating = false;
        this.behavior = Optional.of(behavior);
        this.tree = Optional.empty();
    }

    /**
     * Returns true if this Actor is currently making Decisions (or false if it is
     * ready to act on those Decisions)
     */
    public boolean isDeliberating() {
        return this.deliberating;
    }

    /**
     * Returns the Behavior that is currently being processed (if one exists)
     */
    public Optional<Behavior> getCurrentBehavior() {
        return this.behavior;
    }

    /**
     * Returns the active SelectionTree
     */
    public Optional<SelectionTree> getSelectionTree() {
        return this.tree;
    }
}
