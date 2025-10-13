package net.lugocorp.kingdom.game.unit;
import net.lugocorp.kingdom.game.Game;
import net.lugocorp.kingdom.game.model.Tile;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.math.Hexagons;
import net.lugocorp.kingdom.utils.math.Point;
import java.util.HashSet;
import java.util.Set;

/**
 * This utility class helps determine what Entities a Unit is adjacent to
 */
public class Adjacency {
    private final Unit unit;

    public Adjacency(Unit unit) {
        this.unit = unit;
    }

    /**
     * Returns true if this Unit is adjacent to a vault Building
     */
    public boolean vault(Game game) {
        if (!this.unit.getLeader().isPresent()) {
            return false;
        }
        final Set<Point> vaults = game.getVaultBuildings(this.unit.getLeader().get());
        for (Point p : vaults) {
            if (Hexagons.areNeighbors(p, this.unit.getPoint())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true if the Unit is adjacent to a Unit that it can give food to
     */
    public Set<Point> unitsToFeed(GameView view) {
        final Set<Point> units = new HashSet<>();
        for (Point p : Hexagons.getAdjacents(this.unit.getPoint())) {
            view.game.world.getTile(p).flatMap((Tile t) -> t.unit).ifPresent((Unit u) -> {
                if ((u.leadership.sameLeader(this.unit) || u.leadership.isFreeRadical()) && !u.haul.isFull()
                        && this.unit.haul.getEdibleItems(view, u).size() > 0) {
                    units.add(u.getPoint());
                }
            });
        }
        return units;
    }
}
