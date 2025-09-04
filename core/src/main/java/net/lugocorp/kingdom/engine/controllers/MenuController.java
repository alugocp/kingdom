package net.lugocorp.kingdom.engine.controllers;
import net.lugocorp.kingdom.ui.menu.Menu;
import net.lugocorp.kingdom.utils.math.Point;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Handles all user input meant for Menu objects in the game
 */
public class MenuController implements InputProcessor {
    private final Supplier<Optional<Menu>> getMenu;
    private Optional<Point> prev = Optional.empty();
    private boolean dragging = false;

    public MenuController(Supplier<Optional<Menu>> getMenu) {
        this.getMenu = getMenu;
    }

    /**
     * Returns true if the given Point falls within the current Menu
     */
    private boolean isInMenu(Point p) {
        final Optional<Menu> menu = this.getMenu.get();
        return menu.isPresent() && menu.get().getBoundingRect().contains(p);
    }

    /** {@inheritdoc} */
    @Override
    public boolean touchDown​(int x, int y, int pointer, int button) {
        this.prev = Optional.of(new Point(x, y));
        return this.isInMenu(this.prev.get());
    }

    /** {@inheritdoc} */
    @Override
    public boolean touchDragged​(int x, int y, int pointer) {
        this.dragging = true;
        if (this.prev.isPresent() && this.isInMenu(this.prev.get()) && this.isInMenu(new Point(x, y))) {
            return this.scrolled(0, this.prev.get().y - y);
        }
        return false;
    }

    /** {@inheritdoc} */
    @Override
    public boolean touchUp​(int x, int y, int pointer, int button) {
        boolean result = this.isInMenu(new Point(x, y));
        if (!this.dragging) {
            result = this.getMenu.get().map((Menu m) -> m.click(new Point(x, y))).orElse(false) || result;
        }
        this.dragging = false;
        this.prev = Optional.empty();
        return result;
    }

    /** {@inheritdoc} */
    @Override
    public boolean scrolled​(float dx, float dy) {
        if (this.isInMenu(new Point(Gdx.input.getX(), Gdx.input.getY()))) {
            this.getMenu.get().ifPresent((Menu m) -> m.scroll((int) -dy));
            return true;
        }
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
            menu.get().mouseMoved(new Point(x, y));
            return true;
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
