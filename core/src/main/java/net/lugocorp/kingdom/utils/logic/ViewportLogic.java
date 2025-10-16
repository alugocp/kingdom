package net.lugocorp.kingdom.utils.logic;
import net.lugocorp.kingdom.utils.math.Coords;
import net.lugocorp.kingdom.utils.math.Point;
import com.badlogic.gdx.utils.viewport.Viewport;

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
    public static Point project(int x, int y) {
        final float scaleX = ViewportLogic.viewport.getScreenWidth() / (float) Coords.SIZE.x;
        final float scaleY = ViewportLogic.viewport.getScreenHeight() / (float) Coords.SIZE.y;
        return new Point((int) (x * scaleX) + ViewportLogic.viewport.getScreenX(),
                (int) (y * scaleY) + ViewportLogic.viewport.getScreenY());
    }

    /**
     * Returns the given screen coordinate in "world units" (a LibGDX term)
     */
    public static Point unproject(int x, int y) {
        final float scaleX = Coords.SIZE.x / (float) ViewportLogic.viewport.getScreenWidth();
        final float scaleY = Coords.SIZE.y / (float) ViewportLogic.viewport.getScreenHeight();
        return new Point((int) ((x - ViewportLogic.viewport.getScreenX()) * scaleX),
                (int) ((y - ViewportLogic.viewport.getScreenY()) * scaleY));
    }
}
