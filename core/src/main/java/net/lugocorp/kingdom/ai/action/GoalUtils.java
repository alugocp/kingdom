package net.lugocorp.kingdom.ai.action;
import net.lugocorp.kingdom.ai.memory.MemoryCell;
import net.lugocorp.kingdom.ai.memory.MemoryMap;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.math.Hexagons;
import net.lugocorp.kingdom.math.Point;
import net.lugocorp.kingdom.pathfinding.Pathfinder;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.Lambda;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

/**
 * Contains utility methods for Goal logic
 */
public class GoalUtils {

    /**
     * Returns a Set of Points that the given Unit can even move to within the given
     * radius
     */
    public static Set<Point> getMoveTargets(GameView view, Unit u, int radius) {
        final Pathfinder pathfinder = new Pathfinder(u);
        final Set<Point> initial = Hexagons.getNeighbors(u.getPoint(), 4);
        return Lambda.filter((Point p) -> pathfinder.getPath(view, p).size() > 0, initial);
    }

    /**
     * Returns a Set of Points that the CompPlayer has seen before and fit some
     * criteria
     */
    public static Set<Point> getRelevantKnownPoints(MemoryMap map, Function<MemoryCell, Boolean> criteria) {
        // TODO write an optimized version that only searches within some radius
        final Set<Point> points = new HashSet<>();
        for (Point p : map.getKnownCells()) {
            if (criteria.apply(map.getCell(p).get())) {
                points.add(p);
            }
        }
        return points;
    }
}
