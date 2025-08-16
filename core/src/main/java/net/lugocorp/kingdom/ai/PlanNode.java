package net.lugocorp.kingdom.ai;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.ui.views.GameView;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * This class forms part of the Actor's strategic details
 */
public abstract class PlanNode {
    private final Supplier<Float> calculateScore;
    private Optional<Float> score = Optional.empty();
    private Optional<PlanNode> child = Optional.empty();
    protected final Unit unit;

    public PlanNode(Unit unit, Function<PlanNode, Float> calculateScore) {
        this.calculateScore = () -> calculateScore.apply(this);
        this.unit = unit;
    }

    /**
     * Expands this PlanNode into several possible paths
     */
    public abstract List<PlanNode> generateTrees();

    /**
     * Runs the PlanNode's logic to command a Unit
     */
    public abstract ActionResult act(GameView view);

    /**
     * Returns the score calculated for this PlanNode
     */
    public float getScore() {
        if (!this.score.isPresent()) {
            this.score = Optional.of(this.calculateScore.get());
        }
        return this.score.get();
    }

    /**
     * Returns this PlanNode's child (if there is one)
     */
    public Optional<PlanNode> getChild() {
        return this.child;
    }
}
