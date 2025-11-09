package net.lugocorp.kingdom.ui.hud;
import net.lugocorp.kingdom.menu.Menu;
import net.lugocorp.kingdom.ui.views.GameView;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * This class handles popup Menu logic within the HUD UI
 */
public class Popups {
    private final GameView view;
    private final List<Boolean> required = new ArrayList<>();
    private final List<Menu> queue = new ArrayList<>();
    private boolean display = false;

    public Popups(GameView view) {
        this.view = view;
    }

    /**
     * Retrieves the first popup Menu in the queue, if any
     */
    public Optional<Menu> get() {
        return this.queue.isEmpty() ? Optional.empty() : Optional.of(this.queue.get(0));
    }

    /**
     * Adds a popup Menu to the state (at the end of the list)
     */
    public void add(Menu menu) {
        if (!this.display) {
            this.view.av.loaders.sounds.play("sfx/popup");
        }
        this.required.add(0, true);
        this.queue.add(menu);
        this.setDisplay(true);
        menu.outline();
    }

    /**
     * Adds a popup Menu to the state (at the front of the list)
     */
    public void addNext(Menu menu) {
        if (!this.display) {
            this.view.av.loaders.sounds.play("sfx/popup");
        }
        this.required.add(0, true);
        this.queue.add(0, menu);
        this.setDisplay(true);
        menu.outline();
    }

    /**
     * Adds an unrequired popup Menu to the state (at the front of the list)
     */
    public void addNextUnrequired(Menu menu) {
        if (!this.display) {
            this.view.av.loaders.sounds.play("sfx/popup");
        }
        this.required.add(0, false);
        this.queue.add(0, menu);
        this.setDisplay(true);
        menu.outline();
    }

    /**
     * Replaces the currently open Menu with another unrequired one
     */
    public void replaceUnrequired(Menu menu) {
        this.complete();
        this.addNextUnrequired(menu);
    }

    /**
     * Removes a popup Menu from the queue
     */
    public void complete() {
        this.queue.remove(0);
        this.required.remove(0);
        if (this.queue.isEmpty()) {
            this.display = false;
        }
    }

    /**
     * Shows or hides popup Menus
     */
    public void setDisplay(boolean display) {
        while (!display && this.required.size() > 0 && !this.required.get(0)) {
            this.complete();
        }
        if (!this.display && display) {
            this.view.hud.logger.clear();
        }
        this.display = display && !this.queue.isEmpty();
    }

    /**
     * Returns true if popup Menus are on screen
     */
    public boolean isDisplayed() {
        return this.display && !this.queue.isEmpty();
    }
}
