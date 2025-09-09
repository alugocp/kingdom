package net.lugocorp.kingdom.ui.menu;
import net.lugocorp.kingdom.engine.AudioVideo;
import net.lugocorp.kingdom.utils.math.Point;
import net.lugocorp.kingdom.utils.math.Rect;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Like a TextNode but it stands out and does something when you click it
 */
public class ButtonNode extends TextNode {
    private final Runnable action;
    private Optional<Supplier<Boolean>> criteria = Optional.empty();
    private boolean disabled = false;
    private boolean hovered = false;

    public ButtonNode(AudioVideo av, String message, Runnable action) {
        super(av, message);
        this.action = action;
    }

    /** {@inheritdoc} */
    @Override
    protected BitmapFont getFont() {
        if (this.disabled) {
            return this.av.fonts.getFont(24, 0xffffff);
        }
        return this.hovered ? this.av.fonts.getFont(24, 0xbfffff) : this.av.fonts.getFont(24, 0x72ffff);
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

    /** {@inheritdoc} */
    @Override
    public void draw(AudioVideo av, Rect bounds) {
        this.criteria.ifPresent((Supplier<Boolean> supplier) -> this.enable(supplier.get()));
        super.draw(av, bounds);
    }

    /** {@inheritdoc} */
    @Override
    public void click(Menu menu, Rect bounds, Point p) {
        if (!this.disabled) {
            this.av.loaders.sounds.play("ui/arrow");
            this.action.run();
        }
    }

    /** {@inheritdoc} */
    @Override
    public void mouseMoved(Rect bounds, Point prev, Point curr) {
        final boolean prevIn = bounds.contains(prev);
        final boolean currIn = bounds.contains(curr);
        if (!prevIn && currIn) {
            this.hovered = true;
        }
        if (prevIn && !currIn) {
            this.hovered = false;
        }
    }
}
