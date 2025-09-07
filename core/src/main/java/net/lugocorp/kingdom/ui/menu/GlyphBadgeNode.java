package net.lugocorp.kingdom.ui.menu;
import net.lugocorp.kingdom.engine.AudioVideo;
import net.lugocorp.kingdom.engine.render.Drawable;
import net.lugocorp.kingdom.game.glyph.Glyph;
import net.lugocorp.kingdom.utils.math.Coords;
import net.lugocorp.kingdom.utils.math.Rect;

/**
 * This MenuNode displays a large Glyph badge
 */
public class GlyphBadgeNode implements MenuNode {
    public static final int SIDE = 250;
    private final Drawable sprite;

    public GlyphBadgeNode(AudioVideo av, Glyph glyph) {
        this.sprite = new Drawable(av.loaders.sprites, String.format("glyph-badge-%s", glyph.key));
    }

    /** {@inheritdoc} */
    @Override
    public int getHeight() {
        return GlyphBadgeNode.SIDE;
    }

    /** {@inheritdoc} */
    @Override
    public void draw(AudioVideo av, Rect bounds) {
        Rect flip = Coords.screen.flip(bounds);
        int x = bounds.x + ((bounds.w - GlyphBadgeNode.SIDE) / 2);
        av.sprites.begin();
        this.sprite.render(av.sprites, x, flip.y);
        av.sprites.end();
    }
}
