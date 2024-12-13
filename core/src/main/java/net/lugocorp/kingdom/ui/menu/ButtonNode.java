package net.lugocorp.kingdom.ui.menu;
import net.lugocorp.kingdom.engine.Graphics;
import net.lugocorp.kingdom.utils.math.Point;
import net.lugocorp.kingdom.utils.math.Rect;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Like a TextNode but it stands out and does something when you click it
 */
public class ButtonNode extends TextNode {
    private final BitmapFont disabledFont;
    private final BitmapFont enabledFont;
    private final Runnable action;
    private Optional<Supplier<Boolean>> criteria = Optional.empty();
    private boolean disabled = false;

    public ButtonNode(Graphics graphics, String message, Runnable action) {
        super(graphics, message);
        this.disabledFont = graphics.fonts.basic;
        this.enabledFont = graphics.fonts.button;
        this.font = graphics.fonts.button;
        this.action = action;
    }

    /**
     * Enables this ButtonNode so it can be clicked
     */
    public void enable() {
        this.font = this.enabledFont;
        this.disabled = false;
    }

    /**
     * Disables this ButtonNode so it cannot be clicked
     */
    public void disable() {
        this.font = this.disabledFont;
        this.disabled = true;
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
    public void draw(Graphics graphics, Rect bounds) {
        this.criteria.ifPresent((Supplier<Boolean> supplier) -> {
            if (supplier.get()) {
                this.enable();
            } else {
                this.disable();
            }
        });
        super.draw(graphics, bounds);
    }

    /** {@inheritdoc} */
    @Override
    public void click(Menu menu, Rect bounds, Point p) {
        // TODO I don't think this works when Menu scroll is > 0, or it could have to do
        // with window resizing
        if (!this.disabled) {
            this.action.run();
        }
    }
}
