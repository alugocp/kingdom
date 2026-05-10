package net.lugocorp.kingdom.menu.game;
import net.lugocorp.kingdom.engine.AudioVideo;
import net.lugocorp.kingdom.engine.render.Drawable;
import net.lugocorp.kingdom.engine.shaders.ElementShader;
import net.lugocorp.kingdom.game.glyph.Glyph;
import net.lugocorp.kingdom.math.Coords;
import net.lugocorp.kingdom.math.Point;
import net.lugocorp.kingdom.math.Rect;
import net.lugocorp.kingdom.menu.Menu;
import net.lugocorp.kingdom.menu.MenuNode;
import net.lugocorp.kingdom.menu.MenuPopup;
import net.lugocorp.kingdom.menu.structure.ListNode;
import net.lugocorp.kingdom.menu.text.HeaderNode;
import net.lugocorp.kingdom.menu.text.TextNode;

/**
 * This MenuNode displays a large Glyph badge
 */
public class GlyphBadgeNode implements MenuNode {
    public static final int SIDE = 250;
    private final MenuPopup popup;
    private final Drawable sprite;
    private final Runnable clicked;
    private final boolean enabled;
    private final MenuNode desc;
    private boolean hovered = false;

    public GlyphBadgeNode(AudioVideo av, Glyph glyph, String desc, Runnable clicked, boolean enabled) {
        this.sprite = new Drawable(av.loaders.sprites, String.format("glyph-badge-%s", glyph.key));
        this.popup = new MenuPopup();
        this.clicked = clicked;
        this.enabled = enabled;
        this.desc = new ListNode().add(new HeaderNode(av, glyph.toString())).add(new TextNode(av, desc));
    }

    /**
     * Returns the shader mode associated with this GlyphBadgeNode's current state
     */
    private int getShaderMode() {
        if (!this.enabled) {
            return ElementShader.GRAY_MODE;
        }
        if (this.hovered) {
            return ElementShader.BRIGHT_MODE;
        }
        return ElementShader.DEFAULT_MODE;
    }

    /** {@inheritdoc} */
    @Override
    public void pack(Menu menu, int width) {
        this.popup.setMenu(menu);
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
        av.special.begin();
        av.shaders.element.setMode(this.getShaderMode());
        this.sprite.render(av.special, x, flip.y);
        av.special.end();
        av.shaders.element.originalColor();
    }

    /** {@inheritdoc} */
    @Override
    public void click(Rect bounds, Point p) {
        if (bounds.contains(p)) {
            this.clicked.run();
        }
    }

    /** {@inheritdoc} */
    @Override
    public void mouseMoved(Rect bounds, Point prev, Point curr) {
        this.popup.update(bounds, curr, this.desc);
        this.hovered = bounds.contains(curr);
    }
}
