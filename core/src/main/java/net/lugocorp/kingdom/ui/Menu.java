package net.lugocorp.kingdom.ui;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
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
            final int coeff = (Gdx.input.isTouched() && Gdx.input.getX() >= this.x + this.width - Menu.MARGIN) ? 1 : -1;
            this.offset = Math.max(0, Math.min(this.root.getHeight() - this.getHeight(), this.offset + (coeff * dy)));
        }
    }

    /**
     * Returns a Rect associated with the Menu's interactive area
     */
    public Rect getBoundingRect() {
        return new Rect(this.x, this.y - this.offset, this.width, this.root.getHeight());
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
    public void draw(SpriteBatch batch, ShapeRenderer shapes) {
        final int h = this.getHeight();
        shapes.begin(ShapeType.Filled);
        shapes.setColor(Color.BLACK);
        shapes.rect(this.x, this.y, this.width, h);
        shapes.end();
        this.root.draw(batch, shapes, new Rect(this.x + Menu.MARGIN, this.y + Menu.MARGIN - this.offset,
                this.width - (Menu.MARGIN * 3), h - (Menu.MARGIN * 2)));
        if (this.shouldScroll()) {
            final int rh = this.root.getHeight();
            shapes.begin(ShapeType.Filled);
            shapes.setColor(Color.TEAL);
            shapes.rect(this.x + this.width - Menu.MARGIN, this.y + this.offset, Menu.MARGIN, (h * h) / rh);
            shapes.end();
        }
    }

    /**
     * Propagates a signal to activate some click logic
     */
    public void click(Point p) {
        final Rect bounds = new Rect(this.x + Menu.MARGIN, this.y + Menu.MARGIN - this.offset,
                this.width - (Menu.MARGIN * 3), this.getHeight() - (Menu.MARGIN * 2));
        if (bounds.contains(p)) {
            this.root.click(bounds, p);
        }
    }
}
