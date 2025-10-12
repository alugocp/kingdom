package net.lugocorp.kingdom.engine.controllers;
import net.lugocorp.kingdom.ui.Menu;
import net.lugocorp.kingdom.utils.math.Point;
import net.lugocorp.kingdom.utils.math.Rect;
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

    public MenuController(Supplier<Optional<Menu>> getMenu) {
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
        this.mouseMoved(Gdx.input.getX(), Gdx.input.getY());
    }

    /**
     * Returns true if the given Point falls within the current Menu
     */
    private boolean isInMenu(Point p) {
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
        this.mouseMoved(Gdx.input.getX(), Gdx.input.getY());
    }

    /** {@inheritdoc} */
    @Override
    public boolean touchDown​(int x, int y, int pointer, int button) {
        if (!this.isRelevant()) {
            return false;
        }
        final Point p = new Point(x, y);
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
        final Point p = new Point(x, y);
        final Point prev = this.touch.update(p);
        if (this.touch.isDragging()) {
            this.scroll((int) ((y - prev.y)
                    * (this.startedInScrollGutter() ? this.getMenu.get().get().getScrollBarRatio() : -1f)));
        }
        return true;
    }

    /** {@inheritdoc} */
    @Override
    public boolean touchUp​(int x, int y, int pointer, int button) {
        if (!this.isRelevant() || !this.touch.isActive()) {
            return false;
        }
        if (!this.touch.isDragging()) {
            this.getMenu.get().get().click(new Point(x, y));
        }
        this.touch.reset();
        return true;
    }

    /** {@inheritdoc} */
    @Override
    public boolean scrolled​(float dx, float dy) {
        this.scroll((dy > 0 ? -1 : 1) * MenuController.SCROLL_SPEED);
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
            return menu.get().mouseMoved(new Point(x, y));
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
