package net.lugocorp.kingdom.menu.input;
import net.lugocorp.kingdom.color.ColorScheme;
import net.lugocorp.kingdom.engine.AudioVideo;
import net.lugocorp.kingdom.menu.Menu;
import net.lugocorp.kingdom.menu.MenuNode;
import net.lugocorp.kingdom.menu.text.TextNode;
import net.lugocorp.kingdom.utils.math.Coords;
import net.lugocorp.kingdom.utils.math.Point;
import net.lugocorp.kingdom.utils.math.Rect;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * MenuNode item representing a radio box
 */
public class OptionsNode implements MenuNode {
    private static final int RADIUS = 8;
    private static final int MARGIN = 10;
    private final List<TextNode> options = new ArrayList<>();
    private final Consumer<Integer> selected;
    private final AudioVideo av;
    private int index = 0;
    protected BitmapFont font;

    public OptionsNode(AudioVideo av, Consumer<Integer> selected) {
        this.selected = selected;
        this.av = av;
    }

    public OptionsNode add(String label) {
        this.options.add(new TextNode(av, label));
        return this;
    }

    /** {@inheritdoc} */
    @Override
    public int getHeight() {
        int h = 0;
        for (TextNode n : this.options) {
            h += n.getHeight();
        }
        return h;
    }

    /** {@inheritdoc} */
    @Override
    public void pack(Menu menu, int width) {
        final int w = width - OptionsNode.MARGIN - (OptionsNode.RADIUS * 2);
        for (TextNode n : this.options) {
            n.pack(menu, w);
        }
    }

    /** {@inheritdoc} */
    @Override
    public void draw(AudioVideo av, Rect bounds) {
        int y = bounds.y;
        final int x = bounds.x + OptionsNode.MARGIN + (OptionsNode.RADIUS * 2);
        final int w = bounds.w - OptionsNode.MARGIN - (OptionsNode.RADIUS * 2);
        for (int a = 0; a < this.options.size(); a++) {
            final Rect r = new Rect(x, y, w, this.options.get(a).getHeight());
            av.shapes.setColor(ColorScheme.BUTTON.color);
            av.shapes.begin(this.index == a ? ShapeType.Filled : ShapeType.Line);
            av.shapes.circle(bounds.x + OptionsNode.RADIUS, Coords.SIZE.y - y - (OptionsNode.RADIUS * 2),
                    OptionsNode.RADIUS);
            av.shapes.end();
            this.options.get(a).draw(av, r);
            y += r.h;
        }
    }

    /** {@inheritdoc} */
    @Override
    public void click(Rect bounds, Point p) {
        int y = bounds.y;
        for (int a = 0; a < this.options.size(); a++) {
            final Rect r = new Rect(bounds.x, y, bounds.w, this.options.get(a).getHeight());
            if (r.contains(p)) {
                this.av.loaders.sounds.play("sfx/arrow");
                this.selected.accept(a);
                this.index = a;
            }
            y += r.h;
        }
    }
}
