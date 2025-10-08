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
 * A prettier version of the NakedButtonNode
 */
public class ButtonNode extends NakedButtonNode {
    private static final int OUTER_MARGIN = 2;
    private Optional<Supplier<Boolean>> criteria = Optional.empty();
    private boolean disabled = false;

    public ButtonNode(AudioVideo av, String message, Runnable action) {
        super(av, message, action);
        this.center();
    }

    /**
     * Returns the current background Color
     */
    protected Color getColor() {
        if (this.disabled) {
            return ColorScheme.DISABLE.color;
        }
        return this.isHovered() ? ColorScheme.HOVER.color : ColorScheme.BUTTON.color;
    }

    /** {@inheritdoc} */
    @Override
    protected BitmapFont getFont() {
        return this.av.fonts.getFont(24, this.disabled ? ColorScheme.GUTTER.color : ColorScheme.TEXT.color);
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

    /** {@inheritdoc} */
    @Override
    protected Rect getInnerBounds(Rect b) {
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
        if (inner.contains(p)) {
            if (this.disabled) {
                this.av.loaders.sounds.play("sfx/error");
            } else {
                super.click(bounds, p);
            }
        }
    }
}
