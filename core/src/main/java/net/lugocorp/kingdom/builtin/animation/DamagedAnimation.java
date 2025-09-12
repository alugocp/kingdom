package net.lugocorp.kingdom.builtin.animation;
import net.lugocorp.kingdom.engine.animation.Animation;
import net.lugocorp.kingdom.engine.animation.Tweening;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.utils.math.Coords;
import net.lugocorp.kingdom.utils.math.Hexagons;
import net.lugocorp.kingdom.utils.math.Point;

/**
 * Animation that plays when a Unit takes damage
 */
public class DamagedAnimation extends Animation {
    private final float[] diff;

    public DamagedAnimation(Unit unit, Point source) {
        super(new Tweening().duration(500).goBackToStart());
        final float angle = Coords.grid.angle(source, unit.getPoint());
        this.diff = new float[]{(float) (Hexagons.SIDE / 3f * Math.cos(angle)),
                (float) (Hexagons.SIDE / 3f * Math.sin(angle))};
    }

    /** {@inheritdoc} */
    @Override
    protected void animate(Unit u, float value) {
        u.setModelPositionOffset(this.diff[0] * value, this.diff[1] * value);
    }

    /** {@inheritdoc} */
    @Override
    public void onFinish(Unit u) {
        u.resetModelPosition();
    }
}
