package net.lugocorp.kingdom.game.properties;
import net.lugocorp.kingdom.game.model.Tile;
import net.lugocorp.kingdom.game.model.Tower;
import net.lugocorp.kingdom.game.world.World;
import net.lugocorp.kingdom.math.Hexagons;
import net.lugocorp.kingdom.math.Point;
import java.util.HashSet;
import java.util.Set;

/**
 * Contains a set of Points in the Game World that represents a contiguous area
 */
public class Domain {
    private final Set<Point> domain = new HashSet<>();

    /**
     * Sets the Points in this domain
     */
    public void init(World world, Tower tower, Set<Point> points) {
        if (this.domain.size() > 0) {
            throw new RuntimeException("Domain has already been initialized");
        }
        for (Point p : points) {
            world.getTile(p).ifPresent((Tile t) -> {
                t.addDomainBorder(Hexagons.getBorderInteger(p, (Point p1) -> !points.contains(p1)));
                t.setDomainCenter(tower);
            });
            this.domain.add(p);
        }
    }

    /**
     * Returns true if this domain includes the given Point
     */
    public boolean contains(Point p) {
        return this.domain.contains(p);
    }

    /**
     * Returns the set of Points within this Domain
     */
    public Set<Point> get() {
        return this.domain;
    }
}
