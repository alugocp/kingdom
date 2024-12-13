package net.lugocorp.kingdom.ui.game;
import net.lugocorp.kingdom.game.model.Tile;
import net.lugocorp.kingdom.ui.menu.Menu;
import net.lugocorp.kingdom.ui.menu.MenuNode;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.math.Point;
import java.util.Optional;

/**
 * Handles any Menu that describes a Tile in the World
 */
public class TileMenu {
    private final GameView view;
    private Optional<Menu> menu = Optional.empty();
    private Point menuCoords = new Point(0, 0);

    public TileMenu(GameView view) {
        this.view = view;
    }

    /**
     * Returns the currently open Menu (if any)
     */
    public Optional<Menu> get() {
        return this.menu;
    }

    /**
     * Handles click logic on a Tile (open a Menu for said Tile)
     */
    public void open() {
        Optional<Point> p = this.view.selector.getHovered();
        this.view.selector.deselect();
        this.close();
        if (!p.isPresent()) {
            return;
        }
        this.menuCoords = p.get();
        this.refresh(false);
    }

    /**
     * Opens the Menu that is set in this View's recent memory
     */
    public void refresh(boolean onlyIfCurrentlyOpen) {
        if (onlyIfCurrentlyOpen && !this.menu.isPresent()) {
            return;
        }
        Optional<Tile> t = this.view.game.world.getTile(this.menuCoords);
        if (!t.isPresent()) {
            return;
        }
        MenuNode node = t.get().getMenuContent(this.view, Optional.of(this.menuCoords));
        this.menu = Optional.of(new Menu(0, this.view.hud.getHeight(), 400, true, node));
    }

    /**
     * Closes the currently open Menu
     */
    public void close() {
        this.menu = Optional.empty();
    }
}
