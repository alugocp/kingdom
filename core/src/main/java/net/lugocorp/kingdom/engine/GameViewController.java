package net.lugocorp.kingdom.engine;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import java.util.Optional;
import net.lugocorp.kingdom.math.Coords;
import net.lugocorp.kingdom.math.Hexagons;
import net.lugocorp.kingdom.math.Point;
import net.lugocorp.kingdom.ui.views.GameView;

/**
 * Handles all user control input for the GameView
 */
public class GameViewController extends CameraInputController {
    private static final float MAX_ZOOM = 3.0f;
    private static final float MIN_ZOOM = -2.0f;
    private final MenuController turnMenu;
    private final MenuController menu;
    private final GameView view;
    private Optional<Point> prev = Optional.empty();
    private float currentZoom = 0.0f;
    private boolean dragging = false;

    public GameViewController(GameView view, MenuController menu, Camera camera) {
        super(camera);
        this.turnMenu = new MenuController(
                () -> view.game.getTurnPlayer().isHumanPlayer() ? Optional.of(view.hud.turnMenu) : Optional.empty());
        this.menu = menu;
        this.view = view;
    }

    /** {@inheritdoc} */
    @Override
    public boolean touchDown​(int x, int y, int pointer, int button) {
        if (this.menu.touchDown(x, y, pointer, button)) {
            return true;
        }
        if (this.turnMenu.touchDown(x, y, pointer, button)) {
            return true;
        }
        this.prev = Optional.of(new Point(x, y));
        return true;
    }

    /** {@inheritdoc} */
    @Override
    public boolean touchUp(int x, int y, int pointer, int button) {
        if (this.menu.touchUp(x, y, pointer, button)) {
            return true;
        }
        if (this.turnMenu.touchUp(x, y, pointer, button)) {
            return true;
        }
        if (!this.dragging) {
            if (this.view.isHoveringSelectionTile()) {
                this.view.triggerSelectionAction();
            } else {
                this.view.openTileMenu();
            }
        }
        this.prev = Optional.empty();
        this.dragging = false;
        return true;
    }

    /** {@inheritdoc} */
    @Override
    public boolean touchDragged​(int x, int y, int pointer) {
        if (this.menu.touchDragged(x, y, pointer)) {
            return true;
        }
        if (this.turnMenu.touchDragged(x, y, pointer)) {
            return true;
        }
        if (!this.prev.isPresent()) {
            return false;
        }
        Point p = this.prev.get();
        this.camera.translate(Coords.raw.vector((float) (x - p.x) / 100, 0f, (float) (p.y - y) / 100));
        this.prev = Optional.of(new Point(x, y));
        this.camera.update();
        this.dragging = true;
        return true;
    }

    /** {@inheritdoc} */
    @Override
    public boolean zoom(float amount) {
        if (this.menu.scrolled(0, amount)) {
            return true;
        }
        final float diff = Math.max(GameViewController.MIN_ZOOM,
                Math.min(GameViewController.MAX_ZOOM, this.currentZoom + amount)) - this.currentZoom;
        this.currentZoom += diff;
        return diff == 0 ? false : super.zoom(amount);
    }

    /** {@inheritdoc} */
    @Override
    public boolean mouseMoved​(int x, int y) {
        // Cast out a ray from the mouseover point and find its point along the Y = 0
        // plane. Then find which hexagon that point falls in on the world grid.
        Ray ray = this.camera.getPickRay(x, y);
        float distance = (Hexagons.HEIGHT - ray.origin.y) / ray.direction.y;
        Vector3 endpoint = ray.getEndPoint(new Vector3(), distance);
        int minZ = (int) Math.floor(endpoint.x / (Hexagons.DEPTH - Hexagons.DEPTH_DIFF));
        float lowestDist2 = Integer.MAX_VALUE;
        Point closestPoint = null;
        for (int a = 0; a < 2; a++) {
            int minX = (int) Math.floor((-endpoint.z / Hexagons.WIDTH) - (minZ % 2 == 0 ? 0 : 0.5));
            for (int b = 0; b < 2; b++) {
                float dist = Coords.grid.vector(minX + b, minZ + a).dst2(endpoint);
                if (dist < lowestDist2) {
                    lowestDist2 = dist;
                    closestPoint = new Point(minX + b, minZ + a);
                }
            }
        }
        this.view.setHoveredTile(closestPoint);
        return true;
    }
}
