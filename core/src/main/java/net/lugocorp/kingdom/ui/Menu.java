package net.lugocorp.kingdom.ui;
import net.lugocorp.kingdom.engine.AudioVideo;
import net.lugocorp.kingdom.utils.math.Coords;
import net.lugocorp.kingdom.utils.math.Point;
import net.lugocorp.kingdom.utils.math.Rect;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import java.util.Optional;

/**
 * Contains the logic to display some vertical menu content
 */
public class Menu {
    private static final int MINI_MENU_WIDTH = 250;
    private static final int MARGIN = 15;
    private final boolean tall;
    private int width;
    private Optional<Menu> submenu = Optional.empty();
    private Optional<Point> prev = Optional.empty();
    private Optional<Point> curr = Optional.empty();
    private Optional<Menu> mini = Optional.empty();
    private boolean scrollBarHighlighted = false;
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
     * Adds an outline to this Menu
     */
    public void outline() {
        this.outlined = true;
    }

    /**
     * Packs this Menu's constituent MenuNodes
     */
    public void pack() {
        this.root.pack(this, this.width - (Menu.MARGIN * 3));
    }

    /**
     * Changes this Menu's width and re-packs the node tree
     */
    public void setWidth(int width) {
        this.width = width;
        this.pack();
    }

    /**
     * Registers a submenu on this Menu (a nested Menu that should receive
     * non-MenuNode signals)
     */
    public void setSubmenu(Menu m) {
        if (this.submenu.isPresent()) {
            throw new RuntimeException("Cannot set more than one submenu on the same Menu");
        }
        this.submenu = Optional.of(m);
    }

    /**
     * Returns true if this Menu should allow for scrolling
     */
    public boolean shouldScroll() {
        return this.submenu.map((Menu m) -> m.shouldScroll()).orElse(this.root.getHeight() > this.getHeight());
    }

    /**
     * Scrolls this Menu by some set amount
     */
    public void scroll(int dy) {
        // Submenu support
        if (this.submenu.isPresent()) {
            this.submenu.get().scroll(dy);
            return;
        }

        // Original implementation
        if (this.shouldScroll()) {
            this.offset = Math.max(0, Math.min(this.root.getHeight() - this.getHeight(), this.offset + dy));
        }
    }

    /**
     * Returns the bounds of this Menu's scroll gutter
     */
    public Optional<Rect> getGutterBounds() {
        return this.submenu.map((Menu m) -> m.getGutterBounds())
                .orElse(this.shouldScroll()
                        ? Optional
                                .of(new Rect(this.x + this.width - Menu.MARGIN, this.y, Menu.MARGIN, this.getHeight()))
                        : Optional.empty());
    }

    /**
     * Returns the bounds of this Menu's scroll bar
     */
    public Optional<Rect> getScrollBarBounds() {
        // Submenu support
        if (this.submenu.isPresent()) {
            return this.submenu.get().getScrollBarBounds();
        }

        // Original implementation
        final int h = this.getHeight();
        final int rh = this.root.getHeight();
        return this.shouldScroll()
                ? Optional.of(new Rect(this.x + this.width - Menu.MARGIN, this.y + (this.offset * h / rh), Menu.MARGIN,
                        (h * h) / rh))
                : Optional.empty();
    }

    /**
     * Sets this Menu's x position
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * Sets this Menu's y position
     */
    public void setY(int y) {
        this.y = y;
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
    public void closeMiniMenu() {
        this.mini = Optional.empty();
    }

    /**
     * Returns the mini-Menu's root MenuNode (if any)
     */
    public Optional<MenuNode> getMiniMenuRoot() {
        return this.mini.map((Menu m) -> m.root);
    }

    /**
     * Sets a mini menu on this Menu
     */
    public void setMiniMenu(MenuNode root, int x, int y) {
        final int xBounded = Math.min(Coords.SIZE.x - Menu.MINI_MENU_WIDTH, x);
        if (this.getMiniMenuRoot().map((MenuNode n) -> n == root).orElse(false)) {
            this.mini.get().x = xBounded;
        } else {
            final Menu menu = new Menu(xBounded, y, Menu.MINI_MENU_WIDTH, false, root);
            menu.outlined = true;
            this.mini = Optional.of(menu);
        }
        this.mini.get().y = Math.min(Coords.SIZE.y - (this.mini.get().root.getHeight() + (Menu.MARGIN * 2)), y);
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
        final Rect bg = Coords.screen.flip(this.x, this.y, this.width, h);

        // Draw black background
        av.shapes.begin(ShapeType.Filled);
        av.shapes.setColor(ColorScheme.MENU.color);
        av.shapes.rect(bg.x, bg.y, bg.w, bg.h);
        av.shapes.end();

        // Draw Menu content
        Gdx.gl.glEnable(GL20.GL_SCISSOR_TEST);
        Gdx.gl.glScissor(bg.x, bg.y, bg.w, bg.h);
        this.root.draw(av, new Rect(this.x + Menu.MARGIN, this.y + Menu.MARGIN - this.offset,
                this.width - (Menu.MARGIN * 3), h - (Menu.MARGIN * 2)));
        Gdx.gl.glDisable(GL20.GL_SCISSOR_TEST);

        // Draw scrollbar
        if (this.shouldScroll()) {
            final Rect gutter = Coords.screen.flip(this.getGutterBounds().get());
            final Rect scrollbar = Coords.screen.flip(this.getScrollBarBounds().get());
            av.shapes.begin(ShapeType.Filled);
            av.shapes.setColor(ColorScheme.GUTTER.color);
            av.shapes.rect(gutter.x, gutter.y, gutter.w, gutter.h);
            av.shapes.setColor(this.scrollBarHighlighted ? ColorScheme.HOVER.color : ColorScheme.BUTTON.color);
            av.shapes.rect(scrollbar.x, scrollbar.y, scrollbar.w, scrollbar.h);
            av.shapes.end();
        }

        // Draw Menu outline
        if (this.outlined) {
            av.shapes.begin(ShapeType.Line);
            av.shapes.setColor(ColorScheme.OUTLINE.color);
            av.shapes.rect(bg.x, bg.y, bg.w, bg.h);
            av.shapes.end();
        }

        // Draw mini-Menu
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
            this.root.click(bounds, p1);
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
        final Point p1 = new Point(p.x * Coords.SIZE.x / Gdx.graphics.getWidth(),
                p.y * Coords.SIZE.y / Gdx.graphics.getHeight());
        this.prev = curr;
        this.curr = Optional.of(p1);
        this.scrollBarHighlighted = this.getScrollBarBounds().map((Rect r) -> r.contains(p1)).orElse(false);
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
