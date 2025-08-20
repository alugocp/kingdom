package net.lugocorp.kingdom.ai.action;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.ui.views.GameView;
import java.util.Optional;

/**
 * This class forms part of the Actor's strategic details
 */
public abstract class PlanNode {
    private Optional<PlanNode> child = Optional.empty();
    public final Unit unit;

    public PlanNode(Unit unit) {
        this.unit = unit;
    }

    /**
     * Runs the PlanNode's logic to command a Unit
     */
    public abstract ActionResult act(GameView view);

    /**
     * Returns this PlanNode's child (if there is one)
     */
    public final Optional<PlanNode> getChild() {
        return this.child;
    }

    /**
     * Sets this PlanNode's child
     */
    public final void setChild(PlanNode n) {
        this.child = Optional.of(n);
    }
}
