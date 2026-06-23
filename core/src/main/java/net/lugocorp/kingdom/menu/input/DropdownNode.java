package net.lugocorp.kingdom.menu.input;
import net.lugocorp.kingdom.color.ColorScheme;
import net.lugocorp.kingdom.engine.AudioVideo;
import net.lugocorp.kingdom.engine.fonts.FontParam;
import net.lugocorp.kingdom.math.Coords;
import net.lugocorp.kingdom.math.Point;
import net.lugocorp.kingdom.math.Rect;
import net.lugocorp.kingdom.menu.Menu;
import net.lugocorp.kingdom.menu.MenuNode;
import net.lugocorp.kingdom.menu.text.HoverTextNode;
import net.lugocorp.kingdom.menu.text.NakedButtonNode;
import net.lugocorp.kingdom.menu.text.TextNode;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * MenuNode item representing dropdown selection UI
 */
public class DropdownNode implements MenuNode {
    private static final int MARGINX = 10;
    private static final int MARGINY = 2;
    private final List<TextNode> disabledOptions = new ArrayList<>();
    private final List<TextNode> options = new ArrayList<>();
    private final Function<Integer, Boolean> enableFunc;
    private final Consumer<Integer> selected;
    private final AudioVideo av;
    private boolean opened = false;
    private int index = 0;

    public DropdownNode(AudioVideo av, int index, Consumer<Integer> selected, Function<Integer, Boolean> enableFunc) {
        this.enableFunc = enableFunc;
        this.selected = selected;
        this.index = index;
        this.av = av;
    }

    /**
     * Returns either the enabled or disabled verison of the child node at the given
     * index
     */
    private TextNode getNode(int a) {
        return (this.enableFunc.apply(a) ? this.options : this.disabledOptions).get(a);
    }

    /**
     * Adds another option to this node without a PopupMenu
     */
    public DropdownNode add(String label) {
        this.options.add(new NakedButtonNode(av, label, () -> {
        }));
        this.disabledOptions.add(new TextNode(av, label) {
            /** {@inheritdoc} */
            @Override
            protected BitmapFont getFont() {
                return this.av.fonts.getFont(new FontParam().setSize(22).setColor(ColorScheme.TEXT.color));
            }
        });
        return this;
    }

    /**
     * Adds another option to this node with a PopupMenu
     */
    public DropdownNode add(String label, MenuNode root) {
        this.options.add(new HoverTextNode(av, label, root));
        this.disabledOptions.add(new TextNode(av, label) {
            /** {@inheritdoc} */
            @Override
            protected BitmapFont getFont() {
                return this.av.fonts.getFont(new FontParam().setSize(22).setColor(ColorScheme.TEXT.color));
            }
        });
        return this;
    }

    /** {@inheritdoc} */
    @Override
    public int getHeight() {
        if (this.opened) {
            int h = DropdownNode.MARGINY * 2;
            for (int a = 0; a < this.options.size(); a++) {
                h += this.getNode(a).getHeight();
            }
            return h;
        }
        return this.getNode(this.index).getHeight() + (DropdownNode.MARGINY * 2);
    }

    /** {@inheritdoc} */
    @Override
    public void pack(Menu menu, int width) {
        final int w = width - (DropdownNode.MARGINX * 2);
        for (TextNode n : this.options) {
            n.pack(menu, w);
        }
        for (TextNode n : this.disabledOptions) {
            n.pack(menu, w);
        }
    }

    /** {@inheritdoc} */
    @Override
    public void draw(AudioVideo av, Rect bounds) {
        final int x = bounds.x + DropdownNode.MARGINX;
        final int w = bounds.w - (DropdownNode.MARGINX * 2);
        int y = bounds.y + DropdownNode.MARGINY;

        // Draw black background
        final int h = this.getHeight();
        final Rect bg = Coords.screen.flip(bounds);
        av.shapes.begin(ShapeType.Filled);
        av.shapes.setColor(ColorScheme.BLACK.color);
        av.shapes.rect(bg.x, bg.y + bounds.h - h, bg.w, h);
        av.shapes.end();

        // Draw surrounding border
        av.shapes.begin(ShapeType.Line);
        av.shapes.setColor(ColorScheme.WHITE.color);
        av.shapes.rect(bg.x, bg.y + bounds.h - h, bg.w, h);
        av.shapes.end();

        // Draw the option text
        if (this.opened) {
            for (int a = 0; a < this.options.size(); a++) {
                final Rect r = new Rect(x, y, w, this.getNode(a).getHeight());
                this.getNode(a).draw(av, r);
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
            int y = bounds.y + DropdownNode.MARGINY;
            for (int a = 0; a < this.options.size(); a++) {
                final Rect r = new Rect(bounds.x, y, bounds.w, this.getNode(a).getHeight());
                if (r.contains(p) && this.enableFunc.apply(a)) {
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
    public void unclick() {
        this.opened = false;
    }

    /** {@inheritdoc} */
    @Override
    public void mouseMoved(Rect bounds, Point prev, Point curr) {
        if (!this.opened) {
            return;
        }
        int y = bounds.y + DropdownNode.MARGINY;
        for (int a = 0; a < this.options.size(); a++) {
            final TextNode child = this.getNode(a);
            final Rect r = new Rect(bounds.x, y, bounds.w, child.getHeight());
            child.mouseMoved(r, prev, curr);
            y += r.h;
        }
    }
}
