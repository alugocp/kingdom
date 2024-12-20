package net.lugocorp.kingdom.ui.menu;
import net.lugocorp.kingdom.engine.AudioVideo;

/**
 * Like a TextNode but with larger, bolder text
 */
public class HeaderNode extends TextNode {
    public HeaderNode(AudioVideo av, String message) {
        super(av, message);
        this.font = av.fonts.header;
    }

    /** {@inheritdoc} */
    protected int getMargin() {
        return 10;
    }
}
