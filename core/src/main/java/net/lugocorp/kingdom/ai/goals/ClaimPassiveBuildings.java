package net.lugocorp.kingdom.ai.goals;
import net.lugocorp.kingdom.ai.action.Goal;
import net.lugocorp.kingdom.ai.action.Plan;
import net.lugocorp.kingdom.ai.action.PlanNode;
import net.lugocorp.kingdom.ai.memory.MemoryCell;
import net.lugocorp.kingdom.ai.memory.MemoryMap;
import net.lugocorp.kingdom.ai.plans.MoveNode;
import net.lugocorp.kingdom.game.model.Building;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.game.player.CompPlayer;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.code.Lambda;
import net.lugocorp.kingdom.utils.math.Point;
import java.util.Optional;
import java.util.Set;

/**
 * This class tells the Actor to claim Tiles with Glyphs
 */
public class ClaimPassiveBuildings extends Goal {

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
        MemoryCell cell = memory.getCell(dest);
        if (!cell.getBuilding().isPresent() || cell.getOwner().equals(root.unit.getLeader())) {
            return 0f;
        }
        Building b = view.game.generator.building(cell.getBuilding().get(), 0, 0);
        return b.isActive() ? 0f : 1f;
    }
}
