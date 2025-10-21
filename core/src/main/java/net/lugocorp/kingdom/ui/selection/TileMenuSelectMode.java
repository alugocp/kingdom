package net.lugocorp.kingdom.ui.selection;
import net.lugocorp.kingdom.game.model.Tile;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.math.Point;

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
        view.av.loaders.sounds.play("sfx/select-unit");
        view.hud.bot.tileMenu.set(p);
    }

    /** {@inheritdoc} */
    @Override
    final void clickedInvalidPoint(GameView view) {
        view.logger.error("Cannot view tile under fog of war");
    }

    /** {@inheritdoc} */
    @Override
    final void dispel(GameView view) {
        // No-op
    }
}
