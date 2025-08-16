package net.lugocorp.kingdom.ai.plans;
import net.lugocorp.kingdom.ai.ActionResult;
import net.lugocorp.kingdom.ai.PlanNode;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.math.Point;
import java.util.Set;

/**
 * This PlanNode tells the Actor's Unit to move somewhere
 */
public class MoveNode extends PlanNode {
    private final Point p;

    public MoveNode(Unit unit, Point p) {
        super(unit);
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
}
