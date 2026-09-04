package net.lugocorp.kingdom.ai.analysis;
import net.lugocorp.kingdom.game.model.Tile;
import net.lugocorp.kingdom.math.Hexagons;
import net.lugocorp.kingdom.math.Point;
import net.lugocorp.kingdom.ui.views.GameView;
import java.util.Set;
import java.util.function.Function;

/**
 * Contains logic for the AI to analyze Tiles
 */
public class TileAnalysis {

    /**
     * Counts the number of Tiles in the given radius which meet the given criteria
     */
    public static int nearby(GameView view, Point center, int r, Function<Point, Boolean> criteria) {
        final Set<Point> tiles = r > 1 ? Hexagons.getNeighbors(center, r) : Hexagons.getAdjacents(center);
        int total = 0;
        for (Point p : tiles) {
            if (view.game.world.isInBounds(p) && criteria.apply(p)) {
                total++;
            }
        }
        return total;
    }

    /**
     * Calls into nearby() but with a Tile function instead of Point
     */
    public static int nearbyTiles(GameView view, Point center, int r, Function<Tile, Boolean> criteria) {
        return TileAnalysis.nearby(view, center, r, (Point p) -> criteria.apply(view.game.world.getTile(p).get()));
    }
}
