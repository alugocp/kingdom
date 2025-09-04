package net.lugocorp.kingdom.ui.menu;
import net.lugocorp.kingdom.engine.AudioVideo;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

/**
 * Like a TextNode but with larger, bolder text
 */
public class HeaderNode extends TextNode {

    public HeaderNode(AudioVideo av, String message) {
        super(av, message);
    }

    /** {@inheritdoc} */
    protected int getMargin() {
        return 10;
    }

    /** {@inheritdoc} */
    protected BitmapFont getFont() {
        return this.av.fonts.header;
    }
}
