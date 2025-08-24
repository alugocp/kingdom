package net.lugocorp.kingdom.game.mechanics;
import net.lugocorp.kingdom.game.core.Events;
import net.lugocorp.kingdom.game.events.EventReceiver;
import net.lugocorp.kingdom.game.model.Tile;
import net.lugocorp.kingdom.game.player.Player;
import net.lugocorp.kingdom.game.world.World;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.math.Hexagons;
import net.lugocorp.kingdom.utils.math.Point;
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
    public void translate(Player player, World world, int dx, int dy) {
        for (Point p : this.vision) {
            world.getTile(p.x, p.y).ifPresent((Tile t) -> player.decrementVisibility(t));
            p.set(p.x + dx, p.y + dy);
            world.getTile(p.x, p.y).ifPresent((Tile t) -> player.incrementVisibility(t));
        }
    }

    /**
     * Changes how far the associated Unit/Building can see
     */
    public void set(GameView view, Player player, EventReceiver receiver, Point center) {
        Events.GetVisibilityEvent event = new Events.GetVisibilityEvent();
        receiver.handleEvent(view, event);
        this.remove(player, view.game.world);
        view.game.world.getTile(center).ifPresent((Tile t) -> player.incrementVisibility(t));
        this.vision.add(center);
        boolean isNight = view.game.mechanics.dayNight.isNight();
        for (Point p : Hexagons.getNeighbors(center, event.cumulative(isNight))) {
            view.game.world.getTile(p.x, p.y).ifPresent((Tile t) -> player.incrementVisibility(t));
            this.vision.add(p);
        }
    }

    /**
     * Removes all Points from the vision set
     */
    public void remove(Player player, World world) {
        for (Point p : this.vision) {
            world.getTile(p.x, p.y).ifPresent((Tile t) -> player.decrementVisibility(t));
        }
        this.vision.clear();
    }
}
