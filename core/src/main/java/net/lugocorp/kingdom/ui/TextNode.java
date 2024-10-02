package net.lugocorp.kingdom.ui;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import net.lugocorp.kingdom.math.Point;
import net.lugocorp.kingdom.math.Rect;

/**
 * MenuNode item containing text
 */
public class TextNode implements MenuNode {
    private final BitmapFont font;
    private final String message;

    public TextNode(BitmapFont font, String message) {
        this.message = message;
        this.font = font;
    }

    /** {@inheritdoc} */
    @Override
    public int getHeight() {
        return (int) this.font.getLineHeight();
    }

    /** {@inheritdoc} */
    @Override
    public void pack(int width) {
        // No-op
    }

    /** {@inheritdoc} */
    @Override
    public void draw(SpriteBatch sprites, ShapeRenderer shapes, Rect bounds) {
        sprites.begin();
        this.font.draw(sprites, this.message, bounds.x, bounds.y);
        sprites.end();
    }

    /** {@inheritdoc} */
    @Override
    public void click(Rect bounds, Point p) {
        // No-op
    }
}
