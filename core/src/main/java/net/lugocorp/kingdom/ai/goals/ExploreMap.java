package net.lugocorp.kingdom.ai.goals;
import net.lugocorp.kingdom.ai.Goal;
import net.lugocorp.kingdom.ai.Plan;
import net.lugocorp.kingdom.ai.PlanNode;
import net.lugocorp.kingdom.ai.plans.MoveNode;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.utils.math.Point;

/**
 * This class tells the Actor to explore the map
 */
public class ExploreMap extends Goal {

    /** {@inheritdoc} */
    @Override
    public Plan suggestPlan(Unit u) {
        Point p = new Point(u.getX() - 1, u.getY());
        PlanNode root = new MoveNode(u, p);
        return this.wrapPlanNode(root);
    }

    /** {@inheritdoc} */
    @Override
    protected float getScore(PlanNode root) {
        // TODO implement me
        return 1f;
    }
}
