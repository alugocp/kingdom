package net.lugocorp.kingdom.engine.controllers;
import net.lugocorp.kingdom.engine.projection.CameraLogic;
import net.lugocorp.kingdom.engine.projection.ViewportLogic;
import net.lugocorp.kingdom.game.model.Tile;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.math.Coords;
import net.lugocorp.kingdom.math.Point;
import net.lugocorp.kingdom.settings.Settings;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.Lambda;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;
import java.util.List;
import java.util.Optional;

/**
 * Handles all user control input for the GameView
 */
public class GameViewController implements InputProcessor {
    private static final float SCROLL_SPEED = 2f;
    private static final float ZOOM_SPEED = 1f;
    private static final float MAX_ZOOM = 3.0f;
    private static final float MIN_ZOOM = -2.0f;
    private final TouchState touch = new TouchState();
    private final Vector3 vector = new Vector3();
    private final List<MenuController> menus;
    private final GameView view;
    private final Camera camera;
    private float currentZoom = 0.0f;
    public final KeyState keys = new KeyState();

    public GameViewController(GameView view, Settings settings, Camera camera) {
        this.menus = view.hud.getControllers(view, settings);
        this.camera = camera;
        this.view = view;
    }

    /**
     * Centers the Camera on a given Point in the World
     */
    public void centerCameraOn(Point p) {
        final Vector3 vec = Coords.grid.vector(p.x, p.y);
        final Vector3 endpoint = CameraLogic.getScreenPointOnSurface(Coords.SIZE.x / 2, Coords.SIZE.y / 2);
        this.moveCamera(vec.x - endpoint.x, vec.z - endpoint.z);
    }

    /**
     * Internal function translates the camera while respecting World boundaries
     */
    private void moveCamera(float dx, float dz) {
        // Check bottom-left Camera bounds
        final Vector3 p1 = CameraLogic.getScreenPointOnSurface(ViewportLogic.project(0, Coords.SIZE.y))
                .add(Coords.raw.vector(dx, 0f, dz));
        final Vector3 botLeft = Coords.grid.vector(0, this.view.game.world.getHeight() + 4);
        if (p1.x < botLeft.x) {
            dx += botLeft.x - p1.x;
        }
        if (p1.z > botLeft.z) {
            dz += botLeft.z - p1.z;
        }

        // Check bottom-right Camera bounds
        final Vector3 p2 = CameraLogic.getScreenPointOnSurface(ViewportLogic.project(Coords.SIZE.x, Coords.SIZE.y))
                .add(Coords.raw.vector(dx, 0f, dz));
        final Vector3 botRight = Coords.grid.vector(this.view.game.world.getWidth(),
                this.view.game.world.getHeight() + 4);
        if (p2.x > botRight.x) {
            dx += botRight.x - p2.x;
        }

        // Check top-left Camera bounds
        final Vector3 p3 = CameraLogic.getScreenPointOnSurface(ViewportLogic.project(0, this.view.hud.top.getHeight()))
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

    /**
     * Selects (and moves to) a new Unit in the World
     */
    private void cycleUnits() {
        final Point point = this.view.hud.bot.tileMenu.get();
        final Optional<Unit> unit = this.view.game.world.getTile(point).flatMap((Tile t) -> t.unit);
        final List<Unit> units = Lambda.toList(this.view.game.human.units);
        final int i = unit.map((Unit u) -> u.leadership.belongsToHuman()).orElse(false)
                ? (units.indexOf(unit.get()) + 1) % units.size()
                : 0;
        final Point p = units.get(i).getPoint();
        this.view.hud.bot.tileMenu.set(p);
        this.view.centerOnPoint(p, false);
    }

    /** {@inheritdoc} */
    @Override
    public boolean touchDown​(int x, int y, int pointer, int button) {
        // Turn off MenuController pushdown during animations
        final boolean animating = this.view.animations.inProgress();

        // Menu logic
        for (MenuController m : this.menus) {
            if (m.touchDown(x, y, pointer, button)) {
                if (animating) {
                    m.cancel();
                }
                return true;
            }
        }

        // Do not click on the World if we've clicked on the Minimap
        final Optional<Point> p = ViewportLogic.unproject(x, y);
        if (p.map((Point p1) -> p1.y >= Coords.SIZE.y - this.view.hud.bot.getHeight()).orElse(true)) {
            return true;
        }

        // Game World logic
        this.touch.start(p.get());
        return true;
    }

    /** {@inheritdoc} */
    @Override
    public boolean touchUp(int x, int y, int pointer, int button) {
        // Menu logic
        for (MenuController m : this.menus) {
            if (m.touchUp(x, y, pointer, button)) {
                return true;
            }
        }

        // Minimap logic
        if (!this.touch.isActive()) {
            final Optional<Point> p = ViewportLogic.unproject(x, y);
            if (p.map((Point p1) -> !this.view.hud.bot.minimap.click(this.view, p1)).orElse(false)) {
                return true;
            }
        }

        // Game World logic
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
        for (MenuController m : this.menus) {
            if (m.touchDragged(x, y, pointer)) {
                return true;
            }
        }

        // Game World logic
        if (this.touch.isActive()) {
            final Optional<Point> p = ViewportLogic.unproject(x, y);
            if (p.isPresent()) {
                final Point prev = this.touch.update(p.get());
                if (this.touch.isDragging()) {
                    this.moveCamera((float) (prev.x - p.get().x) / 100, (float) (prev.y - p.get().y) / 100);
                    return true;
                }
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
        for (MenuController m : this.menus) {
            if (ViewportLogic.unproject(Gdx.input.getX(), Gdx.input.getY()).map((Point p) -> m.isInMenu(p))
                    .orElse(false)) {
                m.scrolled(0, dy);
                return true;
            }
        }

        // Handle game interface
        this.zoomCamera((dy > 0 ? -1 : 1) * GameViewController.ZOOM_SPEED);
        return true;
    }

    /** {@inheritdoc} */
    @Override
    public boolean mouseMoved​(int x, int y) {
        // Menu logic
        for (MenuController m : this.menus) {
            if (m.mouseMoved(x, y)) {
                return true;
            }
        }

        // Do not check the World if we're hovering over a permanent HUD element
        if (!this.view.isHoveringOverGameWorld(x, y)) {
            return true;
        }

        // Unit/Building mouse over logic
        final Point b = this.view.getFrameBufferMappedPoint();
        if (!(b.x == -1 && b.y == -1)) {
            this.view.selector.hover(b);
            return true;
        }

        // Tile mouse over logic
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

        // Unit selection
        this.keys.check(keycode, Keys.NUMPAD_ENTER, () -> this.view.hud.bot.turnButton.finishTurn(this.view, true));
        this.keys.check(keycode, Keys.ENTER, () -> this.view.hud.bot.turnButton.finishTurn(this.view, true));
        this.keys.check(keycode, Keys.TAB, () -> this.cycleUnits());
        this.keys.check(keycode, Keys.ESCAPE, () -> {
            if (this.view.hud.popups.isDisplayed()) {
                this.view.hud.popups.setDisplay(false);
            } else {
                this.view.selector.resetMode();
            }
        });

        // Menu logic
        for (MenuController m : this.menus) {
            m.keyUp(keycode);
        }
        return false;
    }

    /** {@inheritdoc} */
    @Override
    public boolean keyTyped​(char keycode) {
        return false;
    }
}
