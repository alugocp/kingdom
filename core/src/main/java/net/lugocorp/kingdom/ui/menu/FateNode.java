package net.lugocorp.kingdom.ui.menu;
import net.lugocorp.kingdom.engine.Drawable;
import net.lugocorp.kingdom.engine.Graphics;
import net.lugocorp.kingdom.game.model.Fate;
import net.lugocorp.kingdom.utils.math.Coords;
import net.lugocorp.kingdom.utils.math.Point;
import net.lugocorp.kingdom.utils.math.Rect;

/**
 * A node that displays some Fate as an option
 */
public class FateNode implements MenuNode {
    public static final int HEIGHT = 400;
    public static final int WIDTH = 300;
    private final Runnable onClick;
    private final Drawable image;
    private Fate fate;

    public FateNode(Graphics graphics, Fate fate, Runnable onClick) {
        this.onClick = onClick;
        this.image = new Drawable(graphics.loaders.sprites);
        this.setFate(graphics, fate);
    }

    /**
     * Returns this FateNode's Fate
     */
    public Fate getFate() {
        return this.fate;
    }

    /**
     * Sets this FateNode's Fate
     */
    public void setFate(Graphics graphics, Fate fate) {
        this.image.setSprite(fate.image.orElse("placeholder"));
        this.fate = fate;
    }

    /** {@inheritdoc} */
    @Override
    public int getHeight() {
        return FateNode.HEIGHT;
    }

    /** {@inheritdoc} */
    @Override
    public void pack(int width) {
    }

    /** {@inheritdoc} */
    @Override
    public void draw(Graphics graphics, Rect bounds) {
        Rect flip = Coords.screen.flip(bounds);
        graphics.sprites.begin();
        this.image.render(graphics.sprites, flip.x, flip.y);
        graphics.sprites.end();
    }

    /** {@inheritdoc} */
    @Override
    public void click(Menu menu, Rect bounds, Point p) {
        this.onClick.run();
    }
}
