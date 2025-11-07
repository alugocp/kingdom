package net.lugocorp.kingdom.ui.selection;
import net.lugocorp.kingdom.game.model.Tile;
import net.lugocorp.kingdom.math.Point;
import net.lugocorp.kingdom.ui.views.GameView;

/**
 * This TileSelector allows us to view a Tile's details
 */
class TileMenuSelectMode extends TileSelectMode {

    /** {@inheritdoc} */
    @Override
    final void init(GameView view) {
        // No-op
    }

    /** {@inheritdoc} */
    @Override
    final boolean isValidTile(GameView view, Point p) {
        return view.game.world.getTile(p).map((Tile t) -> t.isVisible()).orElse(false);
    }

    /** {@inheritdoc} */
    @Override
    final void clickedValidPoint(GameView view, Point p) {
        final boolean unit = view.game.world.getTile(p).flatMap((Tile t) -> t.unit).isPresent();
        view.av.loaders.sounds.play(unit ? "sfx/select-unit" : "sfx/select-tile");
        view.hud.bot.tileMenu.set(p);
    }

    /** {@inheritdoc} */
    @Override
    final void clickedInvalidPoint(GameView view) {
        view.hud.logger.error("Cannot view tile under fog of war");
    }

    /** {@inheritdoc} */
    @Override
    final void dispel(GameView view) {
        // No-op
    }
}
