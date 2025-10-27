package net.lugocorp.kingdom.engine.projection;
import net.lugocorp.kingdom.math.Coords;
import net.lugocorp.kingdom.math.Hexagons;
import net.lugocorp.kingdom.math.Point;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;

/**
 * This class contains logic for converting between world and screen coordinates
 */
public class CameraLogic {

    /**
     * Returns the screen coordinates for the given Point in the World with some
     * offset vector
     */
    public static float[] getScreenPointFromTileOffset(Point p, Vector3 offset) {
        final Vector3 v = ViewportLogic.getViewport().project(Coords.grid.vector(p.x, p.y).add(offset));
        Point unprojected = ViewportLogic.unproject((int) v.x, (int) v.y).orElse(new Point(-10, -10));
        return new float[]{unprojected.x, unprojected.y};
    }

    /**
     * Calculates the point on the surface of the World that corresponds to a point
     * on the viewing area (the plane where the mouse lives)
     */
    public static Vector3 getScreenPointOnSurface(int x, int y) {
        final Ray ray = ViewportLogic.getViewport().getPickRay(x, y);
        final float distance = (Hexagons.HEIGHT - ray.origin.y) / ray.direction.y;
        return ray.getEndPoint(new Vector3(), distance);
    }

    /**
     * Calls into getScreenPointOnSurface() from a Point
     */
    public static Vector3 getScreenPointOnSurface(int[] point) {
        return CameraLogic.getScreenPointOnSurface(point[0], point[1]);
    }

    /**
     * Returns the Tile coordinate in the World that lives under the given Point on
     * the screen
     */
    public static Point getCoordUnderScreenPoint(int x, int y) {
        // Cast out a ray from the mouseover point and find its point along the Y = 0
        // plane. Then find which hexagon that point falls in on the world grid.
        final Vector3 endpoint = CameraLogic.getScreenPointOnSurface(x, y);
        final int minZ = (int) Math.floor(endpoint.z / (Hexagons.DEPTH - Hexagons.DEPTH_DIFF));
        float lowestDist2 = Integer.MAX_VALUE;
        Point closestPoint = null;
        for (int a = 0; a < 2; a++) {
            final int minX = (int) Math.floor((endpoint.x / Hexagons.WIDTH) - (minZ % 2 == 0 ? 0 : 0.5));
            for (int b = 0; b < 2; b++) {
                final float dist = Coords.grid.vector(minX + b, minZ + a).dst2(endpoint);
                if (dist < lowestDist2) {
                    lowestDist2 = dist;
                    closestPoint = new Point(minX + b, minZ + a);
                }
            }
        }
        return closestPoint;
    }
}
