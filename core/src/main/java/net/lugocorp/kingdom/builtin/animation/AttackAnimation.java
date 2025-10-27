package net.lugocorp.kingdom.builtin.animation;
import net.lugocorp.kingdom.engine.animation.Animation;
import net.lugocorp.kingdom.engine.animation.Tween;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.math.Coords;
import net.lugocorp.kingdom.math.Hexagons;
import net.lugocorp.kingdom.math.Point;
import net.lugocorp.kingdom.ui.views.GameView;

/**
 * Animation that plays when a Unit makes an attack
 */
public class AttackAnimation extends Animation {
    private final float[] diff;
    private final float angle;
    private final Unit unit;

    public AttackAnimation(Unit unit, Point target) {
        super(new Tween().duration(750).goBackToStart());
        final float angle = Coords.grid.angle(unit.getPoint(), target);
        this.diff = new float[]{(float) (Hexagons.SIDE / 2f * Math.cos(angle)),
                (float) (Hexagons.SIDE / 2f * Math.sin(angle))};
        this.angle = angle - ((float) Math.PI / 2f);
        this.unit = unit;
    }

    /** {@inheritdoc} */
    @Override
    protected void animate(float value) {
        this.unit.setModelPositionOffset(this.diff[0] * value, this.diff[1] * value);
        this.unit.setRotation(this.angle);
    }

    /** {@inheritdoc} */
    @Override
    public void onFinish(GameView view) {
        this.unit.resetModelPosition();
    }
}
