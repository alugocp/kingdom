package net.lugocorp.kingdom.ui.hud;
import net.lugocorp.kingdom.game.world.World;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.math.Coords;

/**
 * Represents the bottom half of the HUD UI
 */
public class BotHud {
    public final Minimap minimap = new Minimap();
    public final TurnButton turnButton;
    public final TileMenu tileMenu;

    public BotHud(GameView view) {
        this.turnButton = new TurnButton(view);
        this.tileMenu = new TileMenu(view);
    }

    /**
     * Sets up the layout of the UI
     */
    public void init(World world) {
        final int y = Coords.SIZE.y - Minimap.MAX_H - this.turnButton.getContentHeight();
        this.turnButton.setY(Coords.SIZE.y - this.turnButton.getContentHeight());
        this.minimap.init(world, Coords.SIZE.x - Minimap.MAX_W, y);
        this.tileMenu.setY(y);
    }
}
