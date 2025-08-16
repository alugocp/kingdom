package net.lugocorp.kingdom.ai.goals;
import net.lugocorp.kingdom.ai.Goal;
import net.lugocorp.kingdom.ai.PlanNode;
import net.lugocorp.kingdom.ai.plans.ScoutNode;
import net.lugocorp.kingdom.game.model.Unit;
import java.util.ArrayList;
import java.util.List;

/**
 * This class tells the Actor to explore the map
 */
public class ExploreMap implements Goal {

    /** {@inheritdoc} */
    @Override
    public List<PlanNode> suggestPlanNodes(Unit u) {
        List<PlanNode> list = new ArrayList();
        list.add(new ScoutNode(u, this::scoreNode));
        return list;
    }

    /** {@inheritdoc} */
    @Override
    public float scoreNode(PlanNode leaf) {
        // TODO implement me
        return 1f;
    }
}
