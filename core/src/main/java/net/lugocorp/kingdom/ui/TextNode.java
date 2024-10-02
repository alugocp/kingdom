package net.lugocorp.kingdom.ui;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import java.util.ArrayList;
import java.util.List;
import net.lugocorp.kingdom.engine.Graphics;
import net.lugocorp.kingdom.math.Point;
import net.lugocorp.kingdom.math.Rect;

/**
 * MenuNode item containing text
 */
public class TextNode implements MenuNode {
    private final List<String> lines = new ArrayList<>();
    protected BitmapFont font;

    public TextNode(Graphics graphics, String message) {
        this.font = graphics.fonts.basic;
        this.lines.add(message);
    }

    /** {@inheritdoc} */
    @Override
    public int getHeight() {
        return (int) (this.lines.size() * this.font.getLineHeight());
    }

    /** {@inheritdoc} */
    @Override
    public void pack(int width) {
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
            layout.setText(this.font, substr);
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
    public void draw(Graphics graphics, Rect bounds) {
        graphics.sprites.begin();
        int y = Gdx.graphics.getHeight() - bounds.y;
        for (int a = 0; a < this.lines.size(); a++) {
            this.font.draw(graphics.sprites, this.lines.get(a), bounds.x, y);
            y -= (int) this.font.getLineHeight();
        }
        graphics.sprites.end();
    }

    /** {@inheritdoc} */
    @Override
    public void click(Rect bounds, Point p) {
        // No-op
    }
}
