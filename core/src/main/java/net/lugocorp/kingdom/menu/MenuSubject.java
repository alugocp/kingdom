package net.lugocorp.kingdom.menu;
import net.lugocorp.kingdom.views.GameView;

/**
 * This interface should be implemented for anything that appears in an in-game
 * Menu
 */
public interface MenuSubject {

    /**
     * Generate the relevant menu content
     */
    public MenuNode getMenuContent(GameView view, int x, int y);
}
