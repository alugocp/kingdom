package net.lugocorp.kingdom.menu;
import net.lugocorp.kingdom.engine.Graphics;
import net.lugocorp.kingdom.math.Point;
import net.lugocorp.kingdom.math.Rect;

public class ButtonNode extends TextNode {
    private final Runnable action;

    public ButtonNode(Graphics graphics, String message, Runnable action) {
        super(graphics, message);
        this.font = graphics.fonts.button;
        this.action = action;
    }

    /** {@inheritdoc} */
    @Override
    public void click(Rect bounds, Point p) {
        this.action.run();
    }
}
