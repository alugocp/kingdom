package net.lugocorp.kingdom.engine;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import java.util.Optional;
import java.util.function.Function;
import net.lugocorp.kingdom.math.Coords;
import net.lugocorp.kingdom.math.Hexagons;
import net.lugocorp.kingdom.math.Point;

public class GameCameraController extends CameraInputController {
    private static final float MAX_ZOOM = 3.0f;
    private static final float MIN_ZOOM = -2.0f;
    private final Function<Point, Void> setHoveredTile;
    private Optional<Point> prev = Optional.empty();
    private float currentZoom = 0.0f;

    public GameCameraController(Camera camera, Function<Point, Void> setHoveredTile) {
        super(camera);
        this.setHoveredTile = setHoveredTile;
    }

    @Override
    public boolean touchDown​(int x, int y, int pointer, int button) {
        this.prev = Optional.of(new Point(x, y));
        return true;
    }

    @Override
    public boolean touchUp(int x, int y, int pointer, int button) {
        this.prev = Optional.empty();
        return true;
    }

    @Override
    public boolean touchDragged​(int x, int y, int pointer) {
        if (!this.prev.isPresent()) {
            return false;
        }
        Point p = this.prev.get();
        this.camera.translate(Coords.raw.vector((float) (x - p.x) / 100, 0f, (float) (p.y - y) / 100));
        this.prev = Optional.of(new Point(x, y));
        this.camera.update();
        return true;
    }

    @Override
    public boolean zoom(float amount) {
        final float diff = Math.max(GameCameraController.MIN_ZOOM,
                Math.min(GameCameraController.MAX_ZOOM, this.currentZoom + amount)) - this.currentZoom;
        this.currentZoom += diff;
        return diff == 0 ? false : super.zoom(amount);
    }

    @Override
    public boolean mouseMoved​(int x, int y) {
        // Cast out a ray from the mouseover point and find its point along the Y = 0
        // plane.
        // Then find which hexagon that point falls in on the world grid.
        Ray ray = this.camera.getPickRay(x, y);
        float distance = (Hexagons.HEIGHT - ray.origin.y) / ray.direction.y;
        Vector3 endpoint = ray.getEndPoint(new Vector3(), distance);
        int minZ = (int) Math.floor(endpoint.x / (Hexagons.DEPTH - Hexagons.DEPTH_DIFF));
        float lowestDist2 = Integer.MAX_VALUE;
        Point closestPoint = null;
        for (int a = 0; a < 2; a++) {
            int minX = (int) Math.floor((-endpoint.z / Hexagons.WIDTH) - (minZ % 2 == 0 ? 0 : 0.5));
            for (int b = 0; b < 2; b++) {
                float dist = Coords.grid.vector(minX + b, 0, minZ + a).dst2(endpoint);
                if (dist < lowestDist2) {
                    lowestDist2 = dist;
                    closestPoint = new Point(minX + b, minZ + a);
                }
            }
        }
        this.setHoveredTile.apply(closestPoint);
        return true;
    }
}
