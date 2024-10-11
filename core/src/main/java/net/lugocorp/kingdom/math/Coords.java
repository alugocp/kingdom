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
         * Converts a 2D grid coordinate to a 3D vector that libGDX will understand
         */
        public Vector3 vector(int x, int z) {
            return new Vector3(z * (Hexagons.DEPTH - Hexagons.DEPTH_DIFF), 0f,
                    -(x + ((z % 2 == 0) ? 0f : 0.5f)) * Hexagons.WIDTH);
        }

        /**
         * Converts a 2D grid coordinate to its actual 2D coordinate
         */
        public float[] coordinates(int x, int y) {
            return new float[]{(x + ((y % 2 == 0) ? 0f : 0.5f)) * Hexagons.WIDTH,
                    y * (Hexagons.DEPTH - Hexagons.DEPTH_DIFF)};
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

        /**
         * Calls the other flip method but with a Rect input
         */
        public Rect flip(Rect r) {
            return this.flip(r.x, r.y, r.w, r.h);
        }
    }
}
