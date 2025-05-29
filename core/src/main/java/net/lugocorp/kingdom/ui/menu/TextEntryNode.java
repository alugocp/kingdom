package net.lugocorp.kingdom.ui.menu;
import net.lugocorp.kingdom.engine.AudioVideo;
import net.lugocorp.kingdom.utils.math.Coords;
import net.lugocorp.kingdom.utils.math.Point;
import net.lugocorp.kingdom.utils.math.Rect;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

/**
 * MenuNode item allowing text entry
 */
public class TextEntryNode implements MenuNode {
    private final int MARGIN = 5;
    private final int charWidth;
    private boolean selected = false;
    private String message;
    private int charsWindow = 10;
    private int cursor = 0;
    private int delta = 0;
    protected BitmapFont font;

    public TextEntryNode(AudioVideo av, String message) {
        this.font = av.fonts.basic;
        this.message = message;

        // Find width of a single glyph
        GlyphLayout layout = new GlyphLayout();
        layout.setText(this.font, "W");
        this.charWidth = (int) layout.width;
    }

    public TextEntryNode(AudioVideo av) {
        this(av, "");
    }

    /**
     * Returns the substring that's visible on the node
     */
    private String getVisibleSubstring() {
        String s = this.message.substring(this.delta);
        return s.length() > this.charsWindow ? s.substring(0, this.charsWindow) : s;
    }

    /** {@inheritdoc} */
    @Override
    public int getHeight() {
        return ((int) this.font.getLineHeight()) + (this.MARGIN * 2);
    }

    /** {@inheritdoc} */
    @Override
    public void pack(int width) {
        this.charsWindow = width / this.charWidth;
    }

    /** {@inheritdoc} */
    @Override
    public void draw(AudioVideo av, Rect bounds) {
        // Draw the cursor
        Rect flip2 = Coords.screen.flip(bounds.x + (this.charWidth * (this.cursor - this.delta)),
                bounds.y + this.MARGIN, this.charWidth, bounds.h - (this.MARGIN * 2));
        av.shapes.begin(ShapeType.Filled);
        av.shapes.setColor(Color.TEAL);
        av.shapes.rect(flip2.x, flip2.y, flip2.w, flip2.h);
        av.shapes.end();

        // Draw the selected highlight
        Rect flip1 = Coords.screen.flip(bounds.x, bounds.y, bounds.w, bounds.h);
        av.shapes.begin(ShapeType.Line);
        av.shapes.setColor(this.selected ? Color.TEAL : Color.WHITE);
        av.shapes.rect(flip1.x, flip1.y, flip1.w, flip1.h);
        av.shapes.end();

        // Draw the text
        av.sprites.begin();
        String visible = this.getVisibleSubstring();
        for (int a = 0; a < visible.length(); a++) {
            this.font.draw(av.sprites, visible.substring(a, a + 1), bounds.x + (int) ((a + 0.25) * this.charWidth),
                    Coords.SIZE.y - bounds.y - this.MARGIN - 3);
        }
        av.sprites.end();
    }

    /** {@inheritdoc} */
    @Override
    public void click(Menu menu, Rect bounds, Point p) {
        this.cursor = Math.min((p.x - bounds.x) / this.charWidth, this.getVisibleSubstring().length()) + this.delta;
        this.selected = true;
    }

    /** {@inheritdoc} */
    @Override
    public void unclick() {
        this.selected = false;
    }
}
