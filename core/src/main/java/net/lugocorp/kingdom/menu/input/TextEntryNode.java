package net.lugocorp.kingdom.menu.input;
import net.lugocorp.kingdom.color.ColorScheme;
import net.lugocorp.kingdom.engine.AudioVideo;
import net.lugocorp.kingdom.math.Coords;
import net.lugocorp.kingdom.math.Point;
import net.lugocorp.kingdom.math.Rect;
import net.lugocorp.kingdom.menu.Menu;
import net.lugocorp.kingdom.menu.MenuNode;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import java.util.function.Consumer;

/**
 * MenuNode item allowing text entry
 */
public class TextEntryNode implements MenuNode {
    private static final int MARGIN = 5;
    private final Consumer<String> entered;
    private final BitmapFont font;
    private final AudioVideo av;
    private final int charWidth;
    private boolean numbersOnly = false;
    private boolean selected = false;
    private StringBuilder builder;
    private int charsWindow = 10;
    private int cursor = 0;
    private int delta = 0;

    public TextEntryNode(AudioVideo av, String initial, Consumer<String> entered) {
        this.builder = new StringBuilder(initial);
        this.font = av.fonts.getFont(ColorScheme.TEXT.color);
        this.entered = entered;
        this.av = av;

        // Find width of a single glyph
        GlyphLayout layout = new GlyphLayout();
        layout.setText(this.font, "W");
        this.charWidth = (int) layout.width;
    }

    public TextEntryNode(AudioVideo av, Consumer<String> entered) {
        this(av, "", entered);
    }

    /**
     * Sets whether or not players can enter non-number characters into this field
     */
    public TextEntryNode setNumbersOnly(boolean numbersOnly) {
        this.numbersOnly = numbersOnly;
        return this;
    }

    /**
     * Returns the substring that's visible on the node
     */
    private String getVisibleSubstring() {
        String s = this.builder.substring(this.delta);
        return s.length() > this.charsWindow ? s.substring(0, this.charsWindow) : s;
    }

    /** {@inheritdoc} */
    @Override
    public int getHeight() {
        return ((int) this.font.getLineHeight()) + (TextEntryNode.MARGIN * 2);
    }

    /** {@inheritdoc} */
    @Override
    public void pack(Menu menu, int width) {
        this.charsWindow = width / this.charWidth;
    }

    /** {@inheritdoc} */
    @Override
    public void draw(AudioVideo av, Rect bounds) {
        // Draw the cursor
        Rect flip2 = Coords.screen.flip(bounds.x + (this.charWidth * (this.cursor - this.delta)),
                bounds.y + TextEntryNode.MARGIN, this.charWidth, bounds.h - (TextEntryNode.MARGIN * 2));
        av.shapes.begin(ShapeType.Filled);
        av.shapes.setColor(ColorScheme.BUTTON.color);
        av.shapes.rect(flip2.x, flip2.y, flip2.w, flip2.h);
        av.shapes.end();

        // Draw the selected highlight
        Rect flip1 = Coords.screen.flip(bounds.x, bounds.y, bounds.w, bounds.h);
        av.shapes.begin(ShapeType.Line);
        av.shapes.setColor(this.selected ? ColorScheme.BUTTON.color : ColorScheme.OUTLINE.color);
        av.shapes.rect(flip1.x, flip1.y, flip1.w, flip1.h);
        av.shapes.end();

        // Draw the text
        av.sprites.begin();
        String visible = this.getVisibleSubstring();
        for (int a = 0; a < visible.length(); a++) {
            this.font.draw(av.sprites, visible.substring(a, a + 1), bounds.x + (int) ((a + 0.25) * this.charWidth),
                    Coords.SIZE.y - bounds.y - TextEntryNode.MARGIN - 3);
        }
        av.sprites.end();
    }

    /** {@inheritdoc} */
    @Override
    public void click(Rect bounds, Point p) {
        this.av.loaders.sounds.play("sfx/arrow");
        this.cursor = Math.min((p.x - bounds.x) / this.charWidth, this.getVisibleSubstring().length()) + this.delta;
        this.selected = true;
    }

    /** {@inheritdoc} */
    @Override
    public void unclick() {
        this.selected = false;
    }

    /** {@inheritdoc} */
    @Override
    public void keyPressed(int keycode) {
        if (!this.selected) {
            return;
        }
        if (keycode == Keys.DEL && this.builder.length() > 0 && this.cursor > 0) {
            this.builder.deleteCharAt(this.cursor - 1);
            this.cursor--;
            if (this.delta > 0
                    && (this.cursor == this.delta || this.builder.length() - this.delta < this.charsWindow)) {
                this.delta--;
            }
        }
        if (keycode == Keys.LEFT && this.cursor > 0) {
            this.cursor--;
            if (this.cursor == this.delta && this.delta > 0) {
                this.delta--;
            }
        }
        if (keycode == Keys.RIGHT && this.cursor < this.builder.length()) {
            this.cursor++;
            if (this.cursor == this.charsWindow + this.delta - 1
                    && this.delta + this.charsWindow < this.builder.length() + 1) {
                this.delta++;
            }
        }
        if ((!this.numbersOnly && keycode >= Keys.A && keycode <= Keys.Z)
                || (keycode >= Keys.NUM_0 && keycode <= Keys.NUM_9)
                || (keycode >= Keys.NUMPAD_0 && keycode <= Keys.NUMPAD_9)) {
            this.builder.insert(this.cursor, Keys.toString(keycode));
            this.cursor++;
            if (this.cursor == this.delta + this.charsWindow) {
                this.delta++;
            }
        }
        this.entered.accept(this.builder.toString());
    }
}
