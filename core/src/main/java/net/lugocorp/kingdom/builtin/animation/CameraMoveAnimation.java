package net.lugocorp.kingdom.builtin.animation;
import net.lugocorp.kingdom.engine.animation.Animation;
import net.lugocorp.kingdom.engine.animation.Tween;
import net.lugocorp.kingdom.engine.controllers.GameViewController;
import net.lugocorp.kingdom.math.Point;

/**
 * Animation that pans the Camera
 */
public class CameraMoveAnimation extends Animation {
    private final GameViewController controller;
    private final Point current;
    private final Point origin;
    private final Point diff;

    public CameraMoveAnimation(GameViewController controller, Point origin, Point dest) {
        super(new Tween().duration(500));
        this.diff = new Point(dest.x - origin.x, dest.y - origin.y);
        this.current = new Point(origin.x, origin.y);
        this.controller = controller;
        this.origin = origin;
    }

    /** {@inheritdoc} */
    @Override
    protected void animate(float value) {
        this.current.set((int) (this.origin.x + (this.diff.x * value)), (int) (this.origin.y + (this.diff.y * value)));
        this.controller.centerCameraOn(this.current);
    }
}
