package net.lugocorp.kingdom.ui.nodes;
import net.lugocorp.kingdom.engine.AudioVideo;
import net.lugocorp.kingdom.ui.ColorScheme;
import net.lugocorp.kingdom.ui.Menu;
import net.lugocorp.kingdom.utils.math.Point;
import net.lugocorp.kingdom.utils.math.Rect;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import java.util.Optional;

/**
 * Like a TextNode but it stands out and does something when you click it
 */
public class NakedButtonNode extends TextNode {
    private final Runnable action;
    private Optional<String> ping = Optional.of("sfx/arrow");
    private Point size = new Point(0, 0);
    private boolean hovered = false;

    public NakedButtonNode(AudioVideo av, String message, Runnable action) {
        super(av, message);
        this.action = action;
    }

    /** {@inheritdoc} */
    @Override
    protected BitmapFont getFont() {
        return this.av.fonts.getFont(24, this.hovered ? ColorScheme.HOVER : ColorScheme.BUTTON);
    }

    /**
     * Returns whether or not the mouse is hovering over this ButtonNode
     */
    protected boolean isHovered() {
        return this.hovered;
    }

    /**
     * Sets the noise that plays when you click this ButtonNode
     */
    public NakedButtonNode setNoise(String ping) {
        this.ping = Optional.of(ping);
        return this;
    }

    /**
     * Sets no noise to play when you click this ButtonNode
     */
    public NakedButtonNode disableNoise() {
        this.ping = Optional.empty();
        return this;
    }

    /**
     * Converts a set of bounds into this ButtonNode's inner bounds
     */
    protected Rect getInnerBounds(Rect b) {
        return this.isCentered()
                ? new Rect(b.x + ((b.w - this.size.x) / 2), b.y + ((b.h - this.size.y) / 2), this.size.x, this.size.y)
                : new Rect(b.x, b.y, this.size.x, this.size.y);
    }

    /** {@inheritdoc} */
    @Override
    public void pack(Menu menu, int width) {
        super.pack(menu, width);
        if (this.lines.size() == 0) {
            return;
        }

        // Find the exact Text bounds
        final GlyphLayout layout = new GlyphLayout();
        final BitmapFont font = this.getFont();
        for (TextNode.Line l : this.lines) {
            layout.setText(font, l.text);
            this.size.x = (int) Math.max(this.size.x, layout.width);
        }
        this.size.y = (int) layout.height;
    }

    /** {@inheritdoc} */
    @Override
    public void click(Rect bounds, Point p) {
        final Rect inner = this.getInnerBounds(bounds);
        if (inner.contains(p)) {
            this.ping.ifPresent((String sound) -> this.av.loaders.sounds.play(sound));
            this.action.run();
        }
    }

    /** {@inheritdoc} */
    @Override
    public void mouseMoved(Rect bounds, Point prev, Point curr) {
        final Rect inner = this.getInnerBounds(bounds);
        final boolean prevIn = inner.contains(prev);
        final boolean currIn = inner.contains(curr);
        if (!prevIn && currIn) {
            this.hovered = true;
        }
        if (prevIn && !currIn) {
            this.hovered = false;
        }
    }
}
