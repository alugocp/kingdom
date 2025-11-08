package net.lugocorp.kingdom.menu.text;
import net.lugocorp.kingdom.color.ColorScheme;
import net.lugocorp.kingdom.color.Colors;
import net.lugocorp.kingdom.engine.AudioVideo;
import net.lugocorp.kingdom.engine.assets.FontParam;
import net.lugocorp.kingdom.math.Coords;
import net.lugocorp.kingdom.math.Point;
import net.lugocorp.kingdom.math.Rect;
import net.lugocorp.kingdom.menu.Menu;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import java.util.ArrayList;
import java.util.List;

/**
 * Like a TextNode but with a colored capsule around the text
 */
public class BadgeNode extends TextNode {
    private static final int BADGE_MARGIN = 2;
    private final List<Point> slashes = new ArrayList<>();
    private final Color foreground;
    private final Color color;

    public BadgeNode(AudioVideo av, int background, int foreground, String message) {
        super(av, message);
        this.foreground = Colors.fromHex(foreground);
        this.color = Colors.fromHex(background);
    }

    /** {@inheritdoc} */
    protected BitmapFont getFont() {
        return this.av.fonts
                .getFont(new FontParam().setSize(18).setColor(this.foreground).setBorder(ColorScheme.BLACK.color));
    }

    /** {@inheritdoc} */
    @Override
    public void pack(Menu menu, int width) {
        super.pack(menu, width);

        // Figure out where to place slashes
        final GlyphLayout layout = new GlyphLayout();
        for (int a = 0; a < this.lines.size(); a++) {
            final Line l = this.lines.get(a);
            int i = 0;
            while (l.text.substring(i).indexOf('/') > -1) {
                i += l.text.substring(i).indexOf('/');
                layout.setText(this.getFont(), l.text.substring(0, i));
                this.slashes.add(new Point((int) layout.width, a));
                i++;
            }
        }
    }

    /** {@inheritdoc} */
    @Override
    public void draw(AudioVideo av, Rect bounds) {
        final int lineHeight = (int) this.getFont().getLineHeight();
        final int badgeHeight = lineHeight + (BadgeNode.BADGE_MARGIN * 2);
        final int yInitial = Coords.SIZE.y - bounds.y - lineHeight - this.getMargin();
        final int dx = badgeHeight / 2;
        int y = yInitial;

        // Draw the rectangles with rounded corners
        av.shapes.begin(ShapeType.Filled);
        av.shapes.setColor(this.color);
        for (int a = 0; a < this.lines.size(); a++) {
            final Line l = this.lines.get(a);
            // TODO support centered text
            av.shapes.rect(bounds.x + dx, y, l.width, badgeHeight);
            if (a == 0) {
                av.shapes.ellipse(bounds.x, y, badgeHeight, badgeHeight);
            }
            if (a == this.lines.size() - 1) {
                av.shapes.ellipse(bounds.x + l.width, y, badgeHeight, badgeHeight);
            }
            y -= lineHeight;
        }
        av.shapes.end();

        // Call down into TextNode.draw()
        super.draw(av, new Rect(bounds.x + dx, bounds.y, bounds.w, bounds.h));

        // Draw a gap over any slashes
        av.shapes.begin(ShapeType.Filled);
        av.shapes.setColor(ColorScheme.MENU.color);
        for (Point slash : this.slashes) {
            int sy = yInitial - (lineHeight * slash.y);
            int sx = bounds.x + slash.x;
            av.shapes.rectLine(sx + dx, sy, sx + dx + 6, sy + badgeHeight, 5);
        }
        av.shapes.end();
    }
}
