package net.lugocorp.kingdom.ui.hud;
import net.lugocorp.kingdom.game.model.Tile;
import net.lugocorp.kingdom.math.Coords;
import net.lugocorp.kingdom.math.MutablePoint;
import net.lugocorp.kingdom.math.Point;
import net.lugocorp.kingdom.menu.Menu;
import net.lugocorp.kingdom.menu.MenuNode;
import net.lugocorp.kingdom.menu.structure.ListNode;
import net.lugocorp.kingdom.ui.views.GameView;
import java.util.Optional;

/**
 * Handles any Menu that describes a Tile in the World
 */
public class TileMenu extends Menu {
    private final MutablePoint menuCoords = new MutablePoint();
    private final GameView view;

    public TileMenu(GameView view) {
        super(0, Coords.SIZE.y, Coords.SIZE.x - Minimap.MAX_W, true, new ListNode());
        this.outline();
        this.view = view;
    }

    /**
     * Returns this TileMenu's currently targeted Point
     */
    public Point get() {
        return this.menuCoords.toPoint();
    }

    /**
     * Sets the Menu content for the given Tile
     */
    public void set(Point p) {
        this.view.game.world.getTile(this.menuCoords.toPoint()).get().changeHovered(false);
        this.view.game.world.getTile(p).ifPresent((Tile t) -> t.changeHovered(true));
        this.menuCoords.set(p.x, p.y);
        this.resetOffset();
        this.refresh();
    }

    /**
     * Opens the Menu that is set in this View's recent memory
     */
    public void refresh() {
        final Optional<Tile> t = this.view.game.world.getTile(this.menuCoords.toPoint());
        if (!t.isPresent()) {
            return;
        }
        final MenuNode node = t.get().getMenuContent(this.view, Optional.of(this.menuCoords.toPoint()));
        this.closeMiniMenu();
        this.setRoot(node);
    }
}
