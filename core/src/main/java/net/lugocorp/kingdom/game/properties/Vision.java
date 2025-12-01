package net.lugocorp.kingdom.game.properties;
import net.lugocorp.kingdom.builtin.Events;
import net.lugocorp.kingdom.game.events.EventReceiver;
import net.lugocorp.kingdom.game.model.Tile;
import net.lugocorp.kingdom.game.player.Player;
import net.lugocorp.kingdom.game.world.World;
import net.lugocorp.kingdom.math.HexSide;
import net.lugocorp.kingdom.math.Hexagons;
import net.lugocorp.kingdom.math.Point;
import net.lugocorp.kingdom.ui.views.GameView;
import java.util.HashSet;
import java.util.Set;

/**
 * This class tracks a Unit/Building's vision area
 */
public class Vision {
    private final Set<Point> vision = new HashSet<>();

    /**
     * Returns the associated Unit's vision radius
     */
    public int get(GameView view, Player player, EventReceiver receiver) {
        final Events.GetVisionEvent event = new Events.GetVisionEvent(player);
        final boolean isNight = view.game.mechanics.dayNight.isNight();
        receiver.handleEvent(view, event);
        return event.cumulative(isNight);
    }

    /**
     * Changes the focal point of the associated Unit/Building
     */
    public void translate(Player player, World world, HexSide direction) {
        for (Point p : this.vision) {
            final Point d = Hexagons.getDirectionTranslation(p, direction);
            world.getTile(p.x, p.y).ifPresent((Tile t) -> player.decrementVision(t));
            p.set(p.x + d.x, p.y + d.y);
            world.getTile(p.x, p.y).ifPresent((Tile t) -> player.incrementVision(t));
        }
    }

    /**
     * Changes how far the associated Unit/Building can see
     */
    public void set(GameView view, Player player, EventReceiver receiver, Point center) {
        this.remove(player, view.game.world);
        view.game.world.getTile(center).ifPresent((Tile t) -> player.incrementVision(t));
        this.vision.add(center);
        for (Point p : Hexagons.getNeighbors(center, this.get(view, player, receiver))) {
            view.game.world.getTile(p.x, p.y).ifPresent((Tile t) -> player.incrementVision(t));
            this.vision.add(p);
        }
    }

    /**
     * Removes all Points from the vision set
     */
    public void remove(Player player, World world) {
        for (Point p : this.vision) {
            world.getTile(p.x, p.y).ifPresent((Tile t) -> player.decrementVision(t));
        }
        this.vision.clear();
    }
}
