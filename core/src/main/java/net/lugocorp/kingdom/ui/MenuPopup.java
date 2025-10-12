package net.lugocorp.kingdom.ui;
import net.lugocorp.kingdom.utils.math.Point;
import net.lugocorp.kingdom.utils.math.Rect;
import java.util.Optional;

/**
 * This class is used to display a popup mini-Menu when you hover MenuNodes
 */
public class MenuPopup {
    private Optional<Menu> menu = Optional.empty();
    private boolean hovered = false;

    public void setMenu(Menu m) {
        this.menu = Optional.of(m);
    }

    /**
     * Checks if we should display a popup mini-Menu or not, then acts accordingly
     */
    public void update(Rect bounds, Point prev, Point curr, MenuNode root) {
        if (!this.menu.isPresent()) {
            return;
        }
        final boolean currIn = bounds.contains(curr);
        if (currIn && !this.hovered) {
            this.menu.get().setMiniMenu(root, curr.x + 25, curr.y + 15);
            this.hovered = true;
        }
        if (!currIn && this.hovered && this.menu.get().getMiniMenuRoot().map((MenuNode n) -> n == root).orElse(false)) {
            this.menu.get().closeMiniMenu();
            this.hovered = false;
        }
    }
}
