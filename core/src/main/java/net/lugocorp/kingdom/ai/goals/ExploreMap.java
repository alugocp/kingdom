package net.lugocorp.kingdom.ai.goals;
import net.lugocorp.kingdom.ai.action.Goal;
import net.lugocorp.kingdom.ai.action.Plan;
import net.lugocorp.kingdom.ai.action.PlanNode;
import net.lugocorp.kingdom.ai.memory.MemoryCell;
import net.lugocorp.kingdom.ai.memory.MemoryMap;
import net.lugocorp.kingdom.ai.plans.MoveNode;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.game.player.CompPlayer;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.code.Lambda;
import net.lugocorp.kingdom.utils.math.Hexagons;
import net.lugocorp.kingdom.utils.math.Point;
import java.util.Optional;
import java.util.Set;

/**
 * This class tells the Actor to explore the map
 */
public class ExploreMap extends Goal {

    /** {@inheritdoc} */
    @Override
    public Optional<Plan> suggestPlan(GameView view, Unit u) {
        Set<Point> targets = u.getMoveTargets(view);
        return this.getBestPlan(Lambda.map((Point p) -> this.wrapPlanNode(view, new MoveNode(u, p)), targets));
    }

    /** {@inheritdoc} */
    @Override
    protected float getScore(GameView view, PlanNode root) {
        MemoryMap memory = ((CompPlayer) root.unit.getLeader().get()).memory;
        Point dest = ((MoveNode) root).dest;
        Set<Point> adj = Hexagons.getNeighbors(dest, 1);
        float score = this.subscore(view, memory, dest);
        for (Point p : Hexagons.getNeighbors(dest, 1)) {
            score += this.subscore(view, memory, p);
        }
        return score / 7f;
    }

    /**
     * Returns a component of the final score for a given Point
     */
    private float subscore(GameView view, MemoryMap memory, Point p) {
        if (!view.game.world.isInBounds(p)) {
            return 0f;
        }
        MemoryCell cell = memory.getCell(p);
        if (!cell.isVisible()) {
            return cell.wasEverVisible() ? 0.5f : 1f;
        }
        return 0f;
    }
}
