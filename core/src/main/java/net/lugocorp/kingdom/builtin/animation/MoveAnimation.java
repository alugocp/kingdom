package net.lugocorp.kingdom.builtin.animation;
import net.lugocorp.kingdom.engine.animation.Animation;
import net.lugocorp.kingdom.engine.animation.Tween;
import net.lugocorp.kingdom.game.events.Event;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.game.player.Player;
import net.lugocorp.kingdom.math.Coords;
import net.lugocorp.kingdom.math.HexSide;
import net.lugocorp.kingdom.math.Hexagons;
import net.lugocorp.kingdom.math.Point;
import net.lugocorp.kingdom.ui.views.GameView;
import java.util.Optional;

/**
 * This Animation plays when a Unit moves
 */
public class MoveAnimation extends Animation {
    private final Optional<Event> after;
    private final float[] diff;
    private final float angle;
    private final Point dest;
    private final Unit unit;

    public MoveAnimation(Unit unit, Point p1, Point p2, Optional<Event> after) {
        super(new Tween().duration(500));
        this.diff = Coords.grid.difference(p1, p2);
        this.angle = Coords.grid.angle(p1, p2) - ((float) Math.PI / 2f);
        this.after = after;
        this.unit = unit;
        this.dest = p2;
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
        final Optional<HexSide> direction = Hexagons.getDirection(this.unit.getPoint(), this.dest);
        if (!direction.isPresent()) {
            throw new RuntimeException("Should not be here - cannot find vision offset direction");
        }
        this.unit.getLeader().ifPresent((Player l) -> this.unit.vision.translate(l, view.game.world, direction.get()));
        this.unit.movement.removeFromPosition(view.game);
        this.unit.movement.setPosition(view, this.dest.x, this.dest.y);
        this.after.ifPresent((Event e) -> this.unit.handleEvent(view, e).execute());
    }
}
