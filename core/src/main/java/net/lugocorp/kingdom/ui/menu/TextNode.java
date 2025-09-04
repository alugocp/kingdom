package net.lugocorp.kingdom.ui.menu;
import net.lugocorp.kingdom.engine.AudioVideo;
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
    private final List<String> lines = new ArrayList<>();
    private int width;
    private int hash;
    protected final AudioVideo av;

    public TextNode(AudioVideo av, String message) {
        this.hash = message.hashCode();
        this.lines.add(message);
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
        return this.av.fonts.basic;
    }

    /**
     * Updates the content of this TextNode
     */
    public void setText(String message) {
        if (message.hashCode() == this.hash) {
            return;
        }
        this.hash = message.hashCode();
        this.lines.clear();
        this.lines.add(message);
        this.pack(this.width);
    }

    /** {@inheritdoc} */
    @Override
    public int getHeight() {
        BitmapFont font = this.getFont();
        return (int) (this.lines.size() * font.getLineHeight()) + (this.getMargin() * 2);
    }

    /** {@inheritdoc} */
    @Override
    public void pack(int width) {
        this.width = width;
        BitmapFont font = this.getFont();
        GlyphLayout layout = new GlyphLayout();
        String[] message = this.lines.get(0).split(" ");
        String buffer = "";
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
                    this.lines.add(substr);
                    start++;
                } else {
                    this.lines.add(buffer);
                    start += length - 1;
                    buffer = "";
                    length = 1;
                }
            } else {
                buffer = substr;
                length++;
            }
        }
        if (buffer.length() > 0) {
            this.lines.add(buffer);
        }
    }

    /** {@inheritdoc} */
    @Override
    public void draw(AudioVideo av, Rect bounds) {
        BitmapFont font = this.getFont();
        int y = Coords.SIZE.y - bounds.y - this.getMargin();
        av.sprites.begin();
        for (int a = 0; a < this.lines.size(); a++) {
            font.draw(av.sprites, this.lines.get(a), bounds.x, y);
            y -= (int) font.getLineHeight();
        }
        av.sprites.end();
    }
}
