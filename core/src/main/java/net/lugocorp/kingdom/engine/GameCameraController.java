package net.lugocorp.kingdom.engine;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import java.util.Optional;
import net.lugocorp.kingdom.math.Coords;
import net.lugocorp.kingdom.math.Point;
import net.lugocorp.kingdom.math.Hexagons;

public class GameCameraController extends CameraInputController {
    private static final float MAX_ZOOM = 3.0f;
    private static final float MIN_ZOOM = -2.0f;
    private Optional<Point> prev = Optional.empty();
    private float currentZoom = 0.0f;

    public GameCameraController(Camera camera) {
        super(camera);
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
        /*Vector3 v = this.camera.unproject(new Vector3((float)x, (float)y, 1f));
        v.x /= (Hexagons.DEPTH - Hexagons.DEPTH_DIFF);
        v.y /= Hexagons.HEIGHT / 2f;
        System.out.println(v);*/
        return true;
    }
}
