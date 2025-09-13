package net.lugocorp.kingdom.engine.controllers;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.math.Coords;
import net.lugocorp.kingdom.utils.math.Hexagons;
import net.lugocorp.kingdom.utils.math.Point;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import java.util.Optional;

/**
 * Handles all user control input for the GameView
 */
public class GameViewController extends CameraInputController {
    private static final float MAX_ZOOM = 3.0f;
    private static final float MIN_ZOOM = -2.0f;
    private final MenuController popupMenu;
    private final MenuController hudMenu;
    private final MenuController menu;
    private final GameView view;
    private Optional<Point> prev = Optional.empty();
    private float currentZoom = 0.0f;
    private boolean dragging = false;

    public GameViewController(GameView view, MenuController menu, Camera camera) {
        super(camera);
        this.popupMenu = new MenuController(
                () -> view.game.mechanics.turns.canHumanPlayerAct() && view.popups.isDisplayed()
                        ? view.popups.get()
                        : Optional.empty());
        this.hudMenu = new MenuController(() -> Optional.of(view.hud));
        this.menu = menu;
        this.view = view;
    }

    /**
     * Centers the Camera on a given Point in the World
     */
    public void centerCameraOn(Point p) {
        Vector3 vec = Coords.grid.vector(p.x, p.y);
        Vector3 endpoint = this.getScreenPointOnSurface(Coords.SIZE.x / 2, Coords.SIZE.y / 2);
        this.moveCamera(vec.x - endpoint.x, vec.z - endpoint.z);
    }

    /**
     * Calculates the point on the surface of the World that corresponds to a point
     * on the viewing area (the plane where the mouse lives)
     */
    private Vector3 getScreenPointOnSurface(int x, int y) {
        Ray ray = this.camera.getPickRay(x, y);
        float distance = (Hexagons.HEIGHT - ray.origin.y) / ray.direction.y;
        return ray.getEndPoint(new Vector3(), distance);
    }

    /**
     * Returns the Tile coordinate in the World that lives under the given Point on
     * the screen
     */
    public Point getCoordUnderScreenPoint(int x, int y) {
        // Cast out a ray from the mouseover point and find its point along the Y = 0
        // plane. Then find which hexagon that point falls in on the world grid.
        Vector3 endpoint = this.getScreenPointOnSurface(x, y);
        int minZ = (int) Math.floor(endpoint.z / (Hexagons.DEPTH - Hexagons.DEPTH_DIFF));
        float lowestDist2 = Integer.MAX_VALUE;
        Point closestPoint = null;
        for (int a = 0; a < 2; a++) {
            int minX = (int) Math.floor((endpoint.x / Hexagons.WIDTH) - (minZ % 2 == 0 ? 0 : 0.5));
            for (int b = 0; b < 2; b++) {
                float dist = Coords.grid.vector(minX + b, minZ + a).dst2(endpoint);
                if (dist < lowestDist2) {
                    lowestDist2 = dist;
                    closestPoint = new Point(minX + b, minZ + a);
                }
            }
        }
        return closestPoint;
    }

    /**
     * Internal function translates the camera while respecting World boundaries
     */
    private void moveCamera(float dx, float dz) {
        // Check bottom-left Camera bounds
        Vector3 p1 = this.getScreenPointOnSurface(0, Coords.SIZE.y).add(Coords.raw.vector(dx, 0f, dz));
        Vector3 botLeft = Coords.grid.vector(0, this.view.game.world.getHeight());
        if (p1.x < botLeft.x) {
            dx += botLeft.x - p1.x;
        }
        if (p1.z > botLeft.z) {
            dz += botLeft.z - p1.z;
        }

        // Check bottom-right Camera bounds
        Vector3 p2 = this.getScreenPointOnSurface(Coords.SIZE.x, Coords.SIZE.y).add(Coords.raw.vector(dx, 0f, dz));
        Vector3 botRight = Coords.grid.vector(this.view.game.world.getWidth(), this.view.game.world.getHeight());
        if (p2.x > botRight.x) {
            dx += botRight.x - p2.x;
        }

        // Check top-left Camera bounds
        Vector3 p3 = this.getScreenPointOnSurface(0, this.view.hud.getHeight()).add(Coords.raw.vector(dx, 0f, dz));
        Vector3 topLeft = Coords.grid.vector(0, -8);
        if (p3.z < topLeft.z) {
            dz += topLeft.z - p3.z;
        }

        // Translate by correction amount
        this.camera.translate(dx, 0f, dz);
        this.camera.update();
    }

    /** {@inheritdoc} */
    @Override
    public boolean touchDown​(int x, int y, int pointer, int button) {
        // Turn off controls during Animations
        if (this.view.animations.inProgress()) {
            return true;
        }

        // Menu logic
        if (this.popupMenu.touchDown(x, y, pointer, button) || this.view.popups.isDisplayed()) {
            return true;
        }
        if (this.menu.touchDown(x, y, pointer, button)) {
            return true;
        }

        // Handle HUD UI and game interface
        if (this.hudMenu.touchDown(x, y, pointer, button)) {
            return true;
        }
        this.prev = Optional.of(new Point(x, y));
        return true;
    }

    /** {@inheritdoc} */
    @Override
    public boolean touchUp(int x, int y, int pointer, int button) {
        // Turn off controls during Animations
        if (this.view.animations.inProgress()) {
            return true;
        }

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
        if (!this.dragging) {
            if (this.view.selector.isHoveringSelectedTile()) {
                this.view.selector.runSelectionAction();
            } else if (this.view.selector.isHoveringVisibleTile()) {
                this.view.av.loaders.sounds.play("sfx/select-unit");
                this.view.menu.open();
            } else {
                this.view.logger.error("Cannot view tile under fog of war", true);
            }
        }
        this.prev = Optional.empty();
        this.dragging = false;
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
        if (!this.prev.isPresent()) {
            return false;
        }
        Point p = this.prev.get();
        this.moveCamera((float) (p.x - x) / 100, (float) (p.y - y) / 100);
        this.prev = Optional.of(new Point(x, y));
        this.dragging = true;
        return true;
    }

    /** {@inheritdoc} */
    @Override
    public boolean zoom(float amount) {
        // Menu logic
        if (this.popupMenu.scrolled(0, amount)) {
            return true;
        }
        if (this.menu.scrolled(0, amount)) {
            return true;
        }
        if (this.view.menu.get().isPresent() || this.view.popups.isDisplayed()) {
            return true;
        }

        // Handle game interface
        final float diff = Math.max(GameViewController.MIN_ZOOM,
                Math.min(GameViewController.MAX_ZOOM, this.currentZoom + amount)) - this.currentZoom;
        this.currentZoom += diff;
        this.moveCamera(0f, 0f);
        return diff == 0 ? false : super.zoom(amount);
    }

    /** {@inheritdoc} */
    @Override
    public boolean mouseMoved​(int x, int y) {
        if (this.popupMenu.mouseMoved(x, y)) {
            return true;
        }
        if (this.hudMenu.mouseMoved(x, y)) {
            return true;
        }
        if (this.menu.mouseMoved(x, y)) {
            return true;
        }
        Point closestPoint = this.getCoordUnderScreenPoint(x, y);
        this.view.selector.hover(closestPoint);
        return true;
    }
}
