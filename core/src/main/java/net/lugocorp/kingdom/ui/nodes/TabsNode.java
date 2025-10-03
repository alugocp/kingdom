package net.lugocorp.kingdom.ui.nodes;
import net.lugocorp.kingdom.engine.AudioVideo;
import net.lugocorp.kingdom.ui.ColorScheme;
import net.lugocorp.kingdom.ui.Menu;
import net.lugocorp.kingdom.ui.MenuNode;
import net.lugocorp.kingdom.utils.code.Tuple;
import net.lugocorp.kingdom.utils.math.Coords;
import net.lugocorp.kingdom.utils.math.Point;
import net.lugocorp.kingdom.utils.math.Rect;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import java.util.ArrayList;
import java.util.List;

/**
 * This MenuNode allows us to swap between different MenuNodes as tabs
 */
public class TabsNode implements MenuNode {
    private final List<Tuple<String, MenuNode>> data = new ArrayList<>();
    private final RowNode tabs = new RowNode();
    private final AudioVideo av;
    private int selected = 0;

    public TabsNode(AudioVideo av) {
        this.av = av;
    }

    /**
     * Adds a new tab with a label and content
     */
    public TabsNode add(String label, MenuNode root) {
        final int index = this.data.size();
        this.data.add(new Tuple<String, MenuNode>(label, root));
        this.tabs.add(new ButtonNode(this.av, label, () -> {
            this.selected = index;
        }).center());
        return this;
    }

    /**
     * Returns the currently displayed content
     */
    private MenuNode content() {
        return this.data.get(this.selected).b;
    }

    /** {@inheritdoc} */
    @Override
    public int getHeight() {
        return this.tabs.getHeight() + this.content().getHeight();
    }

    /** {@inheritdoc} */
    @Override
    public void draw(AudioVideo av, Rect bounds) {
        // Draw the tab highlight
        final int w = bounds.w / this.data.size();
        final int h = this.tabs.getHeight();
        av.shapes.begin(ShapeType.Filled);
        av.shapes.setColor(ColorScheme.GUTTER);
        av.shapes.rect(bounds.x + (w * this.selected), Coords.SIZE.y - bounds.y - h, w, h);
        av.shapes.end();

        // Draw tabs and content
        this.tabs.draw(av, bounds);
        final Rect r = new Rect(bounds.x, bounds.y + h, bounds.w, bounds.h - h);
        this.content().draw(av, r);
    }

    /** {@inheritdoc} */
    @Override
    public void pack(Menu menu, int width) {
        this.tabs.pack(menu, width);
        for (Tuple<String, MenuNode> tab : this.data) {
            tab.b.pack(menu, width);
        }
    }

    /** {@inheritdoc} */
    @Override
    public void click(Rect bounds, Point p) {
        final int h = this.tabs.getHeight();
        final Rect r1 = new Rect(bounds.x, bounds.y, bounds.w, h);
        if (r1.contains(p)) {
            this.tabs.click(r1, p);
        } else {
            this.tabs.unclick();
        }

        final Rect r2 = new Rect(bounds.x, bounds.y + h, bounds.w, this.getHeight() - h);
        if (r2.contains(p)) {
            this.content().click(r2, p);
        } else {
            this.content().unclick();
        }
    }

    /** {@inheritdoc} */
    @Override
    public void unclick() {
        this.tabs.unclick();
        for (Tuple<String, MenuNode> tab : this.data) {
            tab.b.unclick();
        }
    }

    /** {@inheritdoc} */
    @Override
    public void mouseMoved(Rect bounds, Point prev, Point curr) {
        final int h = this.tabs.getHeight();
        final Rect r1 = new Rect(bounds.x, bounds.y, bounds.w, h);
        if (r1.contains(prev) || r1.contains(curr)) {
            this.tabs.mouseMoved(r1, prev, curr);
        }

        final Rect r2 = new Rect(bounds.x, bounds.y + h, bounds.w, this.getHeight() - h);
        if (r2.contains(prev) || r2.contains(curr)) {
            this.content().mouseMoved(r2, prev, curr);
        }
    }

    /** {@inheritdoc} */
    @Override
    public void keyPressed(int keycode) {
        this.tabs.keyPressed(keycode);
        for (Tuple<String, MenuNode> tab : this.data) {
            tab.b.keyPressed(keycode);
        }
    }
}
