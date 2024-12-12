package net.lugocorp.kingdom.ui.menu;
import net.lugocorp.kingdom.engine.Graphics;

/**
 * Like a TextNode but with larger, bolder text
 */
public class HeaderNode extends TextNode {
    public HeaderNode(Graphics graphics, String message) {
        super(graphics, message);
        this.font = graphics.fonts.header;
    }

    /** {@inheritdoc} */
    protected int getMargin() {
        return 10;
    }
}
