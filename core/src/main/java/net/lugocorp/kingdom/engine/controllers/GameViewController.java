package net.lugocorp.kingdom.engine.controllers;
import net.lugocorp.kingdom.engine.Settings;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.logic.CameraLogic;
import net.lugocorp.kingdom.utils.logic.ViewportLogic;
import net.lugocorp.kingdom.utils.math.Coords;
import net.lugocorp.kingdom.utils.math.Point;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;
import java.util.Optional;

/**
 * Handles all user control input for the GameView
 */
public class GameViewController implements InputProcessor {
    private static final float SCROLL_SPEED = 3f;
    private static final float ZOOM_SPEED = 1f;
    private static final float MAX_ZOOM = 3.0f;
    private static final float MIN_ZOOM = -2.0f;
    private final TouchState touch = new TouchState();
    private final Vector3 vector = new Vector3();
    private final MenuController popupMenu;
    private final MenuController hudMenu;
    private final MenuController menu;
    private final GameView view;
    private final Camera camera;
    private float currentZoom = 0.0f;
    public final KeyState keys = new KeyState();

    public GameViewController(GameView view, Settings settings, MenuController menu, Camera camera) {
        this.popupMenu = new MenuController(settings,
                () -> view.game.mechanics.turns.canHumanPlayerAct() && view.popups.isDisplayed()
                        ? view.popups.get()
                        : Optional.empty());
        this.hudMenu = new MenuController(settings, () -> Optional.of(view.hud));
        this.camera = camera;
        this.menu = menu;
        this.view = view;
    }

    /**
     * Centers the Camera on a given Point in the World
     */
    public void centerCameraOn(Point p) {
        Vector3 vec = Coords.grid.vector(p.x, p.y);
        Vector3 endpoint = CameraLogic.getScreenPointOnSurface(Coords.SIZE.x / 2, Coords.SIZE.y / 2);
        this.moveCamera(vec.x - endpoint.x, vec.z - endpoint.z);
    }

    /**
     * Internal function translates the camera while respecting World boundaries
     */
    private void moveCamera(float dx, float dz) {
        // Check bottom-left Camera bounds
        final Vector3 p1 = CameraLogic.getScreenPointOnSurface(ViewportLogic.project(0, Coords.SIZE.y))
                .add(Coords.raw.vector(dx, 0f, dz));
        final Vector3 botLeft = Coords.grid.vector(0, this.view.game.world.getHeight());
        if (p1.x < botLeft.x) {
            dx += botLeft.x - p1.x;
        }
        if (p1.z > botLeft.z) {
            dz += botLeft.z - p1.z;
        }

        // Check bottom-right Camera bounds
        final Vector3 p2 = CameraLogic.getScreenPointOnSurface(ViewportLogic.project(Coords.SIZE.x, Coords.SIZE.y))
                .add(Coords.raw.vector(dx, 0f, dz));
        final Vector3 botRight = Coords.grid.vector(this.view.game.world.getWidth(), this.view.game.world.getHeight());
        if (p2.x > botRight.x) {
            dx += botRight.x - p2.x;
        }

        // Check top-left Camera bounds
        final Vector3 p3 = CameraLogic.getScreenPointOnSurface(ViewportLogic.project(0, this.view.hud.getHeight()))
                .add(Coords.raw.vector(dx, 0f, dz));
        final Vector3 topLeft = Coords.grid.vector(0, -8);
        if (p3.z < topLeft.z) {
            dz += topLeft.z - p3.z;
        }

        // Translate by correction amount
        this.camera.translate(dx, 0f, dz);
        this.camera.update();
    }

    /**
     * Internal function handles zooming the Camera in or out
     */
    private void zoomCamera(float amount) {
        // Calculate a good value to actually zoom by (apply min/max bounds)
        final float diff = Math.max(GameViewController.MIN_ZOOM,
                Math.min(GameViewController.MAX_ZOOM, this.currentZoom + amount)) - this.currentZoom;
        this.currentZoom += diff;

        // Move the Camera if we want to zoom by a nonzero amount
        if (diff != 0) {
            this.camera.translate(this.vector.set(camera.direction).scl(amount));

            // This moveCamera() call keeps the Camera within World bounds after zooming
            this.moveCamera(0f, 0f);
        }
    }

    /** {@inheritdoc} */
    @Override
    public boolean touchDown​(int x, int y, int pointer, int button) {
        // Turn off MenuController pushdown during animations
        final boolean animating = this.view.animations.inProgress();

        // Menu logic
        if (this.popupMenu.touchDown(x, y, pointer, button) || this.view.popups.isDisplayed()) {
            if (animating) {
                this.popupMenu.cancel();
            }
            return true;
        }
        if (this.menu.touchDown(x, y, pointer, button)) {
            if (animating) {
                this.menu.cancel();
            }
            return true;
        }
        if (this.hudMenu.touchDown(x, y, pointer, button)) {
            if (animating) {
                this.hudMenu.cancel();
            }
            return true;
        }

        // Game World logic
        this.touch.start(ViewportLogic.unproject(x, y));
        return true;
    }

