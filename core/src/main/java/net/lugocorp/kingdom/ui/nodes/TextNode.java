package net.lugocorp.kingdom.ui.nodes;
import net.lugocorp.kingdom.engine.AudioVideo;
import net.lugocorp.kingdom.ui.ColorScheme;
import net.lugocorp.kingdom.ui.Menu;
import net.lugocorp.kingdom.ui.MenuNode;
import net.lugocorp.kingdom.utils.math.Coords;
import net.lugocorp.kingdom.utils.math.Rect;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import java.util.ArrayList;
import java.util.List;

/**
 * MenuNode item containing text
 */
public class TextNode implements MenuNode {
    private boolean centered = false;
    private String loaded;
    private int width;
    private int hash;
    protected final List<Line> lines = new ArrayList<>();
    protected final AudioVideo av;
    protected Menu menu = null;

    public TextNode(AudioVideo av, String message) {
        this.hash = message.hashCode();
        this.loaded = message;
        this.av = av;
    }

    /**
     * Sets the vertical margin for this TextNode
     */
    protected int getMargin() {
        return 5;
    }

    /**
     * Returns the font to be used for this TextNode
     */
    protected BitmapFont getFont() {
        return this.av.fonts.getFont(ColorScheme.TEXT);
    }

    /**
     * Updates the content of this TextNode
     */
    public void setText(String message) {
        if (message.hashCode() == this.hash) {
            return;
        }
        this.hash = message.hashCode();
        this.loaded = message;
        this.pack(this.menu, this.width);
    }

    /**
     * Moves the text in this TextNode towards the center
     */
    public TextNode center() {
        this.centered = true;
        return this;
    }

    /**
     * Returns true if this text is centered
     */
    protected boolean isCentered() {
        return this.centered;
    }

    /** {@inheritdoc} */
    @Override
    public int getHeight() {
        BitmapFont font = this.getFont();
        return (int) (this.lines.size() * font.getLineHeight()) + (this.getMargin() * 2);
    }

    /** {@inheritdoc} */
    @Override
    public void pack(Menu menu, int width) {
        this.menu = menu;
        this.width = width;
        BitmapFont font = this.getFont();
        GlyphLayout layout = new GlyphLayout();
        String[] message = this.loaded.split(" ");
        String buffer = "";
        int bufferWidth = 0;
        int start = 0;
        int length = 1;
        this.lines.clear();
        while (start + length <= message.length) {
            String substr = (length == 1)
                    ? message[start]
                    : String.format("%s %s", buffer, message[start + length - 1]);
            layout.setText(font, substr);
            if (layout.width >= width) {
                if (length == 1) {
                    this.lines.add(new Line(substr, (int) layout.width));
                    start++;
                } else {
                    this.lines.add(new Line(buffer, bufferWidth));
                    start += length - 1;
                    bufferWidth = 0;
                    buffer = "";
                    length = 1;
                }
            } else {
                bufferWidth = (int) layout.width;
                buffer = substr;
                length++;
            }
        }
        if (buffer.length() > 0) {
            this.lines.add(new Line(buffer, bufferWidth));
        }
    }

    /** {@inheritdoc} */
    @Override
    public void draw(AudioVideo av, Rect bounds) {
        BitmapFont font = this.getFont();
        int y = Coords.SIZE.y - bounds.y - this.getMargin();
        av.sprites.begin();
        for (int a = 0; a < this.lines.size(); a++) {
            int x = bounds.x + (this.centered ? (bounds.w - this.lines.get(a).width) / 2 : 0);
            font.draw(av.sprites, this.lines.get(a).text, x, y);
            y -= (int) font.getLineHeight();
        }
        av.sprites.end();
    }

    /**
     * Combines a line of text and its width
     */
    protected static class Line {
        protected final String text;
        protected final int width;

        protected Line(String text, int width) {
            this.width = width;
            this.text = text;
        }
    }
}
