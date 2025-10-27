package net.lugocorp.kingdom.builtin.animation;
import net.lugocorp.kingdom.engine.animation.Animation;
import net.lugocorp.kingdom.engine.animation.Tween;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.math.Coords;
import net.lugocorp.kingdom.math.Hexagons;
import net.lugocorp.kingdom.math.Point;
import net.lugocorp.kingdom.ui.views.GameView;

/**
 * Animation that plays when a Unit takes damage
 */
public class DamagedAnimation extends Animation {
    private final float[] diff;
    private final Unit unit;

    public DamagedAnimation(Unit unit, Point source) {
        super(new Tween().duration(500).goBackToStart());
        final float angle = Coords.grid.angle(source, unit.getPoint());
        this.diff = new float[]{(float) (Hexagons.SIDE / 3f * Math.cos(angle)),
                (float) (Hexagons.SIDE / 3f * Math.sin(angle))};
        this.unit = unit;
    }

    /** {@inheritdoc} */
    @Override
    protected void animate(float value) {
        this.unit.setModelPositionOffset(this.diff[0] * value, this.diff[1] * value);
    }

    /** {@inheritdoc} */
    @Override
    public void onFinish(GameView view) {
        this.unit.resetModelPosition();
    }
}
