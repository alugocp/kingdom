package net.lugocorp.kingdom.ui.selection;
import net.lugocorp.kingdom.game.model.Tile;
import net.lugocorp.kingdom.math.Point;
import net.lugocorp.kingdom.ui.views.GameView;
import java.util.Set;
import java.util.function.Consumer;

/**
 * This TileSelector allows us to select one Tile from a finite Set
 */
class TileSetSelectMode extends TileSelectMode {
    private final Consumer<Point> action;
    private final Set<Point> points;

    TileSetSelectMode(Set<Point> points, Consumer<Point> action) {
        this.points = points;
        this.action = action;
    }

    /** {@inheritdoc} */
    @Override
    final void init(GameView view) {
        for (Point p : this.points) {
            view.game.world.getTile(p).ifPresent((Tile t) -> t.setOption(true));
        }
    }

    /** {@inheritdoc} */
    @Override
    final boolean isValidTile(GameView view, Point p) {
        return this.points.contains(p);
    }

    /** {@inheritdoc} */
    @Override
    final void clickedValidPoint(GameView view, Point p) {
        view.av.loaders.sounds.play("sfx/arrow");
        this.action.accept(p);
    }

    /** {@inheritdoc} */
    @Override
    final void clickedInvalidPoint(GameView view) {
        view.hud.logger.error("Invalid tile selection");
    }

    /** {@inheritdoc} */
    @Override
    final void dispel(GameView view) {
        for (Point p : this.points) {
            view.game.world.getTile(p).ifPresent((Tile t) -> t.setOption(false));
        }
    }
}
