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
    private final List<Column> children = new ArrayList<>();
    private Optional<Integer> columns = Optional.empty();

    /**
     * Organizes a set of MenuNodes into a grid with specified max columns
     */
    public static ListNode packIntoRows(int cols, List<MenuNode> nodes) {
        final ListNode rows = new ListNode();
        final int fullRows = nodes.size() / cols;
        final int finalRow = nodes.size() % cols;
        for (int a = 0; a < fullRows; a++) {
            final RowNode row = new RowNode().setColumns(cols);
            for (int b = 0; b < cols; b++) {
                row.add(nodes.get((a * cols) + b));
            }
            rows.add(row);
        }
        if (finalRow > 0) {
            final RowNode row = new RowNode().setColumns(cols);
            for (int a = 0; a < finalRow; a++) {
                row.add(nodes.get((fullRows * cols) + a));
            }
            rows.add(row);
        }
        return rows;
    }

    /**
     * Adds a child to this RowNode with ColumnType.EQUAL
     */
    public RowNode add(MenuNode child) {
        this.children.add(this.columns.map((Integer n) -> new Column(ColumnType.RATIO, 100 / n, child))
                .orElse(new Column(ColumnType.EQUAL, 0, child)));
        return this;
    }

    /**
     * Adds a child to this RowNode with ColumnType.EXACT
     */
    public RowNode addExact(int value, MenuNode child) {
        if (this.columns.isPresent()) {
            throw new RuntimeException("Cannot use fine-grained RowNode controls where auto ratio is set");
        }
        this.children.add(new Column(ColumnType.EXACT, value, child));
        return this;
    }

    /**
     * Adds a child to this RowNode with ColumnType.RATIO
     */
    public RowNode addRatio(int value, MenuNode child) {
        if (this.columns.isPresent()) {
            throw new RuntimeException("Cannot use fine-grained RowNode controls where auto ratio is set");
        }
        this.children.add(new Column(ColumnType.RATIO, value, child));
        return this;
    }

    /**
     * Calculates the width of the given Column
     */
    private int getWidth(Column child, int total) {
        int remaining = total;
        int totalEquals = 0;
        int totalRatio = 0;

        // ColumnType.EXACT is easy
        if (child.getType() == ColumnType.EXACT) {
            return child.getValue();
        }

        // ColumnType.RATIO must get the remaining width
        for (Column n : this.children) {
            if (n.getType() == ColumnType.EXACT) {
                remaining -= n.getValue();
            }
            if (n.getType() == ColumnType.RATIO) {
                totalRatio += n.getValue();
            }
            if (n.getType() == ColumnType.EQUAL) {
                totalEquals++;
            }
        }
        if (child.getType() == ColumnType.RATIO) {
            return remaining * child.getValue() / 100;
        }

        // ColumnType.EQUAL must get the remainder of the remaining width
        return remaining * (100 - totalRatio) / (100 * totalEquals);
    }

    /**
     * Sets a static number of columns to display in this RowNode
     */
    public RowNode setColumns(int columns) {
        this.columns = Optional.of(columns);
        return this;
    }

    /**
     * Returns a version of this RowNode as a ListNode
     */
    public ListNode toListNode() {
        final ListNode ls = new ListNode();
        for (Column n : this.children) {
            ls.add(n.getNode());
        }
        return ls;
    }

    /** {@inheritdoc} */
    @Override
    public int getHeight() {
        int h = 0;
        for (Column n : this.children) {
            h = Math.max(h, n.getNode().getHeight());
        }
        return h;
    }

    /** {@inheritdoc} */
    @Override
    public void pack(Menu menu, int width) {
        for (Column n : this.children) {
            n.getNode().pack(menu, this.getWidth(n, width));
        }
    }

    /** {@inheritdoc} */
    @Override
    public void draw(AudioVideo av, Rect bounds) {
        final int h = this.getHeight();
        int x = bounds.x;
        for (Column n : this.children) {
            final int w = this.getWidth(n, bounds.w);
            final Rect r = new Rect(x, bounds.y, w, h);
            n.getNode().draw(av, r);
            x += w;
        }
    }

    /** {@inheritdoc} */
    @Override
    public void click(Rect bounds, Point p) {
        final int h = this.getHeight();
        int x = bounds.x;
        for (Column n : this.children) {
            final int w = this.getWidth(n, bounds.w);
            final Rect r = new Rect(x, bounds.y, w, h);
            if (r.contains(p)) {
                n.getNode().click(r, p);
            } else {
                n.getNode().unclick();
            }
            x += w;
        }
    }

    /** {@inheritdoc} */
    @Override
    public void unclick() {
        for (Column n : this.children) {
            n.getNode().unclick();
        }
    }

    /** {@inheritdoc} */
    @Override
    public void mouseMoved(Rect bounds, Point prev, Point curr) {
        final int h = this.getHeight();
        int x = bounds.x;
        for (Column n : this.children) {
            final int w = this.getWidth(n, bounds.w);
            final Rect r = new Rect(x, bounds.y, w, h);
            n.getNode().mouseMoved(r, prev, curr);
            x += w;
        }
    }

    /** {@inheritdoc} */
    @Override
    public void keyPressed(int keycode) {
        for (Column n : this.children) {
            n.getNode().keyPressed(keycode);
        }
    }
}
