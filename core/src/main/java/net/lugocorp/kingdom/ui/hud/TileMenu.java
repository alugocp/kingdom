package net.lugocorp.kingdom.ui.hud;
import net.lugocorp.kingdom.engine.AudioVideo;
import net.lugocorp.kingdom.game.model.Tile;
import net.lugocorp.kingdom.game.world.World;
import net.lugocorp.kingdom.ui.ColorScheme;
import net.lugocorp.kingdom.ui.Menu;
import net.lugocorp.kingdom.ui.MenuNode;
import net.lugocorp.kingdom.ui.nodes.ButtonNode;
import net.lugocorp.kingdom.ui.nodes.ListNode;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.math.Coords;
import net.lugocorp.kingdom.utils.math.Point;
import com.badlogic.gdx.graphics.Color;
import java.util.Optional;

/**
 * Handles any Menu that describes a Tile in the World
 */
public class TileMenu {
    public static final int WIDTH = 400;
    private final ButtonNode completeTurnButton;
    private final Menu completeTurnMenu;
    private final GameView view;
    private final Menu menu;
    private Point menuCoords = new Point(0, 0);
    public final Minimap minimap = new Minimap();

    public TileMenu(GameView view) {
        this.menu = new Menu(0, Coords.SIZE.y, Coords.SIZE.x - Minimap.MAX_W, true, new ListNode()).outline();
        this.view = view;
        this.completeTurnButton = (ButtonNode) (new ButtonNode(view.av, "Complete Turn", () -> {
            if (view.popups.get().isPresent()) {
                view.av.loaders.sounds.play("sfx/error");
                view.popups.setDisplay(true);
            } else if (view.game.actions.goToNextUnit(view)) {
                view.av.loaders.sounds.play("sfx/error");
            } else {
                view.av.loaders.sounds.play("sfx/end-turn");
                view.logger.log("You have ended your turn");
                view.game.mechanics.turns.iterateTurnPlayer(view);
                view.menu.refresh();
            }
        }) {
            /** {@inheritdoc} */
            @Override
            protected Color getColor() {
                if (!this.isEnabled()) {
                    return ColorScheme.DISABLE.color;
                }
                return this.isHovered() ? ColorScheme.SPECIAL_HOVER.color : ColorScheme.SPECIAL_BUTTON.color;
            }
        }.setEnabledCriteria(() -> view.game.mechanics.turns.canHumanPlayerAct()).disableNoise());
        this.completeTurnMenu = new Menu(Coords.SIZE.x - Minimap.MAX_W, Coords.SIZE.y, Minimap.MAX_W, true,
                this.completeTurnButton);
    }

    /**
     * Sets up the layout of the UI
     */
    public void init(World world) {
        final int y = Coords.SIZE.y - Minimap.MAX_H - this.completeTurnButton.getHeight();
        this.completeTurnMenu.setY(Coords.SIZE.y - this.completeTurnButton.getHeight());
        this.minimap.init(world, Coords.SIZE.x - Minimap.MAX_W, y);
        this.menu.setY(y);
    }

    /**
     * Modifies the UI based on the current Player's turn
     */
    public void setPlayerTurn(boolean isHumanPlayer) {
        if (isHumanPlayer) {
            this.completeTurnButton.enable(true).setText("Complete Turn");
        } else {
            this.completeTurnButton.enable(false).setText("Waiting...");
        }
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

    public void draw(AudioVideo av) {
        this.menu.draw(av);
        this.completeTurnMenu.draw(av);
        this.minimap.draw(av, this.view.getCenteredPoint());
    }
}
