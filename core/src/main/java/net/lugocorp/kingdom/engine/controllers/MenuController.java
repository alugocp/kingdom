package net.lugocorp.kingdom.engine.controllers;
import net.lugocorp.kingdom.ui.Menu;
import net.lugocorp.kingdom.utils.math.Point;
import com.badlogic.gdx.InputProcessor;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Handles all user input meant for Menu objects in the game
 */
public class MenuController implements InputProcessor {
    private final TouchState state = new TouchState();
    private final Supplier<Optional<Menu>> getMenu;

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

    /**
     * Returns true if getMenu()'s return value is present
     */
    private boolean isRelevant() {
        return this.getMenu.get().isPresent();
    }

    /** {@inheritdoc} */
    @Override
    public boolean touchDown​(int x, int y, int pointer, int button) {
        if (!this.isRelevant()) {
            return false;
        }
        final Point p = new Point(x, y);
        if (this.isInMenu(p)) {
            this.state.start(p);
            return true;
        }
        return false;
    }

    /** {@inheritdoc} */
    @Override
    public boolean touchDragged​(int x, int y, int pointer) {
        if (!this.isRelevant() || !this.state.isActive()) {
            return false;
        }
        final Point p = new Point(x, y);
        final Point prev = this.state.update(p);
        if (this.state.isDragging()) {
            this.scrolled(0, prev.y - y);
        }
        return true;
    }

    /** {@inheritdoc} */
    @Override
    public boolean touchUp​(int x, int y, int pointer, int button) {
        if (!this.isRelevant() || !this.state.isActive()) {
            return false;
        }
        if (!this.state.isDragging()) {
            this.getMenu.get().get().click(new Point(x, y));
        }
        this.state.reset();
        return true;
    }

    /** {@inheritdoc} */
    @Override
    public boolean scrolled​(float dx, float dy) {
        this.getMenu.get().ifPresent((Menu m) -> m.scroll((int) -dy));
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
