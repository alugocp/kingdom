package net.lugocorp.kingdom.ai.plans;
import net.lugocorp.kingdom.ai.action.ActionResult;
import net.lugocorp.kingdom.ai.action.PlanNode;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.math.Point;
import java.util.Set;

/**
 * This PlanNode tells the Actor's Unit to move somewhere
 */
public class MoveNode extends PlanNode {
    public final Point dest;

    public MoveNode(Unit unit, Point dest) {
        super(unit);
        this.dest = dest;
    }

    /** {@inheritdoc} */
    @Override
    public ActionResult act(GameView view) {
        Set<Point> options = this.unit.movement.getTargets(view);
        if (options.contains(this.dest)) {
            this.unit.movement.move(view, this.dest).execute();
            return ActionResult.POP;
        }
        return ActionResult.POP_ALL;
    }
}
