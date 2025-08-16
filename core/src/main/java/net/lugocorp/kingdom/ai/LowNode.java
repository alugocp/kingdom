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
public abstract class LowNode {
    private final Supplier<Float> calculateScore;
    private Optional<Float> score = Optional.empty();
    private Optional<LowNode> child = Optional.empty();
    protected final Unit unit;

    public LowNode(Unit unit, Function<LowNode, Float> calculateScore) {
        this.calculateScore = () -> calculateScore.apply(this);
        this.unit = unit;
    }

    /**
     * Expands this LowNode into several possible paths
     */
    public abstract List<LowNode> generateTrees();

    /**
     * Runs the LowNode's logic to command a Unit
     */
    public abstract ActionResult act(GameView view);

    /**
     * Returns the score calculated for this LowNode
     */
    public float getScore() {
        if (!this.score.isPresent()) {
            this.score = Optional.of(this.calculateScore.get());
        }
        return this.score.get();
    }

    /**
     * Returns this LowNode's child (if there is one)
     */
    public Optional<LowNode> getChild() {
        return this.child;
    }
}
