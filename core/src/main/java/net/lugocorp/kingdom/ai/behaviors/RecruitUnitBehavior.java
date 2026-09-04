package net.lugocorp.kingdom.ai.behaviors;
import net.lugocorp.kingdom.ai.Behavior;
import net.lugocorp.kingdom.ai.Priority;
import net.lugocorp.kingdom.game.glyph.Glyph;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.game.player.CompPlayer;
import net.lugocorp.kingdom.math.Point;
import net.lugocorp.kingdom.ui.views.GameView;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

/**
 * This Behavior tells the CompPlayer to recruit a specific type of Unit
 */
public class RecruitUnitBehavior implements Behavior {
    private final Function<Unit, Priority> criteria;
    private final Function<Set<Point>, Point> getSpawnPoint;
    private final CompPlayer player;
    private final Glyph glyph;

    public RecruitUnitBehavior(CompPlayer player, Glyph glyph, Function<Unit, Priority> criteria,
            Function<Set<Point>, Point> getSpawnPoint) {
        this.getSpawnPoint = getSpawnPoint;
        this.criteria = criteria;
        this.player = player;
        this.glyph = glyph;
    }

    /** {@inheritdoc} */
    @Override
    public void act(GameView view) {
        // Get all possible spawn Points
        final Set<Point> tiles = view.game.getRecruitmentTiles(this.player);
        if (tiles.size() == 0) {
            return;
        }

        // Get the preferred spawn Point
        final Point spawnPoint = this.getSpawnPoint.apply(tiles);
        if (spawnPoint == null) {
            return;
        }

        // Grab a list of Unit options
        final List<Unit> options = view.game.mechanics.recruitUnits.getRecruitmentOptions(view, this.glyph, spawnPoint,
                this.player.numRecruitmentOptions);
        if (options.size() == 0) {
            return;
        }

        // Get the highest rated Unit option
        Priority highest = Priority.FATAL;
        Unit best = null;
        for (Unit u : options) {
            final Priority p = this.criteria.apply(u);
            if (p.value > highest.value || (p.value == highest.value && Math.random() < 0.3)) {
                highest = p;
                best = u;
            }
        }

        // Recruit that highest rated Unit
        view.game.mechanics.recruitUnits.choose(view, this.player, best);
    }

    /** {@inheritdoc} */
    @Override
    public boolean isFinished(GameView view) {
        return true;
    }
}
