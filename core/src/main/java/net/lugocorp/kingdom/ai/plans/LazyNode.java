package net.lugocorp.kingdom.ai.plans;
import net.lugocorp.kingdom.ai.ActionResult;
import net.lugocorp.kingdom.ai.PlanNode;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.ui.views.GameView;

/**
 * This PlanNode tells the Actor's Unit to do nothing
 */
public class LazyNode extends PlanNode {

    public LazyNode(Unit unit) {
        super(unit);
    }

    /** {@inheritdoc} */
    @Override
    public ActionResult act(GameView view) {
        return ActionResult.POP;
    }
}
