package net.lugocorp.kingdom.engine.projection;
import net.lugocorp.kingdom.math.Coords;
import net.lugocorp.kingdom.math.Point;
import com.badlogic.gdx.utils.viewport.Viewport;
import java.util.Optional;

/**
 * This class handles the logic for switching between Viewports and translating
 * coordinates
 */
public class ViewportLogic {
    private static Viewport viewport = null;

    /**
     * Returns the current Viewport
     */
    public static Viewport getViewport() {
        return ViewportLogic.viewport;
    }

    /**
     * Sets the current Viewport
     */
    public static void setViewport(Viewport viewport) {
        ViewportLogic.viewport = viewport;
    }

    /**
     * Returns the given "world units" (a LibGDX term) in screen coordinates
     */
    public static int[] project(int x, int y) {
        final float scaleX = ViewportLogic.viewport.getScreenWidth() / (float) Coords.SIZE.x;
        final float scaleY = ViewportLogic.viewport.getScreenHeight() / (float) Coords.SIZE.y;
        return new int[]{(int) ((x * scaleX) + ViewportLogic.viewport.getScreenX()),
                (int) ((y * scaleY) + ViewportLogic.viewport.getScreenY())};
    }

    /**
     * Returns the given screen coordinate in "world units" (a LibGDX term)
     */
    public static Optional<Point> unproject(int x, int y) {
        final float scaleX = Coords.SIZE.x / (float) ViewportLogic.viewport.getScreenWidth();
        final float scaleY = Coords.SIZE.y / (float) ViewportLogic.viewport.getScreenHeight();
        final float sx = (x - ViewportLogic.viewport.getScreenX()) * scaleX;
        final float sy = (y - ViewportLogic.viewport.getScreenY()) * scaleY;
        if (sx < 0 || sy < 0 || sx > Coords.SIZE.x || sy > Coords.SIZE.y) {
            return Optional.empty();
        }
        return Optional.of(new Point((int) sx, (int) sy));
    }
}
