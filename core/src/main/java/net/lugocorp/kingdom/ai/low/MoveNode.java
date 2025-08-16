package net.lugocorp.kingdom.ai.low;
import net.lugocorp.kingdom.ai.ActionResult;
import net.lugocorp.kingdom.ai.LowNode;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.math.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

/**
 * This LowNode tells the Actor's Unit to move somewhere
 */
public class MoveNode extends LowNode {
    private final Point p;

    public MoveNode(Point p, Unit unit, Function<LowNode, Float> calculateScore) {
        super(unit, calculateScore);
        this.p = p;
    }

    /** {@inheritdoc} */
    @Override
    public ActionResult act(GameView view) {
        Set<Point> options = this.unit.getMoveTargets(view);
        if (options.contains(this.p)) {
            this.unit.move(view.game, this.p);
            return ActionResult.POP;
        }
        return ActionResult.POP_ALL;
    }

    /** {@inheritdoc} */
    @Override
    public List<LowNode> generateTrees() {
        return new ArrayList<>();
    }
}
