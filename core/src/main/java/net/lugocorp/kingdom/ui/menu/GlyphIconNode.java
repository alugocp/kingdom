package net.lugocorp.kingdom.ui.menu;
import net.lugocorp.kingdom.engine.AudioVideo;
import net.lugocorp.kingdom.engine.render.Drawable;
import net.lugocorp.kingdom.game.glyph.Glyph;
import net.lugocorp.kingdom.utils.math.Coords;
import net.lugocorp.kingdom.utils.math.Rect;

/**
 * This MenuNode displays a small Glyph icon
 */
public class GlyphIconNode implements MenuNode {
    public static final int SIDE = 25;
    private final Drawable sprite;

    public GlyphIconNode(AudioVideo av, Glyph glyph) {
        this.sprite = new Drawable(av.loaders.sprites, String.format("glyph-icon-%s", glyph.key));
    }

    /** {@inheritdoc} */
    @Override
    public int getHeight() {
        return GlyphIconNode.SIDE;
    }

    /** {@inheritdoc} */
    @Override
    public void draw(AudioVideo av, Rect bounds) {
        Rect flip = Coords.screen.flip(bounds);
        av.sprites.begin();
        this.sprite.render(av.sprites, flip.x, flip.y);
        av.sprites.end();
    }
}
