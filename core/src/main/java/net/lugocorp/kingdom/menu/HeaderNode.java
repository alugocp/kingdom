package net.lugocorp.kingdom.menu;
import net.lugocorp.kingdom.engine.Graphics;

public class HeaderNode extends TextNode {
    public HeaderNode(Graphics graphics, String message) {
        super(graphics, message);
        this.font = graphics.fonts.header;
    }
}
