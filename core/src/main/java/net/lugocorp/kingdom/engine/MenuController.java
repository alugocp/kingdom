package net.lugocorp.kingdom.engine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import java.util.Optional;
import java.util.function.Function;
import net.lugocorp.kingdom.math.Point;
import net.lugocorp.kingdom.ui.Menu;

/**
 * Handles all user input meant for Menu objects in the game
 */
public class MenuController implements InputProcessor {
    private final Function<Void, Optional<Menu>> getMenu;
    private Optional<Point> prev = Optional.empty();
    private boolean dragging = false;

    public MenuController(Function<Void, Optional<Menu>> getMenu) {
        this.getMenu = getMenu;
    }

    /**
     * Returns true if the given Point falls within the current Menu
     */
    private boolean isInMenu(Point p) {
        final Optional<Menu> menu = this.getMenu.apply(null);
        return menu.isPresent() && menu.get().getBoundingRect().contains(p);
    }

    @Override
    public boolean touchDown​(int x, int y, int pointer, int button) {
        this.prev = Optional.of(new Point(x, y));
        return this.isInMenu(this.prev.get());
    }

    @Override
    public boolean touchDragged​(int x, int y, int pointer) {
        this.dragging = true;
        if (this.prev.isPresent() && this.isInMenu(this.prev.get()) && this.isInMenu(new Point(x, y))) {
            return this.scrolled(0, this.prev.get().y - y);
        }
        return false;
    }

    @Override
    public boolean touchUp​(int x, int y, int pointer, int button) {
        if (!this.dragging) {
            this.getMenu.apply(null).ifPresent((Menu m) -> m.click(new Point(x, y)));
        }
        this.dragging = false;
        this.prev = Optional.empty();
        return this.isInMenu(new Point(x, y));
    }

    @Override
    public boolean scrolled​(float dx, float dy) {
        if (this.isInMenu(new Point(Gdx.input.getX(), Gdx.input.getY()))) {
            this.getMenu.apply(null).ifPresent((Menu m) -> m.scroll((int) dy));
            return true;
        }
        return false;
    }

    @Override
    public boolean touchCancelled​(int x, int y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean mouseMoved​(int x, int y) {
        return false;
    }

    @Override
    public boolean keyDown​(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped​(char character) {
        return false;
    }

    @Override
    public boolean keyUp​(int keycode) {
        return false;
    }
}
