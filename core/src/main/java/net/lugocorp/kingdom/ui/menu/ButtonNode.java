package net.lugocorp.kingdom.ui.menu;
import net.lugocorp.kingdom.engine.Graphics;
import net.lugocorp.kingdom.math.Point;
import net.lugocorp.kingdom.math.Rect;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

/**
 * Like a TextNode but it stands out and does something when you click it
 */
public class ButtonNode extends TextNode {
    private final BitmapFont disabledFont;
    private final Runnable action;
    private boolean disabled = false;

    public ButtonNode(Graphics graphics, String message, Runnable action) {
        super(graphics, message);
        this.disabledFont = graphics.fonts.basic;
        this.font = graphics.fonts.button;
        this.action = action;
    }

    /**
     * Disables this ButtonNode so it cannot be clicked
     */
    public void disable() {
        this.font = this.disabledFont;
        this.disabled = true;
    }

    /** {@inheritdoc} */
    @Override
    public void click(Menu menu, Rect bounds, Point p) {
        if (!this.disabled) {
            this.action.run();
        }
    }
}
