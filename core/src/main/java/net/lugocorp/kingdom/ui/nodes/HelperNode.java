package net.lugocorp.kingdom.ui.nodes;
import net.lugocorp.kingdom.engine.AudioVideo;
import net.lugocorp.kingdom.ui.ColorScheme;
import net.lugocorp.kingdom.ui.Menu;
import net.lugocorp.kingdom.ui.MenuNode;
import net.lugocorp.kingdom.ui.MenuPopup;
import net.lugocorp.kingdom.utils.math.Point;
import net.lugocorp.kingdom.utils.math.Rect;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

/**
 * A friendly popup that explains the more advanced mechanics to players
 */
public class HelperNode extends NakedButtonNode {
    private final MenuPopup popup = new MenuPopup();
    private final MenuNode desc;

    public HelperNode(AudioVideo av, MenuNode desc) {
        super(av, "Help", () -> {
        });
        this.desc = desc;
        this.disableNoise();
        this.center();
    }

    public HelperNode(AudioVideo av, String desc) {
        this(av, new TextNode(av, desc));
    }

    /** {@inheritdoc} */
    @Override
    protected BitmapFont getFont() {
        return this.av.fonts.getFont(24,
                this.isHovered() ? ColorScheme.SPECIAL_HOVER.color : ColorScheme.SPECIAL_BUTTON.color);
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
        super.mouseMoved(bounds, prev, curr);
        this.popup.update(bounds, prev, curr, this.desc);
    }
}
