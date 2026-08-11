package net.lugocorp.kingdom.ai.goals;
import net.lugocorp.kingdom.ai.action.Goal;
import net.lugocorp.kingdom.ai.action.GoalUtils;
import net.lugocorp.kingdom.ai.action.Plan;
import net.lugocorp.kingdom.ai.action.PlanNode;
import net.lugocorp.kingdom.ai.action.Priority;
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
    protected Priority getScore(GameView view, PlanNode root) {
        final MemoryMap memory = ((CompPlayer) root.unit.getLeader().get()).memory;
        final Unit unit = ((MoveNode) root).unit;
        final Point dest = ((MoveNode) root).dest;
        final int vision = unit.vision.get(view, unit.getLeader().get(), unit);
        final Set<Point> points = Hexagons.getNeighbors(dest, vision);
        points.add(dest);

        // Calculate how many Tiles will be revealed or revisited
        int revisited = 0;
        int revealed = 0;
        for (Point p : points) {
            final Optional<MemoryCell> cell = memory.getCell(p);
            if (cell.map((MemoryCell c) -> !c.isVisible()).orElse(false)) {
                if (cell.get().wasEverVisible()) {
                    revisited++;
                } else {
                    revealed++;
                }
            }
        }

        // Determine Priority based on aforementioned stats
        if (revealed > 7) {
            return Priority.OPTIMAL;
        }
        if (revealed > 4 || revisited > 7) {
            return Priority.GOOD_IDEA;
        }
        return Priority.NEUTRAL;
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
