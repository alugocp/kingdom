package net.lugocorp.kingdom.ui.nodes;
import net.lugocorp.kingdom.engine.AudioVideo;
import net.lugocorp.kingdom.ui.ColorScheme;
import net.lugocorp.kingdom.utils.math.Coords;
import net.lugocorp.kingdom.utils.math.Point;
import net.lugocorp.kingdom.utils.math.Rect;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Like a TextNode but it stands out and does something when you click it
 */
public class ButtonNode extends TextNode {
    private static final int OUTER_MARGIN = 2;
    private final Runnable action;
    private Optional<Supplier<Boolean>> criteria = Optional.empty();
    private boolean disabled = false;
    private boolean hovered = false;
    private String ping = "sfx/arrow";

    public ButtonNode(AudioVideo av, String message, Runnable action) {
        super(av, message);
        this.action = action;
    }

    /**
     * Returns the current background Color
     */
    private Color getColor() {
        if (this.disabled) {
            return ColorScheme.DISABLE;
        }
        return this.hovered ? ColorScheme.HOVER : ColorScheme.BUTTON;
    }

    /** {@inheritdoc} */
    @Override
    protected BitmapFont getFont() {
        return this.av.fonts.getFont(24, ColorScheme.TEXT);
    }

    /**
     * Returns whether or not the mouse is hovering over this ButtonNode
     */
    protected boolean isHovered() {
        return this.hovered;
    }

    /**
     * Returns whether or not this ButtonNode is enabled
     */
    protected boolean isEnabled() {
        return !this.disabled;
    }

    /**
     * Disables this ButtonNode based on some criteria
     */
    public ButtonNode enable(boolean criteria) {
        this.disabled = !criteria;
        return this;
    }

    /**
     * Sets a dynamic criteria that sets this ButtonNode's enabled/disabled state
     */
    public ButtonNode setEnabledCriteria(Supplier<Boolean> supplier) {
        this.criteria = Optional.of(supplier);
        return this;
    }

    /**
     * Sets the noise that plays when you click this ButtonNode
     */
    public ButtonNode setNoise(String ping) {
        this.ping = ping;
        return this;
    }

    /**
     * Converts a set of bounds into this ButtonNode's inner bounds
     */
    private Rect getInnerBounds(Rect b) {
        return new Rect(b.x + ButtonNode.OUTER_MARGIN, b.y + ButtonNode.OUTER_MARGIN,
                b.w - (ButtonNode.OUTER_MARGIN * 2), b.h - (ButtonNode.OUTER_MARGIN * 2));
    }

    /** {@inheritdoc} */
    @Override
    public void draw(AudioVideo av, Rect bounds) {
        this.criteria.ifPresent((Supplier<Boolean> supplier) -> this.enable(supplier.get()));

        // Draw the background
        final Rect bg = Coords.screen.flip(this.getInnerBounds(bounds));
        av.shapes.begin(ShapeType.Filled);
        av.shapes.setColor(this.getColor());
        av.shapes.rect(bg.x, bg.y, bg.w, bg.h);
        av.shapes.end();

        // Draw the actual text
        super.draw(av, bounds);
    }

    /** {@inheritdoc} */
    @Override
    public void click(Rect bounds, Point p) {
        final Rect inner = this.getInnerBounds(bounds);
        if (!this.disabled && inner.contains(p)) {
            this.av.loaders.sounds.play(this.ping);
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
