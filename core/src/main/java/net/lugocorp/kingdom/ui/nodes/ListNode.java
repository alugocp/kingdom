package net.lugocorp.kingdom.ui.nodes;
import net.lugocorp.kingdom.engine.AudioVideo;
import net.lugocorp.kingdom.ui.ColorScheme;
import net.lugocorp.kingdom.ui.Menu;
import net.lugocorp.kingdom.ui.MenuNode;
import net.lugocorp.kingdom.utils.math.Coords;
import net.lugocorp.kingdom.utils.math.Point;
import net.lugocorp.kingdom.utils.math.Rect;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import java.util.ArrayList;
import java.util.List;

/**
 * MenuNode item containing many child nodes
 */
public class ListNode implements MenuNode {
    private final List<MenuNode> children = new ArrayList<>();
    private boolean border = false;
    private int margin = 0;

    /**
     * Adds a child MenuNode to this ListNode
     */
    public ListNode add(MenuNode child) {
        this.children.add(child);
        return this;
    }

    /**
     * Clears the child MenuNodes form this ListNode
     */
    public void clear() {
        this.children.clear();
    }

    /**
     * Removes the last child in this ListNode
     */
    public void pop() {
        this.children.remove(this.children.size() - 1);
    }

    /**
     * Adds a border (and some margin) to this ListNode
     */
    public ListNode addBorder() {
        this.border = true;
        this.margin = 3;
        return this;
    }

    /** {@inheritdoc} */
    @Override
    public int getHeight() {
        int h = this.margin * 2;
        for (MenuNode child : this.children) {
            h += child.getHeight();
        }
        return h;
    }

    /** {@inheritdoc} */
    @Override
    public void pack(Menu menu, int width) {
        for (MenuNode child : this.children) {
            child.pack(menu, width - (this.margin * 2));
        }
    }

    /** {@inheritdoc} */
    @Override
    public void draw(AudioVideo av, Rect bounds) {
        int y = bounds.y + this.margin;
        for (MenuNode child : this.children) {
            final Rect r = new Rect(bounds.x + this.margin, y, bounds.w - (this.margin * 2), child.getHeight());
            child.draw(av, r);
            y += r.h;
        }
        if (this.border) {
            Rect flip = Coords.screen.flip(bounds.x, bounds.y, bounds.w, bounds.h - 1);
            av.shapes.begin(ShapeType.Line);
            av.shapes.setColor(ColorScheme.OUTLINE.color);
            av.shapes.rect(flip.x, flip.y, flip.w, flip.h);
            av.shapes.end();
        }
    }

    /** {@inheritdoc} */
    @Override
    public void click(Rect bounds, Point p) {
        int y = bounds.y + this.margin;
        for (MenuNode child : this.children) {
            final Rect r = new Rect(bounds.x + this.margin, y, bounds.w - (this.margin * 2), child.getHeight());
            if (r.contains(p)) {
                child.click(r, p);
            } else {
                child.unclick();
            }
            y += r.h;
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
        int y = bounds.y + this.margin;
        for (MenuNode child : this.children) {
            final Rect r = new Rect(bounds.x + this.margin, y, bounds.w - (this.margin * 2), child.getHeight());
            child.mouseMoved(r, prev, curr);
            y += r.h;
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
