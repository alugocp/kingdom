package net.lugocorp.kingdom.utils.math;
import com.badlogic.gdx.math.Vector3;

/**
 * Conversion logic between coordinate systems
 */
public class Coords {
    public static final Point SIZE = new Point(1600, 960);
    public static ScreenCoords screen = new ScreenCoords();
    public static GridCoords grid = new GridCoords();
    public static RawCoords raw = new RawCoords();

    /**
     * This nested class handles packaging raw numbers into a libGDX Vector3
     */
    public static class RawCoords {
        /**
         * Converts some number values into a 3D vector that libGDX will understand
         */
        public Vector3 vector(float x, float y, float z) {
            return new Vector3(x, y, z);
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
            return new Vector3((x + ((z % 2 == 0) ? 0f : 0.5f)) * Hexagons.WIDTH, 0f,
                    z * (Hexagons.DEPTH - Hexagons.DEPTH_DIFF));
        }

        /**
         * Converts a 2D grid coordinate to its actual 2D coordinate
         */
        public float[] coordinates(int x, int y) {
            return new float[]{(x + ((y % 2 == 0) ? 0f : 0.5f)) * Hexagons.WIDTH,
                    y * (Hexagons.DEPTH - Hexagons.DEPTH_DIFF)};
        }

        // Returns the difference (in actual 2D coordinates) between the two given grid
        // Points
        public float[] difference(Point p1, Point p2) {
            final Vector3 v1 = Coords.grid.vector(p1.x, p1.y);
            final Vector3 v2 = Coords.grid.vector(p2.x, p2.y);
            return new float[]{v2.x - v1.x, v2.z - v1.z};
        }

        /**
         * Returns the angle pointing from the first grid Point to the second grid Point
         */
        public float angle(Point p1, Point p2) {
            float[] diff = this.difference(p1, p2);
            return (float) Math.atan2(diff[1], diff[0]);
        }

        /**
         * Returns the euclidean distance between the two grid Points
         */
        public float distance(Point p1, Point p2) {
            return (float) Math.sqrt(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2));
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
            return new Rect(x, Coords.SIZE.y - y - h, w, h);
        }

        /**
         * Calls the other flip method but with a Rect input
         */
        public Rect flip(Rect r) {
            return this.flip(r.x, r.y, r.w, r.h);
        }
    }
}
