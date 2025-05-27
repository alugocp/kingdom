package net.lugocorp.kingdom.game.mechanics;
import net.lugocorp.kingdom.utils.math.Hexagons;
import net.lugocorp.kingdom.utils.math.Point;
import net.lugocorp.kingdom.game.world.World;
import net.lugocorp.kingdom.game.model.Tile;
import java.util.HashSet;
import java.util.Set;

/**
 * This class tracks a Unit/Building's visibility area
 */
public class Visibility {
    private final Set<Point> vision = new HashSet<>();

    /**
     * Changes the focal point of the associated Unit/Building
     */
    public void translate(World world, int dx, int dy) {
        for (Point p : this.vision) {
            world.getTile(p.x, p.y).ifPresent((Tile t) -> t.decrementVisibility());
            p.set(p.x + dx, p.y + dy);
            world.getTile(p.x, p.y).ifPresent((Tile t) -> t.incrementVisibility());
        }
    }

    /**
     * Changes how far the associated Unit/Building can see
     */
    public void setVisibleRadius(World world, Point center, int radius) {
        this.removeVision(world);
        world.getTile(center).ifPresent((Tile t) -> t.incrementVisibility());
        this.vision.add(center);
        for (Point p: Hexagons.getNeighbors(center, radius)) {
            world.getTile(p.x, p.y).ifPresent((Tile t) -> t.incrementVisibility());
            this.vision.add(p);
        }
    }

    /**
     * Removes all Points from the vision set
     */
    public void removeVision(World world) {
        for (Point p: this.vision) {
            world.getTile(p.x, p.y).ifPresent((Tile t) -> t.decrementVisibility());
        }
        this.vision.clear();
    }
}