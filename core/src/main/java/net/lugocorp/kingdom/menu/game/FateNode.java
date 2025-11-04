package net.lugocorp.kingdom.menu.game;
import net.lugocorp.kingdom.engine.AudioVideo;
import net.lugocorp.kingdom.engine.render.Drawable;
import net.lugocorp.kingdom.engine.shaders.ElementShader;
import net.lugocorp.kingdom.game.model.Fate;
import net.lugocorp.kingdom.math.Coords;
import net.lugocorp.kingdom.math.Point;
import net.lugocorp.kingdom.math.Rect;
import net.lugocorp.kingdom.menu.MenuNode;

/**
 * A node that displays some Fate as an option
 */
public class FateNode implements MenuNode {
    public static final int HEIGHT = 400;
    public static final int WIDTH = 300;
    private final Runnable onClick;
    private final Drawable image;
    private boolean hovered = false;
    private Fate fate;

    public FateNode(AudioVideo av, Fate fate, Runnable onClick) {
        this.onClick = onClick;
        this.image = new Drawable(av.loaders.sprites);
        this.setFate(av, fate);
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
    public void setFate(AudioVideo av, Fate fate) {
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
    public void draw(AudioVideo av, Rect bounds) {
        Rect flip = Coords.screen.flip(bounds);
        av.special.begin();
        if (this.hovered) {
            av.shaders.element.setMode(ElementShader.BRIGHT_MODE);
        }
        this.image.render(av.special, flip.x, flip.y);
        av.special.end();
        av.shaders.element.setMode(ElementShader.DEFAULT_MODE);
    }

    /** {@inheritdoc} */
    @Override
    public void click(Rect bounds, Point p) {
        this.onClick.run();
    }

    /** {@inheritdoc} */
    @Override
    public void mouseMoved(Rect bounds, Point prev, Point curr) {
        final Rect r = new Rect(bounds.x, bounds.y, FateNode.WIDTH, bounds.h);
        final boolean currIn = r.contains(curr);
        if (currIn && !this.hovered) {
            this.hovered = true;
        }
        if (!currIn && this.hovered) {
            this.hovered = false;
        }
    }
}
