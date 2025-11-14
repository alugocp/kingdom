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
        final int mid = (Coords.SIZE.y + this.view.hud.top.getHeight() - this.view.hud.bot.getHeight()) / 2;
        final int center = (Coords.SIZE.x / 2) - 150;
        this.add(
                "Welcome to Legends of T'ahn! This is a small tutorial for beginners that you can turn off in the settings at the top right.",
                TutorialArrow.NONE, center, mid);
        this.add(
                "This is the unit/building/tile menu. You can click on the tabs below to view details on any unit, building, or tile in the game world.",
                TutorialArrow.DOWN, center, bot);
        this.add(
                "Speaking of which, this is the game world. You can click on any visible tile here to open its details in the menu below.",
                TutorialArrow.DOWN, center, mid);
        this.add(
                "This is the minimap. It gives you an overview of the entire game world. You'll see it change colors as your units explore the map and claim tiles in your name. You can also click on it to instantly move the camera.",
                TutorialArrow.DOWN, Coords.SIZE.x - 330, bot);
        this.add(
                "Below the minimap is the finish turn button. Click this when you're ready to end your turn so the computer players can go.",
                TutorialArrow.DOWN, Coords.SIZE.x - 330, bot);
        this.add(
                "This is the overhead bar. You can view personal and global stats here, as well as access the settings menu.",
                TutorialArrow.UP, center, top);
        this.add(
                "There are forces conspiring against you elsewhere in the map. Grow your clan as you explore the game world and stop these conspirators!",
                TutorialArrow.NONE, center, mid);
        this.add("That's the basics! Click on the red arrow ability below to move your unit, or try another ability.",
                TutorialArrow.DOWN, (Coords.SIZE.x / 3) - 150, bot);
    }

    /**
     * Adds a node to this Tutorial
     */
    private Tutorial add(String text, TutorialArrow arrow, int x, int y) {
        final TextNode node = new TextNode(this.view.av, text);
        final TextNode click = new TextNode(this.view.av,
                String.format("click to continue (%d/8)", this.popups.size() + 1)) {
            /** {@inheritdoc} */
            @Override
            protected BitmapFont getFont() {
                return this.av.fonts.getFont(new FontParam().setColor(ColorScheme.GOLD.color));
            }
        };
        final int width = 300;
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
