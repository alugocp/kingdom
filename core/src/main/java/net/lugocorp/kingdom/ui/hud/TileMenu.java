package net.lugocorp.kingdom.ui.hud;
import net.lugocorp.kingdom.game.model.Tile;
import net.lugocorp.kingdom.ui.Menu;
import net.lugocorp.kingdom.ui.MenuNode;
import net.lugocorp.kingdom.ui.nodes.ListNode;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.math.Coords;
import net.lugocorp.kingdom.utils.math.Point;
import java.util.Optional;

/**
 * Handles any Menu that describes a Tile in the World
 */
public class TileMenu extends Menu {
    private final Point menuCoords = new Point();
    private final GameView view;

    public TileMenu(GameView view) {
        super(0, Coords.SIZE.y, Coords.SIZE.x - Minimap.MAX_W, true, new ListNode());
        this.outline();
        this.view = view;
    }

    /**
     * Sets the Menu content for the given Tile
     */
    public void set(Point p) {
        this.view.game.world.getTile(this.menuCoords).get().changeHovered(false);
        this.view.game.world.getTile(p).ifPresent((Tile t) -> t.changeHovered(true));
        this.menuCoords.set(p.x, p.y);
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
        this.setRoot(node);
    }
}
