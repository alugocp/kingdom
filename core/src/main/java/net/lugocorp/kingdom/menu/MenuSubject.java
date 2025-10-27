package net.lugocorp.kingdom.menu;
import net.lugocorp.kingdom.math.Point;
import net.lugocorp.kingdom.ui.views.GameView;
import java.util.Optional;

/**
 * This interface should be implemented for anything that appears in an in-game
 * Menu
 */
public interface MenuSubject {

    /**
     * Generate the relevant menu content
     */
    public MenuNode getMenuContent(GameView view, Optional<Point> p);
}
