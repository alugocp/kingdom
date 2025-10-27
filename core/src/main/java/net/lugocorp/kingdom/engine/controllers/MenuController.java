package net.lugocorp.kingdom.engine.controllers;
import net.lugocorp.kingdom.engine.Settings;
import net.lugocorp.kingdom.engine.projection.ViewportLogic;
import net.lugocorp.kingdom.math.Point;
import net.lugocorp.kingdom.math.Rect;
import net.lugocorp.kingdom.menu.Menu;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Handles all user input meant for Menu objects in the game
 */
public class MenuController implements InputProcessor {
    private static final int SCROLL_SPEED = 20;
    private final TouchState touch = new TouchState();
    private final Supplier<Optional<Menu>> getMenu;
    private final Settings settings;

    public MenuController(Settings settings, Supplier<Optional<Menu>> getMenu) {
        this.settings = settings;
        this.getMenu = getMenu;
    }

    /**
     * Cancels an active TouchState in the controller
     */
    void cancel() {
        this.touch.reset();
    }

    /**
     * Resets the mouse position and triggers mouseMove logic
     */
    public void reset() {
        this.mouseMoved(-1, -1);
        final Point p = ViewportLogic.unproject(Gdx.input.getX(), Gdx.input.getY()).orElse(new Point(-1, -1));
        this.mouseMoved(p.x, p.y);
    }

    /**
     * Returns true if the given Point falls within the current Menu
     */
    public boolean isInMenu(Point p) {
        final Optional<Menu> menu = this.getMenu.get();
        return menu.isPresent() && menu.get().getBoundingRect().contains(p);
    }

    /**
     * Returns true if getMenu()'s return value is present
     */
    private boolean isRelevant() {
        return this.getMenu.get().isPresent();
    }

    /**
     * Returns true if we're clicking / dragging on the Menu's scroll gutter
     */
    private boolean startedInScrollGutter() {
        return this.touch.getOrigin().map((Point o) -> this.getMenu.get().flatMap((Menu m) -> m.getGutterBounds())
                .map((Rect r) -> r.contains(o)).orElse(false)).orElse(false);
    }

    /**
     * Causes the underlying Menu to scroll
     */
    private void scroll(int dy) {
        this.getMenu.get().ifPresent((Menu m) -> m.scroll(dy));
        final Point p = ViewportLogic.unproject(Gdx.input.getX(), Gdx.input.getY()).orElse(new Point(-1, -1));
        this.mouseMoved(p.x, p.y);
    }

    /** {@inheritdoc} */
    @Override
    public boolean touchDown​(int x, int y, int pointer, int button) {
        if (!this.isRelevant()) {
            return false;
        }
        final Point p = ViewportLogic.unproject(x, y).orElse(new Point(-1, -1));
        if (this.isInMenu(p)) {
            this.touch.start(p);
            return true;
        }
        return false;
    }

    /** {@inheritdoc} */
    @Override
    public boolean touchDragged​(int x, int y, int pointer) {
        if (!this.isRelevant() || !this.touch.isActive()) {
            return false;
        }
        final Optional<Point> p = ViewportLogic.unproject(x, y);
        if (p.isPresent()) {
            final Point prev = this.touch.update(p.get());
            if (this.touch.isDragging()) {
                this.scroll((int) ((p.get().y - prev.y)
                        * (this.startedInScrollGutter() ? this.getMenu.get().get().getScrollBarRatio() : -1f)));
            }
            return true;
        }
        return false;
    }

    /** {@inheritdoc} */
    @Override
    public boolean touchUp​(int x, int y, int pointer, int button) {
        if (!this.isRelevant() || !this.touch.isActive()) {
            return false;
        }
        if (!this.touch.isDragging()) {
            this.getMenu.get().get().click(ViewportLogic.unproject(x, y).orElse(new Point(-1, -1)));
        }
        this.touch.reset();
        return true;
    }

    /** {@inheritdoc} */
    @Override
    public boolean scrolled​(float dx, float dy) {
        this.scroll((dy > 0 ? -1 : 1) * (this.settings.getReversedScrollDirection() ? -1 : 1)
                * MenuController.SCROLL_SPEED);
        return false;
    }

    /** {@inheritdoc} */
    @Override
    public boolean touchCancelled​(int x, int y, int pointer, int button) {
        return false;
    }

    /** {@inheritdoc} */
    @Override
    public boolean mouseMoved​(int x, int y) {
        final Optional<Menu> menu = this.getMenu.get();
        if (menu.isPresent()) {
            return menu.get().mouseMoved(ViewportLogic.unproject(x, y).orElse(new Point(-1, -1)));
        }
        return false;
    }

    /** {@inheritdoc} */
    @Override
    public boolean keyDown​(int keycode) {
        return false;
    }

    /** {@inheritdoc} */
    @Override
    public boolean keyTyped​(char character) {
        return false;
    }

    /** {@inheritdoc} */
    @Override
    public boolean keyUp​(int keycode) {
        Optional<Menu> m = this.getMenu.get();
        if (m.isPresent()) {
            m.get().keyPressed(keycode);
            return true;
        }
        return false;
    }
}
