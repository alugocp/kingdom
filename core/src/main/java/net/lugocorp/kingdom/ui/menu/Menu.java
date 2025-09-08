package net.lugocorp.kingdom.ui.menu;
import net.lugocorp.kingdom.engine.AudioVideo;
import net.lugocorp.kingdom.utils.math.Coords;
import net.lugocorp.kingdom.utils.math.Point;
import net.lugocorp.kingdom.utils.math.Rect;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import java.util.Optional;

/**
 * Contains the logic to display some vertical menu content
 */
public class Menu {
    private static final int MINI_MENU_WIDTH = 250;
    private static final int MARGIN = 10;
    private final boolean tall;
    private final int width;
    private Optional<Point> prev = Optional.empty();
    private Optional<Point> curr = Optional.empty();
    private Optional<Menu> mini = Optional.empty();
    private boolean outlined = false;
    private int offset = 0;
    private int x;
    private int y;
    protected final MenuNode root;

    public Menu(int x, int y, int width, boolean tall, MenuNode root) {
        this.width = width;
        this.root = root;
        this.tall = tall;
        this.x = x;
        this.y = y;
        this.pack();
    }

    /**
     * Packs this Menu's constituent MenuNodes
     */
    public void pack() {
        this.root.pack(width - (Menu.MARGIN * 3));
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
     * Removes the mini menu from this Menu
     */
    void closeMiniMenu() {
        this.mini = Optional.empty();
    }

    /**
     * Sets a mini menu on this Menu
     */
    void setMiniMenu(MenuNode root, int x, int y) {
        final int xBounded = Math.min(Coords.SIZE.x - Menu.MINI_MENU_WIDTH, x);
        final int yBounded = Math.max(root.getHeight(), y);
        if (this.mini.isPresent() && this.mini.get().root == root) {
            this.mini.get().x = xBounded;
            this.mini.get().y = yBounded;
        } else {
            final Menu menu = new Menu(xBounded, yBounded, Menu.MINI_MENU_WIDTH, false, root);
            menu.outlined = true;
            this.mini = Optional.of(menu);
        }
    }

    /**
     * Returns the height of the Menu UI
     */
    public int getHeight() {
        int max = Coords.SIZE.y - this.y;
        return this.tall ? max : Math.min(max, this.root.getHeight() + (Menu.MARGIN * 2));
    }

    /**
     * Draws every MenuNode in this Menu
     */
    public void draw(AudioVideo av) {
        final int h = this.getHeight();
        Rect bg = Coords.screen.flip(this.x, this.y, this.width, h);
        av.shapes.begin(ShapeType.Filled);
        av.shapes.setColor(Color.BLACK);
        av.shapes.rect(bg.x, bg.y, bg.w, bg.h);
        av.shapes.end();
        if (this.outlined) {
            av.shapes.begin(ShapeType.Line);
            av.shapes.setColor(Color.WHITE);
            av.shapes.rect(bg.x, bg.y, bg.w, bg.h);
            av.shapes.end();
        }
        this.root.draw(av, new Rect(this.x + Menu.MARGIN, this.y + Menu.MARGIN - this.offset,
                this.width - (Menu.MARGIN * 3), h - (Menu.MARGIN * 2)));
        if (this.shouldScroll()) {
            final int rh = this.root.getHeight();
            Rect flip = Coords.screen.flip(this.x + this.width - Menu.MARGIN, this.y + (this.offset * h / rh),
                    Menu.MARGIN, (h * h) / rh);
            av.shapes.begin(ShapeType.Filled);
            av.shapes.setColor(Color.TEAL);
            av.shapes.rect(flip.x, flip.y, flip.w, flip.h);
            av.shapes.end();
        }
        this.mini.ifPresent((Menu m) -> m.draw(av));
    }

    /**
     * Propagates a signal to activate some click logic
     */
    public boolean click(Point p) {
        if (this.mini.isPresent() && this.mini.get().click(p)) {
            this.root.unclick();
            return true;
        }
        this.closeMiniMenu();
        final Rect bounds = new Rect(this.x + Menu.MARGIN, this.y + Menu.MARGIN - this.offset,
                this.width - (Menu.MARGIN * 3), this.root.getHeight());
        Point p1 = new Point(p.x * Coords.SIZE.x / Gdx.graphics.getWidth(),
                p.y * Coords.SIZE.y / Gdx.graphics.getHeight());
        if (bounds.contains(p1)) {
            this.root.click(this, bounds, p1);
            return true;
        }
        this.root.unclick();
        return false;
    }

    /**
     * Propagates a signal to activate some mouse moved logic
     */
    public boolean mouseMoved(Point p) {
        final Rect bounds = new Rect(this.x + Menu.MARGIN, this.y + Menu.MARGIN - this.offset,
                this.width - (Menu.MARGIN * 3), this.root.getHeight());
        Point p1 = new Point(p.x * Coords.SIZE.x / Gdx.graphics.getWidth(),
                p.y * Coords.SIZE.y / Gdx.graphics.getHeight());
        this.prev = curr;
        this.curr = Optional.of(p1);
        if (this.prev.isPresent()) {
            this.root.mouseMoved(bounds, this.prev.get(), this.curr.get());
            if (bounds.contains(this.prev.get()) || bounds.contains(this.curr.get())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Propagates a signal when some key is pressed
     */
    public void keyPressed(int keycode) {
        this.root.keyPressed(keycode);
    }
}
