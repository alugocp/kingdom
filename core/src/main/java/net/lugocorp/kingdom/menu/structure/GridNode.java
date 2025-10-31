package net.lugocorp.kingdom.menu.structure;
import net.lugocorp.kingdom.engine.AudioVideo;
import net.lugocorp.kingdom.math.Point;
import net.lugocorp.kingdom.math.Rect;
import net.lugocorp.kingdom.menu.Menu;
import net.lugocorp.kingdom.menu.MenuNode;
import java.util.ArrayList;
import java.util.List;

/**
 * This class organizes child nodes into a grid (automatically recommends the
 * right number of columns)
 */
public class GridNode implements MenuNode {
    private static final int MARGIN = 2;
    private final List<MenuNode> nodes = new ArrayList<>();
    private final Point itemSize;
    private int cols;
    private int rows;

    public GridNode(Point itemSize) {
        this.itemSize = itemSize;
    }

    /**
     * Adds a child element to this GridNode
     */
    public GridNode add(MenuNode n) {
        this.nodes.add(n);
        return this;
    }

    /**
     * Returns the position of an element at the given grid coordinates
     */
    protected Rect getNodePos(Rect bounds, int x, int y) {
        return new Rect(bounds.x + (x * (this.itemSize.x + GridNode.MARGIN)),
                bounds.y + (y * (this.itemSize.y + GridNode.MARGIN)), this.itemSize.x, this.itemSize.y);
    }

    /** {@inheritdoc} */
    @Override
    public int getHeight() {
        return this.rows * (this.itemSize.y + GridNode.MARGIN);
    }

    /** {@inheritdoc} */
    @Override
    public void pack(Menu menu, int width) {
        this.cols = (int) (width / (this.itemSize.x + GridNode.MARGIN));
        this.rows = this.nodes.size() == 0 ? 0 : (int) Math.ceil(this.nodes.size() / (float) this.cols);
        for (MenuNode n : this.nodes) {
            n.pack(menu, this.itemSize.x);
        }
    }

    /** {@inheritdoc} */
    @Override
    public void draw(AudioVideo av, Rect bounds) {
        if (this.nodes.size() == 0) {
            return;
        }
        for (int a = 0; a < this.rows; a++) {
            for (int b = 0; b < this.cols; b++) {
                final int index = (a * this.cols) + b;
                if (index >= this.nodes.size()) {
                    break;
                }
                this.nodes.get(index).draw(av, this.getNodePos(bounds, b, a));
            }
        }
    }

    /** {@inheritdoc} */
    @Override
    public void click(Rect bounds, Point p) {
        for (int a = 0; a < this.rows; a++) {
            for (int b = 0; b < this.cols; b++) {
                final int index = (a * this.cols) + b;
                if (index >= this.nodes.size()) {
                    break;
                }
                final Rect r = this.getNodePos(bounds, b, a);
                if (r.contains(p)) {
                    this.nodes.get(index).click(r, p);
                } else {
                    this.nodes.get(index).unclick();
                }
            }
        }
    }

    /** {@inheritdoc} */
    @Override
    public void unclick() {
        for (MenuNode n : this.nodes) {
            n.unclick();
        }
    }

    /** {@inheritdoc} */
    @Override
    public void mouseMoved(Rect bounds, Point prev, Point curr) {
        for (int a = 0; a < this.rows; a++) {
            for (int b = 0; b < this.cols; b++) {
                final int index = (a * this.cols) + b;
                if (index >= this.nodes.size()) {
                    break;
                }
                final Rect r = this.getNodePos(bounds, b, a);
                this.nodes.get(index).mouseMoved(r, prev, curr);
            }
        }
    }
}
