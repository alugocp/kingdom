package net.lugocorp.kingdom;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import java.util.Optional;
import net.lugocorp.kingdom.math.Point;

class GameCameraController extends CameraInputController {
    private Optional<Point> prev = Optional.empty();

    GameCameraController(Camera camera) {
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
        this.camera.translate((float) (p.y - y) / 100, 0, (float) (x - p.x) / 100);
        this.prev = Optional.of(new Point(x, y));
        this.camera.update();
        return true;
    }
}
