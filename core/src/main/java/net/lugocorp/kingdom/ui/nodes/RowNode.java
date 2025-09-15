package net.lugocorp.kingdom.ui.nodes;
import net.lugocorp.kingdom.engine.AudioVideo;
import net.lugocorp.kingdom.ui.Menu;
import net.lugocorp.kingdom.ui.MenuNode;
import net.lugocorp.kingdom.utils.math.Point;
import net.lugocorp.kingdom.utils.math.Rect;
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
        int x = bounds.x;
        int w = bounds.w / this.getColumns();
        for (MenuNode child : this.children) {
            final Rect r = new Rect(x, bounds.y, w, child.getHeight());
            child.draw(av, r);
            x += w;
        }
    }

    /** {@inheritdoc} */
    @Override
    public void click(Rect bounds, Point p) {
        int x = bounds.x;
        int w = bounds.w / this.getColumns();
        for (MenuNode child : this.children) {
            final Rect r = new Rect(x, bounds.y, w, child.getHeight());
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
        if (bounds.contains(prev) || bounds.contains(curr)) {
            int x = bounds.x;
            int w = bounds.w / this.getColumns();
            for (MenuNode child : this.children) {
                final Rect r = new Rect(x, bounds.y, w, child.getHeight());
                child.mouseMoved(r, prev, curr);
                x += w;
            }
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
