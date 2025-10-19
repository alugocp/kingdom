package net.lugocorp.kingdom.ui.hud;
import net.lugocorp.kingdom.game.model.Tile;
import net.lugocorp.kingdom.ui.Menu;
import net.lugocorp.kingdom.ui.MenuNode;
import net.lugocorp.kingdom.ui.nodes.ListNode;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.math.Point;
import java.util.Optional;

/**
 * Handles any Menu that describes a Tile in the World
 */
public class TileMenu {
    public static final int WIDTH = 400;
    private final GameView view;
    private final Menu menu;
    private Point menuCoords = new Point(0, 0);

    public TileMenu(GameView view) {
        this.menu = new Menu(0, view.hud.getHeight() + view.hud.minimap.getHeight(), TileMenu.WIDTH, true,
                new ListNode()).outline();
        this.view = view;
    }

    /**
     * Returns the current Menu content
     */
    public Menu get() {
        return this.menu;
    }

    /**
     * Sets the Menu content for the given Tile
     */
    public void set(Point p) {
        this.view.game.world.getTile(this.menuCoords).get().changeHovered(false);
        this.view.game.world.getTile(p).ifPresent((Tile t) -> t.changeHovered(true));
        this.menuCoords = p;
        this.refresh();
    }

    /**
     * Opens the Menu that is set in this View's recent memory
     */
    public void refresh() {
        final Optional<Tile> t = this.view.game.world.getTile(this.menuCoords);
        if (!t.isPresent()) {
            return;
        }
        final MenuNode node = t.get().getMenuContent(this.view, Optional.of(this.menuCoords));
        this.menu.setRoot(node);
    }
}
