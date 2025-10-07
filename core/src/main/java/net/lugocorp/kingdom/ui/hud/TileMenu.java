package net.lugocorp.kingdom.ui.hud;
import net.lugocorp.kingdom.builtin.animation.CloseTileMenuAnimation;
import net.lugocorp.kingdom.builtin.animation.OpenTileMenuAnimation;
import net.lugocorp.kingdom.game.model.Tile;
import net.lugocorp.kingdom.ui.Menu;
import net.lugocorp.kingdom.ui.MenuNode;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.math.Point;
import java.util.Optional;

/**
 * Handles any Menu that describes a Tile in the World
 */
public class TileMenu {
    public static final int WIDTH = 400;
    private final GameView view;
    private Optional<Menu> menu = Optional.empty();
    private Point menuCoords = new Point(0, 0);
    // TODO the hovered and selected tiles have full visibility bightness (even in
    // low visiiblity), and tile selection options should pulsate brightness like
    // how waves rock back and forth

    public TileMenu(GameView view) {
        this.view = view;
    }

    /**
     * Returns the currently open Menu's Tile coordinates
     */
    public Optional<Point> getCoords() {
        return this.menu.map((Menu _m) -> this.menuCoords);
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
    public void open(Point p) {
        this.menu.ifPresent((Menu _m) -> this.view.game.world.getTile(this.menuCoords).get().decrementSelection());
        this.view.game.world.getTile(p).ifPresent((Tile t) -> t.incrementSelection());
        this.menuCoords = p;
        this.refresh(false);
    }

    /**
     * Opens the Menu that is set in this View's recent memory
     */
    public void refresh(boolean onlyIfCurrentlyOpen) {
        final boolean wasMenuOpen = this.menu.isPresent();
        if (onlyIfCurrentlyOpen && !wasMenuOpen) {
            return;
        }
        final Optional<Tile> t = this.view.game.world.getTile(this.menuCoords);
        if (!t.isPresent()) {
            return;
        }
        final MenuNode node = t.get().getMenuContent(this.view, Optional.of(this.menuCoords));
        this.menu = Optional.of(new Menu(wasMenuOpen ? 0 : -TileMenu.WIDTH,
                this.view.hud.getHeight() + this.view.hud.minimap.getHeight(), TileMenu.WIDTH, true, node));
        this.menu.get().outline();
        if (!wasMenuOpen) {
            this.view.animations.add(new OpenTileMenuAnimation(this.menu.get()));
        }
    }

    /**
     * Closes the currently open Menu
     */
    public void close() {
        this.menu.ifPresent((Menu m) -> {
            this.view.game.world.getTile(this.menuCoords).get().decrementSelection();
            this.view.animations.add(new CloseTileMenuAnimation(m, () -> {
                this.menu = Optional.empty();
            }));
        });
    }
}
