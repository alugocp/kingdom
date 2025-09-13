package net.lugocorp.kingdom.game.player;
import net.lugocorp.kingdom.game.model.Tile;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.code.SideEffect;
import net.lugocorp.kingdom.utils.math.Point;
import com.badlogic.gdx.graphics.Color;
import java.util.Set;
import java.util.function.Function;

/**
 * This Player is operated by a real life human being
 */
public class HumanPlayer extends Player {

    public HumanPlayer(Color color) {
        super("you", null, color);
    }

    /** {@inheritdoc} */
    @Override
    public boolean isHumanPlayer() {
        return true;
    }

    /** {@inheritdoc} */
    @Override
    public void incrementVision(Tile t) {
        t.incrementVision();
    }

    /** {@inheritdoc} */
    @Override
    public void decrementVision(Tile t) {
        t.decrementVision();
    }

    /** {@inheritdoc} */
    @Override
    public SideEffect select(GameView view, Set<Point> points, String error, Function<Point, SideEffect> action) {
        view.selector.select(points, error, (Point p) -> action.apply(p).execute());
        return SideEffect.none;
    }
}
