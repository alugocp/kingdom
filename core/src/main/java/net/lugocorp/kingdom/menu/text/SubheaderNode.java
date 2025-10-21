package net.lugocorp.kingdom.menu.text;
import net.lugocorp.kingdom.color.ColorScheme;
import net.lugocorp.kingdom.engine.AudioVideo;
import net.lugocorp.kingdom.engine.assets.FontService;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

/**
 * Like a TextNode but with larger, bolder text
 */
public class SubheaderNode extends TextNode {

    public SubheaderNode(AudioVideo av, String message) {
        super(av, message);
    }

    /** {@inheritdoc} */
    protected int getMargin() {
        return 8;
    }

    /** {@inheritdoc} */
    protected BitmapFont getFont() {
        return this.av.fonts.getFont(FontService.BOLD, 22, ColorScheme.TEXT.color);
    }
}
