package net.lugocorp.kingdom.ai.goals;
import net.lugocorp.kingdom.ai.action.Goal;
import net.lugocorp.kingdom.ai.action.Plan;
import net.lugocorp.kingdom.ai.action.PlanNode;
import net.lugocorp.kingdom.ai.memory.MemoryCell;
import net.lugocorp.kingdom.ai.memory.MemoryMap;
import net.lugocorp.kingdom.ai.plans.MoveNode;
import net.lugocorp.kingdom.ai.wishlist.GlyphWishlist;
import net.lugocorp.kingdom.game.glyph.Glyph;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.game.player.CompPlayer;
import net.lugocorp.kingdom.math.Hexagons;
import net.lugocorp.kingdom.math.Point;
import net.lugocorp.kingdom.pathfinding.Pathfinder;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.Lambda;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * This class tells the Actor to claim Tiles with Glyphs
 */
public class ClaimGlyphs extends Goal {

    /** {@inheritdoc} */
    @Override
    public Optional<Plan> suggestPlan(GameView view, Unit u) {
        final Set<Point> targets = Hexagons.getNeighbors(u.getPoint(), 4);
        return this.getBestPlan(Lambda.map((Point p) -> this.wrapPlanNode(view, new MoveNode(u, p)), targets));
    }

    /** {@inheritdoc} */
    @Override
    protected float getScore(GameView view, PlanNode root) {
        final GlyphWishlist wishlist = ((CompPlayer) root.unit.getLeader().get()).wishlist.glyphs;
        final Optional<Glyph> wanted = wishlist.getDesiredOptions().getMostWanted();
        final MemoryMap memory = ((CompPlayer) root.unit.getLeader().get()).memory;
        final Point dest = ((MoveNode) root).dest;
        final Unit unit = ((MoveNode) root).unit;
        final Pathfinder pathfinder = new Pathfinder(unit);
        final List<Point> path = pathfinder.getPath(view, dest);
        float score = 0f;
        for (Point p : path) {
            final Optional<MemoryCell> cell = memory.getCell(dest);
            final boolean addToScore = cell
                    .map((MemoryCell c) -> c.getGlyph().isPresent() && !c.getOwner().equals(root.unit.getLeader()))
                    .orElse(false);
            if (addToScore) {
                score += wanted.map((Glyph g) -> g.equals(cell.get().getGlyph().get())).orElse(false) ? 1f : 0.75f;
            }
        }
        return path.size() == 0 ? 0f : (score / path.size());
    }
}
