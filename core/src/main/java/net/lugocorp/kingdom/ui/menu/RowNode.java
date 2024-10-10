package net.lugocorp.kingdom.ui.menu;
import net.lugocorp.kingdom.engine.Graphics;
import net.lugocorp.kingdom.math.Point;
import net.lugocorp.kingdom.math.Rect;
import java.util.ArrayList;
import java.util.List;

/**
 * MenuNode item containing many child nodes
 */
public class RowNode implements MenuNode {
    private final List<MenuNode> children = new ArrayList<>();

    /**
     * Adds a child MenuNode to this RowNode
     */
    public RowNode add(MenuNode child) {
        this.children.add(child);
        return this;
    }

    /** {@inheritdoc} */
    @Override
    public int getHeight() {
        int h = 0;
        for (MenuNode child : this.children) {
            h = Math.max(h, child.getHeight());
        }
        return h;
    }

    /** {@inheritdoc} */
    @Override
    public void pack(int width) {
        for (MenuNode child : this.children) {
            child.pack(width / this.children.size());
        }
    }

    /** {@inheritdoc} */
    @Override
    public void draw(Graphics graphics, Rect bounds) {
        int x = bounds.x;
        int w = bounds.w / this.children.size();
        for (MenuNode child : this.children) {
            final Rect r = new Rect(x, bounds.y, w, child.getHeight());
            child.draw(graphics, r);
            x += w;
        }
    }

    /** {@inheritdoc} */
    @Override
    public void click(Menu menu, Rect bounds, Point p) {
        int x = bounds.x;
        int w = bounds.w / this.children.size();
        for (MenuNode child : this.children) {
            final Rect r = new Rect(x, bounds.y, w, child.getHeight());
            if (r.contains(p)) {
                child.click(menu, r, p);
            }
            x += w;
        }
    }
}
