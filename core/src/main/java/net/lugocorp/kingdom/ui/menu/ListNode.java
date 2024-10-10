package net.lugocorp.kingdom.ui.menu;
import java.util.ArrayList;
import java.util.List;
import net.lugocorp.kingdom.engine.Graphics;
import net.lugocorp.kingdom.math.Point;
import net.lugocorp.kingdom.math.Rect;

/**
 * MenuNode item containing many child nodes
 */
public class ListNode implements MenuNode {
    private final List<MenuNode> children = new ArrayList<>();

    /**
     * Adds a child MenuNode to this ListNode
     */
    public ListNode add(MenuNode child) {
        this.children.add(child);
        return this;
    }

    /** {@inheritdoc} */
    @Override
    public int getHeight() {
        int h = 0;
        for (MenuNode child : this.children) {
            h += child.getHeight();
        }
        return h;
    }

    /** {@inheritdoc} */
    @Override
    public void pack(int width) {
        for (MenuNode child : this.children) {
            child.pack(width);
        }
    }

    /** {@inheritdoc} */
    @Override
    public void draw(Graphics graphics, Rect bounds) {
        int y = bounds.y;
        for (MenuNode child : this.children) {
            final Rect r = new Rect(bounds.x, y, bounds.w, child.getHeight());
            child.draw(graphics, r);
            y += r.h;
        }
    }

    /** {@inheritdoc} */
    @Override
    public void click(Menu menu, Rect bounds, Point p) {
        int y = bounds.y;
        for (MenuNode child : this.children) {
            final Rect r = new Rect(bounds.x, y, bounds.w, child.getHeight());
            if (r.contains(p)) {
                child.click(menu, r, p);
            }
            y += r.h;
        }
    }
}
