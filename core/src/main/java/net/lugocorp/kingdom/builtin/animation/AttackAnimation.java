package net.lugocorp.kingdom.builtin.animation;
import net.lugocorp.kingdom.engine.animation.Animation;
import net.lugocorp.kingdom.engine.animation.Tweening;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.utils.math.Coords;
import net.lugocorp.kingdom.utils.math.Hexagons;
import net.lugocorp.kingdom.utils.math.Point;

/**
 * Animation that plays when a Unit makes an attack
 */
public class AttackAnimation extends Animation {
    private final float[] diff;
    private final float angle;

    public AttackAnimation(Unit unit, Point target) {
        super(new Tweening().duration(750).goBackToStart());
        final float angle = Coords.grid.angle(unit.getPoint(), target);
        this.diff = new float[]{(float) (Hexagons.SIDE / 2f * Math.cos(angle)),
                (float) (Hexagons.SIDE / 2f * Math.sin(angle))};
        this.angle = angle - ((float) Math.PI / 2f);
    }

    /** {@inheritdoc} */
    @Override
    protected void animate(Unit u, float value) {
        u.setModelPositionOffset(this.diff[0] * value, this.diff[1] * value);
        u.setRotation(this.angle);
    }

    /** {@inheritdoc} */
    @Override
    public void onFinish(Unit u) {
        u.resetModelPosition();
        u.setRotation(0f);
    }
}
