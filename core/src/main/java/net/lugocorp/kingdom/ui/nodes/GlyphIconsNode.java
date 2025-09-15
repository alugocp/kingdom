package net.lugocorp.kingdom.ui.nodes;
import net.lugocorp.kingdom.engine.AudioVideo;
import net.lugocorp.kingdom.engine.render.Drawable;
import net.lugocorp.kingdom.game.glyph.Glyph;
import net.lugocorp.kingdom.game.glyph.GlyphCategory;
import net.lugocorp.kingdom.ui.MenuNode;
import net.lugocorp.kingdom.utils.math.Coords;
import net.lugocorp.kingdom.utils.math.Rect;

/**
 * This MenuNode displays small Glyph icon(s)
 */
public class GlyphIconsNode implements MenuNode {
    public static final int MARGIN = 5;
    public static final int SIDE = 25;
    private final Drawable[] icons;

    public GlyphIconsNode(AudioVideo av, Glyph[] glyphs) {
        this.icons = new Drawable[glyphs.length];
        for (int a = 0; a < glyphs.length; a++) {
            this.icons[a] = new Drawable(av.loaders.sprites, String.format("glyph-icon-%s", glyphs[a].key));
        }
    }

    public GlyphIconsNode(AudioVideo av, GlyphCategory gc) {
        this(av, gc.glyphs);
    }

    /** {@inheritdoc} */
    @Override
    public int getHeight() {
        return GlyphIconsNode.SIDE;
    }

    /** {@inheritdoc} */
    @Override
    public void draw(AudioVideo av, Rect bounds) {
        Rect flip = Coords.screen.flip(bounds);
        av.sprites.begin();
        for (int a = 0; a < this.icons.length; a++) {
            this.icons[a].render(av.sprites, flip.x + ((GlyphIconsNode.SIDE + GlyphIconsNode.MARGIN) * a), flip.y);
        }
        av.sprites.end();
    }
}
