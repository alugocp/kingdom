package net.lugocorp.kingdom.engine;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import java.util.Optional;
import net.lugocorp.kingdom.math.Coords;
import net.lugocorp.kingdom.math.Point;

public class GameCameraController extends CameraInputController {
    private Optional<Point> prev = Optional.empty();

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
}
