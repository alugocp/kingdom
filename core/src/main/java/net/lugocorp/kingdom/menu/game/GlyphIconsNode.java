package net.lugocorp.kingdom.menu.game;
import net.lugocorp.kingdom.engine.AudioVideo;
import net.lugocorp.kingdom.engine.render.Drawable;
import net.lugocorp.kingdom.game.glyph.Glyph;
import net.lugocorp.kingdom.game.glyph.GlyphCategory;
import net.lugocorp.kingdom.math.Coords;
import net.lugocorp.kingdom.math.Point;
import net.lugocorp.kingdom.math.Rect;
import net.lugocorp.kingdom.menu.Menu;
import net.lugocorp.kingdom.menu.MenuNode;
import net.lugocorp.kingdom.menu.MenuPopup;
import net.lugocorp.kingdom.menu.text.TextNode;

/**
 * This MenuNode displays small Glyph icon(s)
 */
public class GlyphIconsNode implements MenuNode {
    public static final int MARGIN = 5;
    public static final int SIDE = 25;
    private final MenuPopup[] popups;
    private final TextNode[] labels;
    private final Drawable[] icons;

    public GlyphIconsNode(AudioVideo av, Glyph[] glyphs) {
        this.popups = new MenuPopup[glyphs.length];
        this.labels = new TextNode[glyphs.length];
        this.icons = new Drawable[glyphs.length];
        for (int a = 0; a < glyphs.length; a++) {
            this.icons[a] = new Drawable(av.loaders.sprites, String.format("glyph-icon-%s", glyphs[a].key));
            this.labels[a] = new TextNode(av, String.format("%s glyph", glyphs[a].toString()));
            this.popups[a] = new MenuPopup();
        }
    }

    public GlyphIconsNode(AudioVideo av, GlyphCategory gc) {
        this(av, gc.glyphs);
    }

    /**
     * Returns a GlyphIconsNode's given a certain number of icons
     */
    public static int width(int n) {
        return (GlyphIconsNode.SIDE + GlyphIconsNode.MARGIN) * n;
    }

    /** {@inheritdoc} */
    @Override
    public void pack(Menu menu, int width) {
        for (MenuPopup p : this.popups) {
            p.setMenu(menu);
        }
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

    /** {@inheritdoc} */
    @Override
    public void mouseMoved(Rect bounds, Point prev, Point curr) {
        final int w = GlyphIconsNode.SIDE + GlyphIconsNode.MARGIN;
        for (int a = 0; a < this.labels.length; a++) {
            this.popups[a].update(new Rect(bounds.x + (w * a), bounds.y, w, bounds.h), curr, this.labels[a]);
        }
    }
}
