package net.lugocorp.kingdom.game.core;
import net.lugocorp.kingdom.game.combat.Damage;
import net.lugocorp.kingdom.game.combat.HitPoints;
import net.lugocorp.kingdom.game.model.Tile;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.math.Hexagons;
import net.lugocorp.kingdom.utils.math.Point;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This class contains utility functions for writing new Ability effects
 */
public class AbilityLogic {

    /**
     * Convenience wrapper for an active Ability that implements an attack
     */
    public static void attack(GameView view, Unit attacker, Damage dmg) {
        Map<Point, HitPoints> targets = new HashMap<>();
        Set<Point> points = new HashSet<>();

        // Grab every possible attack target within range
        int range = attacker.getAttackRange(view);
        Set<Point> unfiltered = Hexagons.getNeighbors(attacker.getPoint(), range);
        for (Point p : unfiltered) {
            Tile t = view.game.world.getTile(p).get();
            if (t.unit.isPresent()) {
                targets.put(p, t.unit.get().health);
                points.add(p);
            } else if (t.building.isPresent()) {
                targets.put(p, t.building.get().health);
                points.add(p);
            }
        }

        // Have the human Player select which target to attack
        view.selectTiles(points, "No attack targets are in range",
                (Point p) -> attacker.health.attack(view, targets.get(p), dmg));
    }
}
