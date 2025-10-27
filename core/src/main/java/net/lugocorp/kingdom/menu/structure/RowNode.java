package net.lugocorp.kingdom.menu.structure;
import net.lugocorp.kingdom.engine.AudioVideo;
import net.lugocorp.kingdom.math.Point;
import net.lugocorp.kingdom.math.Rect;
import net.lugocorp.kingdom.menu.Menu;
import net.lugocorp.kingdom.menu.MenuNode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * MenuNode item containing many child nodes
 */
public class RowNode implements MenuNode {
    private final List<MenuNode> children = new ArrayList<>();
    private Optional<Integer> columns = Optional.empty();

    /**
     * Adds a child MenuNode to this RowNode
     */
    public RowNode add(MenuNode child) {
        this.children.add(child);
        return this;
    }

    /**
     * Sets a static number of columns to display in this RowNode
     */
    public RowNode setColumns(int columns) {
        this.columns = Optional.of(columns);
        return this;
    }

    /**
     * Returns the number of columns to render in this RowNode
     */
    private int getColumns() {
        return this.columns.map((Integer i) -> i).orElse(this.children.size());
    }

    /**
     * Returns a version of this RowNode as a ListNode
     */
    public ListNode toListNode() {
        final ListNode ls = new ListNode();
        for (MenuNode n : this.children) {
            ls.add(n);
        }
        return ls;
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
    public void pack(Menu menu, int width) {
        if (this.getColumns() < this.children.size()) {
            throw new RuntimeException("Not enough columns set for this RowNode");
        }
        for (MenuNode child : this.children) {
            child.pack(menu, width / this.getColumns());
        }
    }

    /** {@inheritdoc} */
    @Override
    public void draw(AudioVideo av, Rect bounds) {
        final int h = this.getHeight();
        final int w = bounds.w / this.getColumns();
        int x = bounds.x;
        for (MenuNode child : this.children) {
            final Rect r = new Rect(x, bounds.y, w, h);
            child.draw(av, r);
            x += w;
        }
    }

    /** {@inheritdoc} */
    @Override
    public void click(Rect bounds, Point p) {
        final int h = this.getHeight();
        final int w = bounds.w / this.getColumns();
        int x = bounds.x;
        for (MenuNode child : this.children) {
            final Rect r = new Rect(x, bounds.y, w, h);
            if (r.contains(p)) {
                child.click(r, p);
            } else {
                child.unclick();
            }
            x += w;
        }
    }

    /** {@inheritdoc} */
    @Override
    public void unclick() {
        for (MenuNode n : this.children) {
            n.unclick();
        }
    }

    /** {@inheritdoc} */
    @Override
    public void mouseMoved(Rect bounds, Point prev, Point curr) {
        final int h = this.getHeight();
        final int w = bounds.w / this.getColumns();
        int x = bounds.x;
        for (MenuNode child : this.children) {
            final Rect r = new Rect(x, bounds.y, w, h);
            child.mouseMoved(r, prev, curr);
            x += w;
        }
    }

    /** {@inheritdoc} */
    @Override
    public void keyPressed(int keycode) {
        for (MenuNode n : this.children) {
            n.keyPressed(keycode);
        }
    }
}
