package net.lugocorp.kingdom.menu;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import java.util.Optional;
import net.lugocorp.kingdom.engine.Graphics;
import net.lugocorp.kingdom.math.Coords;
import net.lugocorp.kingdom.math.Point;
import net.lugocorp.kingdom.math.Rect;

/**
 * Contains the logic to display some vertical menu content
 */
public class Menu {
    private static final int MARGIN = 5;
    private final MenuNode root;
    private final boolean tall;
    private final int width;
    private final int x;
    private final int y;
    private Optional<Menu> mini = Optional.empty();
    private boolean outlined = false;
    private int offset = 0;

    public Menu(int x, int y, int width, boolean tall, MenuNode root) {
        this.width = width;
        this.root = root;
        this.tall = tall;
        this.x = x;
        this.y = y;
        root.pack(width);
    }

    /**
     * Returns true if this Menu should allow for scrolling
     */
    private boolean shouldScroll() {
        return this.root.getHeight() > this.getHeight();
    }

    /**
     * Scrolls this Menu by some set amount
     */
    public void scroll(int dy) {
        if (this.shouldScroll()) {
            this.offset = Math.max(0, Math.min(this.root.getHeight() - this.getHeight(), this.offset + dy));
        }
    }

    /**
     * Returns a Rect associated with the Menu's interactive area
     */
    public Rect getBoundingRect() {
        return new Rect(this.x, this.y, this.width, this.getHeight());
    }

    /**
     * Sets a mini menu on this Menu
     */
    void setMiniMenu(MenuNode root, int x, int y) {
        Menu menu = new Menu(x, y, 150, false, root);
        menu.outlined = true;
        this.mini = Optional.of(menu);
    }

    /**
     * Returns the height of the Menu UI
     */
    private int getHeight() {
        return this.tall ? Gdx.graphics.getHeight() : this.root.getHeight();
    }

    /**
     * Draws every MenuNode in this Menu
     */
    public void draw(Graphics graphics) {
        final int h = this.getHeight();
        Rect bg = Coords.screen.flip(this.x, this.y, this.width, h);
        graphics.shapes.begin(ShapeType.Filled);
        graphics.shapes.setColor(Color.BLACK);
        graphics.shapes.rect(bg.x, bg.y, bg.w, bg.h);
        graphics.shapes.end();
        if (this.outlined) {
            graphics.shapes.begin(ShapeType.Line);
            graphics.shapes.setColor(Color.WHITE);
            graphics.shapes.rect(bg.x, bg.y, bg.w, bg.h);
            graphics.shapes.end();
        }
        this.root.draw(graphics, new Rect(this.x + Menu.MARGIN, this.y + Menu.MARGIN - this.offset,
                this.width - (Menu.MARGIN * 3), h - (Menu.MARGIN * 2)));
        if (this.shouldScroll()) {
            final int rh = this.root.getHeight();
            Rect flip = Coords.screen.flip(this.x + this.width - Menu.MARGIN, this.y + this.offset, Menu.MARGIN,
                    (h * h) / rh);
            graphics.shapes.begin(ShapeType.Filled);
            graphics.shapes.setColor(Color.TEAL);
            graphics.shapes.rect(flip.x, flip.y, flip.w, flip.h);
            graphics.shapes.end();
        }
        this.mini.ifPresent((Menu m) -> m.draw(graphics));
    }

    /**
     * Propagates a signal to activate some click logic
     */
    public boolean click(Point p) {
        if (this.mini.isPresent() && this.mini.get().click(p)) {
            this.mini = Optional.empty();
            return true;
        }
        this.mini = Optional.empty();
        final Rect bounds = new Rect(this.x + Menu.MARGIN, this.y + Menu.MARGIN - this.offset,
                this.width - (Menu.MARGIN * 3), this.getHeight() - (Menu.MARGIN * 2));
        if (bounds.contains(p)) {
            this.root.click(this, bounds, p);
            return true;
        }
        return false;
    }
}
