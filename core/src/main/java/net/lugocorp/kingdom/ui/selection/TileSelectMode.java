package net.lugocorp.kingdom.ui.selection;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.math.Point;

/**
 * This class allows us to select a Tile using different modes
 */
abstract class TileSelectMode {

    /**
     * Do something when this TileSelector appears in the Game
     */
    abstract void init(GameView view);

    /**
     * Returns true if the Tile at the given Point is selectable
     */
    abstract boolean isValidTile(GameView view, Point p);

    /**
     * Do something when we click one of this TileSelector's valid Tiles
     */
    abstract void clickedValidPoint(GameView view, Point p);

    /**
     * Do something when we click outside of this TileSelector's valid Tiles
     */
    abstract void clickedInvalidPoint(GameView view);

    /**
     * Do something when this TileSelector is no longer needed
     */
    abstract void dispel(GameView view);
}
