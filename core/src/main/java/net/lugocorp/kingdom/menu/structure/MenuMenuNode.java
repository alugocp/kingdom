package net.lugocorp.kingdom.menu.structure;
import net.lugocorp.kingdom.engine.AudioVideo;
import net.lugocorp.kingdom.menu.Menu;
import net.lugocorp.kingdom.menu.MenuNode;
import net.lugocorp.kingdom.utils.math.Point;
import net.lugocorp.kingdom.utils.math.Rect;

/**
 * A MenuNode that wraps another Menu
 */
public class MenuMenuNode implements MenuNode {
    private final Menu menu;

    public MenuMenuNode(MenuNode root) {
        this.menu = new Menu(0, 0, 100, false, root);
    }

    /** {@inheritdoc} */
    @Override
    public int getHeight() {
        return this.menu.getHeight();
    }

    /** {@inheritdoc} */
    @Override
    public void draw(AudioVideo av, Rect bounds) {
        this.menu.setX(bounds.x);
        this.menu.setY(bounds.y);
        this.menu.draw(av);
    }

    /** {@inheritdoc} */
    @Override
    public void pack(Menu menu, int width) {
        if (!menu.hasSubmenu()) {
            menu.setSubmenu(this.menu);
        }
        this.menu.setWidth(width);
    }

    /** {@inheritdoc} */
    @Override
    public void click(Rect bounds, Point p) {
        this.menu.click(p);
    }

    /** {@inheritdoc} */
    @Override
    public void unclick() {
        this.menu.click(new Point(-1, -1));
    }

    /** {@inheritdoc} */
    @Override
    public void mouseMoved(Rect bounds, Point prev, Point curr) {
        this.menu.mouseMoved(curr);
    }

    /** {@inheritdoc} */
    @Override
    public void keyPressed(int keycode) {
        this.menu.keyPressed(keycode);
    }
}
