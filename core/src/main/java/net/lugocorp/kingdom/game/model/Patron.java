package net.lugocorp.kingdom.game.model;
import net.lugocorp.kingdom.game.combat.FavorPoints;
import net.lugocorp.kingdom.game.core.Events;
import net.lugocorp.kingdom.game.events.Event;
import net.lugocorp.kingdom.ui.menu.HeaderNode;
import net.lugocorp.kingdom.ui.menu.ListNode;
import net.lugocorp.kingdom.ui.menu.MenuNode;
import net.lugocorp.kingdom.ui.menu.TextNode;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.math.Point;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Represents a local spirit that Players can compete for favor with
 */
public class Patron extends Building {
    private static final int MAX_FAVOR = 100;
    private Map<Player, Integer> favor = new HashMap<>();
    private Set<Point> domain = new HashSet<>();
    private int threshold = 10;

    Patron(String name, int x, int y) {
        super(name, x, y);
        this.setHealth(new FavorPoints(this));
    }

    /**
     * Sets this Patron's favor threshold value
     */
    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    /**
     * Changes the given Player's favor associated with this Patron by the amount
     * specified
     */
    public void addFavor(Player player, int points) {
        // TODO optimize Patron code by checking for favorite player change here, then
        // trigger an event to add/remove listeners (no signal boosters if no favorite
        // player)
        if (!this.favor.containsKey(player)) {
            this.favor.put(player, 0);
        }
        this.favor.put(player, Math.max(0, Math.min(Patron.MAX_FAVOR, this.favor.get(player) + points)));
    }

    /**
     * Returns this Patron's current favorite Player (if any)
     */
    public Optional<Player> getFavoritePlayer() {
        Optional<Player> favorite = Optional.empty();
        for (Player p : this.favor.keySet()) {
            int favor = this.favor.get(p);
            if (favor >= this.threshold && (!favorite.isPresent() || favor > this.favor.get(favorite.get()))) {
                favorite = Optional.of(p);
            }
        }
        return favorite;
    }

    /**
     * Returns true if the given Point is in this Patron's domain
     */
    public boolean isInDomain(Point p) {
        return this.domain.contains(p);
    }

    /**
     * Adds the given Point to this Patron's domain
     */
    public void addToDomain(Point p) {
        this.domain.add(p);
    }

    /** {@inheritdoc} */
    @Override
    public void setObstacle(boolean obstacle) {
        throw new RuntimeException("Cannot change a Patron's obstacle status");
    }

    /** {@inheritdoc} */
    @Override
    public boolean getObstacle() {
        return true;
    }

    /**
     * Spawns this loaded object into the World
     */
    public void spawn(GameView view) {
        view.game.world.getTile(this.x, this.y).ifPresent((Tile t) -> {
            t.building = Optional.of(this);
        });
        this.handleEvent(view, new Events.SpawnEvent<Patron>(this));
    }

    /** {@inheritdoc} */
    @Override
    public void handleEventWithoutSignalBooster(GameView view, Event e) {
        view.game.events.patron.handle(view, this, e);
    }

    /** {@inheritdoc} */
    @Override
    public void deactivate(GameView view) {
        this.deactivateDefault(view);
    }

    /** {@inheritdoc} */
    @Override
    public MenuNode getMenuContent(GameView view, Optional<Point> p) {
        Optional<Player> favorite = this.getFavoritePlayer();
        ListNode node = new ListNode().add(new HeaderNode(view.graphics, this.name))
                .add(new TextNode(view.graphics, this.desc))
                .add(new TextNode(view.graphics, String.format("Threshold: %d favor", this.threshold)));
        if (this.favor.size() > 0) {
            for (Player k : this.favor.keySet()) {
                String label = String.format("%s: %d", k.name, this.favor.get(k));
                if (favorite.map((Player f) -> k == f).orElse(false)) {
                    label += " (FAVORITE)";
                }
                node.add(new TextNode(view.graphics, label));
            }
        } else {
            node.add(new TextNode(view.graphics, "No players have gained favor with this patron yet"));
        }
        return node;
    }
}
