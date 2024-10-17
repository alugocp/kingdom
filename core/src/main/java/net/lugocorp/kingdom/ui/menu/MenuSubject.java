package net.lugocorp.kingdom.ui.menu;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.math.Point;
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
