package net.lugocorp.kingdom.game.model;
import net.lugocorp.kingdom.game.properties.EntityType;
import net.lugocorp.kingdom.game.world.World;
import net.lugocorp.kingdom.math.Hexagons;
import net.lugocorp.kingdom.math.Point;
import net.lugocorp.kingdom.ui.views.GameView;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Towers control influence over the map
 */
public class Tower extends Building {
    private final Set<Point> domain = new HashSet<>();

    Tower(int x, int y, Supplier<Tile> getTile) {
        super("Tower", x, y, getTile);
    }

    /**
     * This should only be used in conjunction with Kryo rehydration
     */
    public Tower() {
        super(null, 0, 0, null);
    }

    /** {@inheritdoc} */
    @Override
    public EntityType getEntityType() {
        return EntityType.TOWER;
    }

    /**
     * Sets up this Patron's domain
     */
    private void initializeDomain(World world) {
        Set<Point> domain = Hexagons.getNeighbors(this.getPoint(), 2);
        for (Point p : domain) {
            world.getTile(p).ifPresent((Tile t) -> t.addDomainBorder(
                    Hexagons.getBorderInteger(p, (Point p1) -> !(domain.contains(p1) || p1.equals(this.getPoint())))));
            this.domain.add(p);
        }
    }

    /**
     * Returns true if this Patron's domain includes the given Point
     */
    public boolean domainContains(Point p) {
        return this.domain.contains(p);
    }

    /**
     * Returns this Patron's domain
     */
    public Set<Point> getDomain() {
        return this.domain;
    }

    /** {@inheritdoc} */
    @Override
    public void spawn(GameView view) {
        super.spawn(view);
        this.initializeDomain(view.game.world);
    }
}
