package net.lugocorp.kingdom.ui.menu;
import net.lugocorp.kingdom.engine.AudioVideo;
import net.lugocorp.kingdom.utils.Colors;
import net.lugocorp.kingdom.utils.math.Coords;
import net.lugocorp.kingdom.utils.math.Rect;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

/**
 * Like a TextNode but with a colored capsule around the text
 */
public class BadgeNode extends TextNode {
    private static final int BADGE_MARGIN = 2;
    private final int foreground;
    private final Color color;

    public BadgeNode(AudioVideo av, int background, int foreground, String message) {
        super(av, message);
        this.color = Colors.fromHex(background);
        this.foreground = foreground;
    }

    /** {@inheritdoc} */
    protected BitmapFont getFont() {
        return this.av.fonts.getFont(18, this.foreground);
    }

    /** {@inheritdoc} */
    @Override
    public void draw(AudioVideo av, Rect bounds) {
        final int lineHeight = (int) this.getFont().getLineHeight();
        final int badgeHeight = lineHeight + (BadgeNode.BADGE_MARGIN * 2);
        int y = Coords.SIZE.y - bounds.y - lineHeight - BadgeNode.BADGE_MARGIN;

        // Draw the rectangles with rounded corners
        av.shapes.begin(ShapeType.Filled);
        av.shapes.setColor(this.color);
        for (int a = 0; a < this.lines.size(); a++) {
            final Line l = this.lines.get(a);
            // TODO support centered text
            av.shapes.rect(bounds.x, y, l.width, badgeHeight);
            if (a == 0) {
                av.shapes.ellipse(bounds.x - (badgeHeight / 2), y, badgeHeight, badgeHeight);
            }
            if (a == this.lines.size() - 1) {
                av.shapes.ellipse(bounds.x + l.width - (badgeHeight / 2), y, badgeHeight, badgeHeight);
            }
            y -= lineHeight;
        }
        av.shapes.end();

        // Call down into TextNode.draw()
        super.draw(av, bounds);
    }
}