    /** {@inheritdoc} */
    @Override
    public boolean touchUp(int x, int y, int pointer, int button) {
        // Menu logic
        if (this.popupMenu.touchUp(x, y, pointer, button)) {
            return true;
        } else if (this.view.popups.isDisplayed()) {
            this.view.popups.setDisplay(false);
            return true;
        }
        if (this.menu.touchUp(x, y, pointer, button)) {
            return true;
        }

        // Handle HUD UI and game interface
        if (this.hudMenu.touchUp(x, y, pointer, button)) {
            return true;
        }
        if (this.touch.isActive()) {
            if (!this.touch.isDragging() && !this.view.animations.inProgress()) {
                this.view.selector.click();
            }
            this.touch.reset();
        }
        return true;
    }

    /** {@inheritdoc} */
    @Override
    public boolean touchDragged​(int x, int y, int pointer) {
        // Menu logic
        if (this.popupMenu.touchDragged(x, y, pointer) || this.view.popups.isDisplayed()) {
            return true;
        }
        if (this.menu.touchDragged(x, y, pointer)) {
            return true;
        }

        // Handle HUD UI and game interface
        if (this.hudMenu.touchDragged(x, y, pointer)) {
            return true;
        }
        if (this.touch.isActive()) {
            final Point p = ViewportLogic.unproject(x, y);
            final Point prev = this.touch.update(p);
            if (this.touch.isDragging()) {
                this.moveCamera((float) (prev.x - p.x) / 100, (float) (prev.y - p.y) / 100);
                return true;
            }
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
    public boolean scrolled(float dx, float dy) {
        // Menu logic
        if (this.popupMenu.scrolled(0, dy)) {
            return true;
        }
        if (this.menu.scrolled(0, dy)) {
            return true;
        }
        if (this.view.menu.get().isPresent() || this.view.popups.isDisplayed()) {
            return true;
        }

        // Handle game interface
        this.zoomCamera((dy > 0 ? -1 : 1) * GameViewController.ZOOM_SPEED);
        return true;
    }

    /** {@inheritdoc} */
    @Override
    public boolean mouseMoved​(int x, int y) {
        // Menu mouse logic
        if (this.popupMenu.mouseMoved(x, y)) {
            return true;
        }
        if (this.hudMenu.mouseMoved(x, y)) {
            return true;
        }
        if (this.menu.mouseMoved(x, y)) {
            return true;
        }

        // Unit/Building/Tile mouse over logic
        final Point b = this.view.getFrameBufferMappedPoint();
        if (!(b.x == -1 && b.y == -1)) {
            this.view.selector.hover(new Point(b.x, b.y));
            return true;
        }
        final Point closestPoint = CameraLogic.getCoordUnderScreenPoint(x, y);
        this.view.selector.hover(closestPoint);
        return true;
    }

    /** {@inheritdoc} */
    @Override
    public boolean keyDown​(int keycode) {
        // TODO have the up/down and w/s keys affect menus if they're open?

        // WASD
        this.keys.down(keycode, Keys.W, () -> this.moveCamera(0, -GameViewController.SCROLL_SPEED));
        this.keys.down(keycode, Keys.A, () -> this.moveCamera(-GameViewController.SCROLL_SPEED, 0));
        this.keys.down(keycode, Keys.S, () -> this.moveCamera(0, GameViewController.SCROLL_SPEED));
        this.keys.down(keycode, Keys.D, () -> this.moveCamera(GameViewController.SCROLL_SPEED, 0));

        // Arrow keys
        this.keys.down(keycode, Keys.UP, () -> this.moveCamera(0, -GameViewController.SCROLL_SPEED));
        this.keys.down(keycode, Keys.LEFT, () -> this.moveCamera(-GameViewController.SCROLL_SPEED, 0));
        this.keys.down(keycode, Keys.DOWN, () -> this.moveCamera(0, GameViewController.SCROLL_SPEED));
        this.keys.down(keycode, Keys.RIGHT, () -> this.moveCamera(GameViewController.SCROLL_SPEED, 0));
        return false;
    }

    /** {@inheritdoc} */
    @Override
    public boolean keyUp​(int keycode) {
        this.keys.up(keycode);
        return false;
    }

    /** {@inheritdoc} */
    @Override
    public boolean keyTyped​(char character) {
        return false;
    }
}
