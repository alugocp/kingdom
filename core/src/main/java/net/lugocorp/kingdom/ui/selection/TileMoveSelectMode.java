package net.lugocorp.kingdom.ui.selection;
import net.lugocorp.kingdom.game.model.Tile;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.math.Hexagons;
import net.lugocorp.kingdom.utils.math.Point;
import java.util.HashSet;
import java.util.Set;

/**
 * This TileSelector allows a Unit to move
 */
class TileMoveSelectMode extends TileSelectMode {
    private final Set<Point> singleTurnTargets = new HashSet<>();
    private final Unit unit;

    TileMoveSelectMode(Unit unit) {
        this.unit = unit;
    }

    /** {@inheritdoc} */
    @Override
    final void init(GameView view) {
        final int remainingDistance = view.game.actions.getRemainingMoveDistance(view, this.unit);
        this.singleTurnTargets.addAll(this.unit.movement.getTargets(view, remainingDistance));
        this.singleTurnTargets.add(this.unit.getPoint());
        for (Point p : this.singleTurnTargets) {
            view.game.world.getTile(p).ifPresent((Tile t) -> t.setDistanceBorder(
                    Hexagons.getBorderInteger(p, (Point p1) -> !this.singleTurnTargets.contains(p1))));
        }
    }

    /** {@inheritdoc} */
    @Override
    final boolean isValidTile(GameView view, Point p) {
        return true;
    }

    /** {@inheritdoc} */
    @Override
    final void clickedValidPoint(GameView view, Point p) {
        if (this.unit.getPoint().equals(p)) {
            return;
        }
        view.av.loaders.sounds.play("sfx/footstep");
        this.unit.movement.move(view, p).execute();
        view.hud.minimap.refresh(view.game.world);
    }

    /** {@inheritdoc} */
    @Override
    final void clickedInvalidPoint(GameView view) {
        // No-op
    }

    /** {@inheritdoc} */
    @Override
    final void dispel(GameView view) {
        for (Point p : this.singleTurnTargets) {
            view.game.world.getTile(p).ifPresent((Tile t) -> t.setDistanceBorder(0));
        }
    }
}
