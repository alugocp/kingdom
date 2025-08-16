package net.lugocorp.kingdom.ai.plans;
import net.lugocorp.kingdom.ai.ActionResult;
import net.lugocorp.kingdom.ai.PlanNode;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.math.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

/**
 * This PlanNode tells the Actor's Unit to move somewhere
 */
public class MoveNode extends PlanNode {
    private final Point p;

    public MoveNode(Point p, Unit unit, Function<PlanNode, Float> calculateScore) {
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
    public List<PlanNode> generateTrees() {
        return new ArrayList<>();
    }
}
