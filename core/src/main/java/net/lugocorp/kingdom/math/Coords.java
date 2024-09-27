package net.lugocorp.kingdom.math;
import com.badlogic.gdx.math.Vector3;

/**
 * Conversion logic between coordinate systems
 */
public class Coords {
    /**
     * Calls into the other getVector() method
     */
    public static Vector3 getVector(Point p) {
        return Coords.getVector((float) p.x, (float) p.y);
    }

    /**
     * Converts a 2D coordinate to a 3D vector that libGDX will understand
     */
    public static Vector3 getVector(float x, float y) {
        return new Vector3(y, 0, x);
    }
}
