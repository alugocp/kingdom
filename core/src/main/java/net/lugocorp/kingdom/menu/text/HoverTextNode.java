package net.lugocorp.kingdom.menu.text;
import net.lugocorp.kingdom.color.ColorScheme;
import net.lugocorp.kingdom.engine.AudioVideo;
import net.lugocorp.kingdom.engine.assets.FontParam;
import net.lugocorp.kingdom.math.Point;
import net.lugocorp.kingdom.math.Rect;
import net.lugocorp.kingdom.menu.Menu;
import net.lugocorp.kingdom.menu.MenuNode;
import net.lugocorp.kingdom.menu.MenuPopup;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

/**
 * TextNode that shows a popup on hover
 */
public class HoverTextNode extends TextNode {
    private final MenuPopup popup = new MenuPopup();
    private final MenuNode root;

    public HoverTextNode(AudioVideo av, String message, MenuNode root) {
        super(av, message);
        this.root = root;
    }

    /** {@inheritdoc} */
    @Override
    protected BitmapFont getFont() {
        return this.av.fonts.getFont(new FontParam().setSize(22)
                .setColor(this.popup.isHovered() ? ColorScheme.HOVER.color : ColorScheme.BUTTON.color));
    }

    /** {@inheritdoc} */
    @Override
    public void pack(Menu menu, int width) {
        super.pack(menu, width);
        this.popup.setMenu(menu);
    }

    /** {@inheritdoc} */
    @Override
    public void mouseMoved(Rect bounds, Point prev, Point curr) {
        this.popup.update(bounds, curr, root);
    }
}
