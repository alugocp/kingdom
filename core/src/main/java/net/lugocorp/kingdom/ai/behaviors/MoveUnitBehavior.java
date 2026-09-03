package net.lugocorp.kingdom.ai.behaviors;
import net.lugocorp.kingdom.ai.Behavior;

/**
 * This Behavior moves a Unit to a certain Point
 */
public class MoveUnitBehavior implements Behavior {
    private final Point dest;
    private final Unit unit;

    public MoveUnitBehavior(Unit unit, Point dest) {
        this.unit = unit;
        this.dest = dest;
    }

    /** {@inheritdoc} */
    @Override
    public void act() {
        // TODO
    }

    /** {@inheritdoc} */
    @Override
    public boolean isFinished() {
        return true;
    }
}
