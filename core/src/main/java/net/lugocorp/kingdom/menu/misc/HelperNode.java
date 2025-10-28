package net.lugocorp.kingdom.menu.misc;
import net.lugocorp.kingdom.engine.AudioVideo;
import net.lugocorp.kingdom.engine.render.Drawable;
import net.lugocorp.kingdom.math.Coords;
import net.lugocorp.kingdom.math.Point;
import net.lugocorp.kingdom.math.Rect;
import net.lugocorp.kingdom.menu.Menu;
import net.lugocorp.kingdom.menu.MenuNode;
import net.lugocorp.kingdom.menu.MenuPopup;
import net.lugocorp.kingdom.menu.text.TextNode;

/**
 * A friendly popup that explains the more advanced mechanics to players
 */
public class HelperNode implements MenuNode {
    public static int SIDE = 35;
    private final MenuPopup popup = new MenuPopup();
    private final MenuNode desc;
    private final Drawable icon;

    public HelperNode(AudioVideo av, MenuNode desc) {
        this.icon = new Drawable(av.loaders.sprites, "help-icon");
        this.desc = desc;
    }

    public HelperNode(AudioVideo av, String desc) {
        this(av, new TextNode(av, desc));
    }

    /** {@inheritdoc} */
    @Override
    public int getHeight() {
        return HelperNode.SIDE;
    }

    /** {@inheritdoc} */
    @Override
    public void pack(Menu menu, int width) {
        this.popup.setMenu(menu);
    }

    /** {@inheritdoc} */
    @Override
    public void mouseMoved(Rect bounds, Point prev, Point curr) {
        this.popup.update(new Rect(bounds.x, bounds.y, HelperNode.SIDE, HelperNode.SIDE), curr, this.desc);
    }

    /** {@inheritdoc} */
    @Override
    public void draw(AudioVideo av, Rect bounds) {
        final Rect flip = Coords.screen.flip(bounds);
        av.sprites.begin();
        this.icon.render(av.sprites, flip.x, flip.y);
        av.sprites.end();
    }
}
