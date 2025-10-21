package net.lugocorp.kingdom.ui.hud;
import net.lugocorp.kingdom.color.ColorScheme;
import net.lugocorp.kingdom.engine.Settings;
import net.lugocorp.kingdom.engine.controllers.MenuController;
import net.lugocorp.kingdom.menu.Menu;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.math.Coords;
import net.lugocorp.kingdom.utils.math.Rect;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * This class handles rendering all the HUD UI
 */
public class Hud {
    public final Popups popups = new Popups();
    public final Logger logger;
    public final TopHud top;
    public final BotHud bot;

    public Hud(GameView view) {
        this.logger = new Logger(view);
        this.top = new TopHud(view);
        this.bot = new BotHud(view);
    }

    /**
     * Returns a list of MenuControllers to interact with the HUD UI
     */
    public List<MenuController> getControllers(GameView view, Settings settings) {
        final List<MenuController> controllers = new ArrayList<>();
        controllers.add(new MenuController(settings,
                () -> view.game.mechanics.turns.canHumanPlayerAct() && view.hud.popups.isDisplayed()
                        ? this.popups.get()
                        : Optional.empty()));
        controllers.add(new MenuController(settings, () -> Optional.of(this.bot.turnButton)));
        controllers.add(new MenuController(settings, () -> Optional.of(this.bot.tileMenu)));
        controllers.add(new MenuController(settings, () -> Optional.of(this.top)));
        return controllers;
    }

    /**
     * Draws all aspects of the HUD UI
     */
    public void draw(GameView view, int dt) {
        // Fill in the space behind the Minimap
        final Rect r = Coords.screen.flip(this.bot.tileMenu.getBoundingRect());
        view.av.shapes.begin(ShapeType.Filled);
        view.av.shapes.setColor(ColorScheme.MENU.color);
        view.av.shapes.rect(r.w, r.y, Coords.SIZE.x - r.w, r.h);
        view.av.shapes.end();

        // Draw the HUD elements
        this.top.draw(view.av);
        this.bot.turnButton.draw(view.av);
        this.bot.minimap.draw(view.av, view.getCenteredPoint());
        this.bot.tileMenu.draw(view.av);
        if (this.popups.isDisplayed()) {
            this.popups.get().ifPresent((Menu m) -> m.draw(view.av));
        }
        this.logger.render(dt);
    }
}
