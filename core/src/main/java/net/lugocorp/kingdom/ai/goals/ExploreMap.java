package net.lugocorp.kingdom.ai.goals;
import net.lugocorp.kingdom.ai.action.Goal;
import net.lugocorp.kingdom.ai.action.GoalUtils;
import net.lugocorp.kingdom.ai.action.Plan;
import net.lugocorp.kingdom.ai.action.PlanNode;
import net.lugocorp.kingdom.ai.memory.MemoryCell;
import net.lugocorp.kingdom.ai.memory.MemoryMap;
import net.lugocorp.kingdom.ai.plans.MoveNode;
import net.lugocorp.kingdom.game.glyph.Glyph;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.game.player.CompPlayer;
import net.lugocorp.kingdom.math.Hexagons;
import net.lugocorp.kingdom.math.Point;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.Lambda;
import java.util.Optional;
import java.util.Set;

/**
 * This class tells the Actor to explore the map
 */
public class ExploreMap extends Goal {

    /** {@inheritdoc} */
    @Override
    public Optional<Plan> suggestPlan(GameView view, Unit u) {
        final Set<Point> targets = GoalUtils.getMoveTargets(view, u, 4);
        return this.getBestPlan(Lambda.map((Point p) -> this.wrapPlanNode(view, new MoveNode(u, p)), targets));
    }

    /** {@inheritdoc} */
    @Override
    protected float getScore(GameView view, PlanNode root) {
        final MemoryMap memory = ((CompPlayer) root.unit.getLeader().get()).memory;
        final Unit unit = ((MoveNode) root).unit;
        final Point dest = ((MoveNode) root).dest;
        final int vision = unit.vision.get(view, unit.getLeader().get(), unit);
        float score = this.subscore(view, memory, dest);
        for (Point p : Hexagons.getNeighbors(dest, vision)) {
            score += this.subscore(view, memory, p);
        }
        return score / (float) Hexagons.tilesWithinRadius(vision);
    }

    /**
     * Returns a component of the final score for a given Point
     */
    private float subscore(GameView view, MemoryMap memory, Point p) {
        final Optional<MemoryCell> cell = memory.getCell(p);
        if (cell.map((MemoryCell c) -> !c.isVisible()).orElse(false)) {
            return cell.get().wasEverVisible() ? 0.5f : 1f;
        }
        return 0f;
    }

    /** {@inheritdoc} */
    @Override
    public boolean likesGlyph(Glyph glyph) {
        return glyph == Glyph.TRADE;
    }

    /** {@inheritdoc} */
    @Override
    public boolean likesEventChannel(String channel) {
        return channel.equals("GetVisionEvent");
    }
}
