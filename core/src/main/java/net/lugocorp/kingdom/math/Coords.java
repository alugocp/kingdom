package net.lugocorp.kingdom.math;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;

/**
 * Conversion logic between coordinate systems
 */
public class Coords {
    public static ScreenCoords screen = new Coords.ScreenCoords();
    public static GridCoords grid = new Coords.GridCoords();
    public static RawCoords raw = new Coords.RawCoords();

    /**
     * This nested class handles packaging raw numbers into a libGDX Vector3
     */
    public static class RawCoords {
        /**
         * Converts some number values into a 3D vector that libGDX will understand
         */
        public Vector3 vector(float x, float y, float z) {
            return new Vector3(z, y, x);
        }
    }

    /**
     * This nested class handles converting game coordinates into libGDX coordinates
     */
    public static class GridCoords {
        /**
         * Converts a 2D coordinate to a 3D vector that libGDX will understand
         */
        public Vector3 vector(int x, int z) {
            final float x_diff = (z % 2 == 0) ? 0f : 0.5f;
            return new Vector3(z * (Hexagons.DEPTH - Hexagons.DEPTH_DIFF), 0f, -(x + x_diff) * Hexagons.WIDTH);
        }
    }

    /**
     * This nested class handles converting screen coordinates to a more intuitive
     * coordinate space
     */
    public static class ScreenCoords {
        /**
         * Flips a Rect from top-left origin space into bottom-left origin space
         */
        public Rect flip(int x, int y, int w, int h) {
            return new Rect(x, Gdx.graphics.getHeight() - y - h, w, h);
        }
    }
}
