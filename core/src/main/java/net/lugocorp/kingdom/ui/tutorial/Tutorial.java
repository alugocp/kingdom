package net.lugocorp.kingdom.ui.tutorial;
import net.lugocorp.kingdom.color.ColorScheme;
import net.lugocorp.kingdom.engine.AudioVideo;
import net.lugocorp.kingdom.engine.assets.FontParam;
import net.lugocorp.kingdom.math.Coords;
import net.lugocorp.kingdom.math.Point;
import net.lugocorp.kingdom.math.Rect;
import net.lugocorp.kingdom.menu.Menu;
import net.lugocorp.kingdom.menu.text.TextNode;
import net.lugocorp.kingdom.ui.views.GameView;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import java.util.LinkedList;
import java.util.Queue;

/**
 * This is a special Menu that acts as a tutorial for the user
 */
public class Tutorial extends Menu {
    private static final Rect OFFSCREEN = new Rect(-1, -1, 0, 0);
    private static final int MARGIN = 10;
    private final Queue<TutorialPopup> popups = new LinkedList<>();
    private final GameView view;

    public Tutorial(GameView view) {
        super(0, 0, Coords.SIZE.x, true, null);
        this.view = view;
    }

    /**
     * Initiates all content in this Tutorial
     */
    public void setup() {
        final int top = this.view.hud.top.getHeight() + Tutorial.MARGIN + 5;
        final int bot = Coords.SIZE.y - this.view.hud.bot.getHeight() - Tutorial.MARGIN - 5;
        this.add("Hi lol", TutorialArrow.UP, 200, 15, top);
        this.add("Okay bye now", TutorialArrow.DOWN, 200, (Coords.SIZE.x - 200) / 2, bot);
    }

    /**
     * Adds a node to this Tutorial
     */
    private Tutorial add(String text, TutorialArrow arrow, int width, int x, int y) {
        final TextNode node = new TextNode(this.view.av, text);
        final TextNode click = new TextNode(this.view.av, "(click to continue)") {
            /** {@inheritdoc} */
            @Override
            protected BitmapFont getFont() {
                return this.av.fonts.getFont(new FontParam().setColor(ColorScheme.GOLD.color));
            }
        };
        node.pack(this, width);
        click.pack(this, width);
        final int height = node.getHeight() + click.getHeight() + (Tutorial.MARGIN * 2);
        this.popups.add(new TutorialPopup(arrow, node, click,
                new Rect(x, y + arrow.offset(height), width + (Tutorial.MARGIN * 2), height)));
        return this;
    }

    /**
     * Swaps to the next node
     */
    private void next() {
        this.popups.remove();
    }

    /** {@inheritdoc} */
    @Override
    public Rect getBoundingRect() {
        return this.popups.size() > 0 ? this.popups.peek().bounds : Tutorial.OFFSCREEN;
    }

    /** {@inheritdoc} */
    @Override
    public void pack() {
        // No-op
    }

    /** {@inheritdoc} */
    @Override
    public int getContentHeight() {
        return 0;
    }

    /** {@inheritdoc} */
    @Override
    public void draw(AudioVideo av) {
        if (this.popups.size() == 0) {
            return;
        }

        // Draw the background
        final TutorialPopup popup = this.popups.peek();
        final Rect bg = popup.bounds;
        final boolean up = popup.direction == TutorialArrow.UP;
        final Point p1 = new Point(bg.x + (bg.w / 2) - (Tutorial.MARGIN / 2),
                Coords.SIZE.y - (up ? bg.y : bg.y + bg.h));
        final Point p2 = new Point(bg.x + (bg.w / 2),
                Coords.SIZE.y - (up ? bg.y - Tutorial.MARGIN : bg.y + bg.h + Tutorial.MARGIN));
        final Point p3 = new Point(bg.x + (bg.w / 2) + (Tutorial.MARGIN / 2),
                Coords.SIZE.y - (up ? bg.y : bg.y + bg.h));
        av.shapes.begin(ShapeType.Filled);
        av.shapes.setColor(ColorScheme.MENU.color);
        av.shapes.rect(bg.x, Coords.SIZE.y - bg.y - bg.h, bg.w, bg.h);
        av.shapes.triangle(p1.x, p1.y, p2.x, p2.y, p3.x, p3.y);
        av.shapes.end();

        // Draw the bounding line
        final Point c1 = new Point(bg.x, Coords.SIZE.y - bg.y); // Top-left corner
        final Point c2 = new Point(bg.x + bg.w, Coords.SIZE.y - bg.y); // Top-right corner
        final Point c3 = new Point(bg.x + bg.w, Coords.SIZE.y - bg.y - bg.h); // Bot-right corner
        final Point c4 = new Point(bg.x, Coords.SIZE.y - bg.y - bg.h); // Bot-left corner
        av.shapes.begin(ShapeType.Line);
        av.shapes.setColor(ColorScheme.OUTLINE.color);
        av.shapes.polyline(popup.getPolylineInput(c1, c2, c3, c4, p1, p2, p3));
        av.shapes.end();

        // Draw the TextNodes
        final TextNode node = this.popups.peek().node;
        final TextNode click = this.popups.peek().click;
        final int w = bg.w - (Tutorial.MARGIN * 2);
        final int h = bg.h - (Tutorial.MARGIN * 2);
        node.draw(av, new Rect(bg.x + Tutorial.MARGIN, bg.y + Tutorial.MARGIN, w, h));
        click.draw(av, new Rect(bg.x + Tutorial.MARGIN, bg.y + Tutorial.MARGIN + node.getHeight(), w, h));
    }

    /** {@inheritdoc} */
    @Override
    public boolean click(Point p) {
        if (this.popups.size() > 0 && this.popups.peek().bounds.contains(p)) {
            this.view.av.loaders.sounds.play("sfx/card-flick");
            this.next();
            return true;
        }
        return false;
    }

    /** {@inheritdoc} */
    @Override
    public boolean mouseMoved(Point p) {
        return false;
    }

    /** {@inheritdoc} */
    @Override
    public void keyPressed(int keycode) {
        // No-op
    }
}
