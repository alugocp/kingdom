package net.lugocorp.kingdom.ai.behaviors;
import net.lugocorp.kingdom.ai.Behavior;
import net.lugocorp.kingdom.game.layers.Entity;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.math.Hexagons;
import net.lugocorp.kingdom.math.Point;
import net.lugocorp.kingdom.pathfinding.Pathfinder;
import net.lugocorp.kingdom.ui.views.GameView;
import java.util.List;

/**
 * This Behavior tells the given Unit to follow the target Entity
 */
public class FollowEntityBehavior implements Behavior {
    private final Entity target;
    private final Unit unit;
    private boolean kill = false;

    public FollowEntityBehavior(Unit unit, Entity target) {
        this.target = target;
        this.unit = unit;
    }

    /** {@inheritdoc} */
    @Override
    public void act(GameView view) {
        final Pathfinder pathfinder = new Pathfinder(this.unit);
        final List<Point> path = pathfinder.getPath(view, this.target.getPoint());
        if (path.size() == 0) {
            this.kill = true;
            return;
        }
        this.unit.movement.move(view, path, true).execute();
    }

    /** {@inheritdoc} */
    @Override
    public boolean isFinished(GameView view) {
        return this.kill || this.unit.getPoint().equals(this.target.getPoint())
                || Hexagons.areNeighbors(this.unit.getPoint(), this.target.getPoint());
    }
}
