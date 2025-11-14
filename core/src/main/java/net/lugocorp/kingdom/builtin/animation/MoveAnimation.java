package net.lugocorp.kingdom.builtin.animation;
import net.lugocorp.kingdom.engine.animation.Animation;
import net.lugocorp.kingdom.engine.animation.Tween;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.math.Coords;
import net.lugocorp.kingdom.math.Point;
import net.lugocorp.kingdom.ui.views.GameView;

/**
 * This Animation plays when a Unit moves
 */
public class MoveAnimation extends Animation {
    private final Runnable after;
    private final float[] diff;
    private final float angle;
    private final Unit unit;

    public MoveAnimation(Unit unit, Point p1, Point p2, Runnable after) {
        super(new Tween().duration(500));
        this.diff = Coords.grid.difference(p1, p2);
        this.angle = Coords.grid.angle(p1, p2) - ((float) Math.PI / 2f);
        this.after = after;
        this.unit = unit;
    }

    /** {@inheritdoc} */
    @Override
    public void animate(float value) {
        if (this.isFirstFrame()) {
            this.unit.setRotation(this.angle);
        }
        this.unit.setModelPositionOffset(this.diff[0] * value, this.diff[1] * value);
    }

    /** {@inheritdoc} */
    @Override
    public void onFinish(GameView view) {
        this.after.run();
    }
}
