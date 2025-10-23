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
import net.lugocorp.kingdom.utils.math.Hexagons;
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
        final Set<Point> targets = Hexagons.getNeighbors(u.getPoint(), 4);
        return this.getBestPlan(Lambda.map((Point p) -> this.wrapPlanNode(view, new MoveNode(u, p)), targets));
    }

    /** {@inheritdoc} */
    @Override
    protected float getScore(GameView view, PlanNode root) {
        final MemoryMap memory = ((CompPlayer) root.unit.getLeader().get()).memory;
        final Point dest = ((MoveNode) root).dest;
        final Optional<MemoryCell> cell = memory.getCell(dest);
        if (cell.map((MemoryCell c) -> !c.getBuilding().isPresent() || c.getOwner().equals(root.unit.getLeader()))
                .orElse(true)) {
            return 0f;
        }
        final Building b = view.game.generator.building(cell.flatMap((MemoryCell c) -> c.getBuilding()).get(), 0, 0);
        return b.isActive() ? 0f : 1f;
    }
}
