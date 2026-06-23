package net.lugocorp.kingdom.menu.input;
import net.lugocorp.kingdom.color.ColorScheme;
import net.lugocorp.kingdom.engine.AudioVideo;
import net.lugocorp.kingdom.math.Coords;
import net.lugocorp.kingdom.math.Point;
import net.lugocorp.kingdom.math.Rect;
import net.lugocorp.kingdom.menu.Menu;
import net.lugocorp.kingdom.menu.MenuNode;
import net.lugocorp.kingdom.menu.text.HoverTextNode;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * MenuNode item representing dropdown selection UI
 */
public class DropdownNode implements MenuNode {
    private static final int MARGINX = 10;
    private static final int MARGINY = 2;
    private final List<HoverTextNode> options = new ArrayList<>();
    private final Consumer<Integer> selected;
    private final AudioVideo av;
    private boolean opened = false;
    private int index = 0;

    public DropdownNode(AudioVideo av, int index, Consumer<Integer> selected) {
        this.selected = selected;
        this.index = index;
        this.av = av;
    }

    /**
     * Adds another option to this node
     */
    public DropdownNode add(String label, MenuNode root) {
        this.options.add(new HoverTextNode(av, label, root));
        return this;
    }

    /** {@inheritdoc} */
    @Override
    public int getHeight() {
        if (this.opened) {
            int h = DropdownNode.MARGINY * 2;
            for (HoverTextNode n : this.options) {
                h += n.getHeight();
            }
            return h;
        }
        return this.options.get(this.index).getHeight() + (DropdownNode.MARGINY * 2);
    }

    /** {@inheritdoc} */
    @Override
    public void pack(Menu menu, int width) {
        final int w = width - (DropdownNode.MARGINX * 2);
        for (HoverTextNode n : this.options) {
            n.pack(menu, w);
        }
    }

    /** {@inheritdoc} */
    @Override
    public void draw(AudioVideo av, Rect bounds) {
        final int x = bounds.x + DropdownNode.MARGINX;
        final int w = bounds.w - (DropdownNode.MARGINX * 2);
        int y = bounds.y + DropdownNode.MARGINY;

        // Draw surrounding border
        final Rect bg = Coords.screen.flip(bounds);
        av.shapes.begin(ShapeType.Line);
        av.shapes.setColor(ColorScheme.WHITE.color);
        av.shapes.rect(bg.x, bg.y, bg.w, bg.h);
        av.shapes.end();

        // Draw the option text
        if (this.opened) {
            for (int a = 0; a < this.options.size(); a++) {
                final Rect r = new Rect(x, y, w, this.options.get(a).getHeight());
                this.options.get(a).draw(av, r);
                y += r.h;
            }
        } else {
            final Rect r = new Rect(x, y, w, this.options.get(this.index).getHeight());
            this.options.get(this.index).draw(av, r);
        }
    }

    /** {@inheritdoc} */
    @Override
    public void click(Rect bounds, Point p) {
        if (this.opened) {
            int y = bounds.y;
            for (int a = 0; a < this.options.size(); a++) {
                final Rect r = new Rect(bounds.x, y + DropdownNode.MARGINY, bounds.w, this.options.get(a).getHeight());
                if (r.contains(p)) {
                    this.selected.accept(a);
                    this.index = a;
                }
                y += r.h;
            }
            this.opened = false;
        } else {
            this.opened = true;
        }
        this.av.loaders.sounds.play("sfx/arrow");
    }

    /** {@inheritdoc} */
    @Override
    public void mouseMoved(Rect bounds, Point prev, Point curr) {
        if (!this.opened) {
            return;
        }
        int y = bounds.y + DropdownNode.MARGINY;
        for (HoverTextNode child : this.options) {
            final Rect r = new Rect(bounds.x, y, bounds.w, child.getHeight());
            child.mouseMoved(r, prev, curr);
            y += r.h;
        }
    }
}
