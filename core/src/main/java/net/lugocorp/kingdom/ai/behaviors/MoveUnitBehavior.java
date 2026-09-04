package net.lugocorp.kingdom.ai.behaviors;
import net.lugocorp.kingdom.ai.Behavior;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.math.Point;
import net.lugocorp.kingdom.ui.views.GameView;
import java.util.List;

/**
 * This Behavior moves a Unit to a certain Point
 */
public class MoveUnitBehavior implements Behavior {
    private final List<Point> path;
    private final Unit unit;

    public MoveUnitBehavior(Unit unit, List<Point> path) {
        this.path = path;
        this.unit = unit;
    }

    /** {@inheritdoc} */
    @Override
    public void act(GameView view) {
        this.unit.movement.move(view, this.path, true).execute();
    }

    /** {@inheritdoc} */
    @Override
    public boolean isFinished(GameView view) {
        return true;
    }
}
