package net.lugocorp.kingdom.ui.selection;
import net.lugocorp.kingdom.game.model.Tile;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.math.Point;
import net.lugocorp.kingdom.utils.pathfinding.Pathfinder;
import java.util.List;
import java.util.Optional;

/**
 * This TileSelector allows a Unit to move
 */
class TileMoveSelectMode extends TileSelectMode {
    private final Pathfinder pathfinder;
    private final Unit unit;
    private Optional<List<Point>> previousPath = Optional.empty();

    TileMoveSelectMode(Unit unit) {
        this.pathfinder = new Pathfinder(unit);
        this.unit = unit;
    }

    /**
     * Removes all shader decoration from the previously marked path (if one exists)
     */
    private final void removeShaderData(GameView view) {
        this.previousPath.ifPresent((List<Point> path) -> {
            for (Point p : path) {
                view.game.world.getTile(p).ifPresent((Tile t) -> t.setDistanceBorder(0));
            }
        });
    }

    /** {@inheritdoc} */
    @Override
    final void init(GameView view) {
        // No-op
    }

    /** {@inheritdoc} */
    @Override
    final boolean isValidTile(GameView view, Point p) {
        return this.unit.movement.canMoveToPoint(view, p);
    }

    /** {@inheritdoc} */
    @Override
    final void clickedValidPoint(GameView view, Point p) {
        view.av.loaders.sounds.play("sfx/footstep");
        this.unit.movement.move(view, this.previousPath.get()).execute();
        view.hud.bot.minimap.refresh(view.game.world);
    }

    /** {@inheritdoc} */
    @Override
    final void clickedInvalidPoint(GameView view) {
        view.hud.logger.error("Unit cannot move there");
    }

    /** {@inheritdoc} */
    @Override
    final void dispel(GameView view) {
        this.removeShaderData(view);
    }

    /** {@inheritdoc} */
    @Override
    final void hoverTile(GameView view, Point p) {
        final List<Point> path = this.pathfinder.getPath(view, p);
        this.removeShaderData(view);
        this.previousPath = Optional.of(path);
        for (Point p1 : path) {
            view.game.world.getTile(p1).ifPresent((Tile t) -> t.setDistanceBorder(63));
        }
    }
}
