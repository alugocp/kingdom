package net.lugocorp.kingdom.menu.icon;
import net.lugocorp.kingdom.engine.AudioVideo;
import net.lugocorp.kingdom.engine.render.Drawable;
import net.lugocorp.kingdom.math.Coords;
import net.lugocorp.kingdom.math.Point;
import net.lugocorp.kingdom.math.Rect;
import net.lugocorp.kingdom.menu.Menu;
import net.lugocorp.kingdom.menu.MenuNode;
import net.lugocorp.kingdom.menu.MenuPopup;

/**
 * An icon that displays something when you hover over it
 */
public abstract class IconNode implements MenuNode {
    public static final int SIDE = 35;
    private final MenuPopup popup = new MenuPopup();
    private Drawable icon;

    public IconNode(AudioVideo av, String icon) {
        this.icon = new Drawable(av.loaders.sprites, icon);
    }

    /**
     * Returns the MenuNode to display in the popup
     */
    protected abstract MenuNode getPopupNode();

    /**
     * Returns the current icon
     */
    public Drawable getIcon() {
        return this.icon;
    }

    /**
     * Sets the current icon
     */
    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    /**
     * Returns the bounds associated with this IconNode's visible element
     */
    private Rect getBounds(Rect bounds) {
        return new Rect(bounds.x + ((bounds.w - IconNode.SIDE) / 2), bounds.y + ((bounds.h - IconNode.SIDE) / 2),
                IconNode.SIDE, IconNode.SIDE);
    }

    /** {@inheritdoc} */
    @Override
    public int getHeight() {
        return IconNode.SIDE;
    }

    /** {@inheritdoc} */
    @Override
    public void pack(Menu menu, int width) {
        this.popup.setMenu(menu);
    }

    /** {@inheritdoc} */
    @Override
    public void mouseMoved(Rect bounds, Point prev, Point curr) {
        this.popup.update(this.getBounds(bounds), curr, this.getPopupNode());
    }

    /** {@inheritdoc} */
    @Override
    public void draw(AudioVideo av, Rect bounds) {
        final Rect flip = Coords.screen.flip(this.getBounds(bounds));
        av.sprites.begin();
        this.icon.render(av.sprites, flip.x, flip.y);
        av.sprites.end();
    }
}
