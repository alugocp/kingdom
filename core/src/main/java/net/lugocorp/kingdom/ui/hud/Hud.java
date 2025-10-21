package net.lugocorp.kingdom.ui.hud;
import net.lugocorp.kingdom.engine.Settings;
import net.lugocorp.kingdom.engine.controllers.MenuController;
import net.lugocorp.kingdom.ui.Menu;
import net.lugocorp.kingdom.ui.views.GameView;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * This class handles rendering all the HUD UI
 */
public class Hud {
    public final Popups popups = new Popups();
    public final TopHud top;
    public final BotHud bot;

    public Hud(GameView view) {
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
    public void draw(GameView view) {
        this.top.draw(view.av);
        this.bot.tileMenu.draw(view.av);
        this.bot.turnButton.draw(view.av);
        this.bot.minimap.draw(view.av, view.getCenteredPoint());
        if (this.popups.isDisplayed()) {
            this.popups.get().ifPresent((Menu m) -> m.draw(view.av));
        }
    }
}
