package net.lugocorp.kingdom.game.model;
import net.lugocorp.kingdom.game.core.Events;
import net.lugocorp.kingdom.game.events.Event;
import net.lugocorp.kingdom.game.world.World;
import net.lugocorp.kingdom.ui.menu.HeaderNode;
import net.lugocorp.kingdom.ui.menu.ListNode;
import net.lugocorp.kingdom.ui.menu.MenuNode;
import net.lugocorp.kingdom.ui.menu.TextNode;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.math.Hexagons;
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
        this.combat.health.invulnerable();
        super.setMinimapColor(0x000000);
    }

    /**
     * This should only be used in conjunction with Kryo rehydration
     */
    public Patron() {
        super(null, 0, 0);
    }

    /**
     * Checks how much favor this Patron has with each Player. Favor is determined
     * by the Units in a Patron's domain.
     */
    public void recalculateFavor() {
        this.favor.clear();
        // TODO implement this
    }

    /**
     * Returns this Patron's current favorite Player (if any)
     */
    public Optional<Player> getFavoritePlayer() {
        Optional<Player> favorite = Optional.empty();
        for (Player p : this.favor.keySet()) {
            int favor = this.favor.get(p);
            if (!favorite.isPresent() || favor > this.favor.get(favorite.get())) {
                favorite = Optional.of(p);
            }
        }
        return favorite;
    }

    /**
     * Sets up this Patron's domain
     */
    private void initializeDomain(World world) {
        Set<Point> domain = Hexagons.getNeighbors(this.getPoint(), 2);
        for (Point p : domain) {
            world.getTile(p).ifPresent((Tile t) -> t.addDomainBorder(
                    Hexagons.getBorderInteger(p, (Point p1) -> !(domain.contains(p1) || p1.equals(this.getPoint())))));
        }
        this.domain = domain;
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

    /** {@inheritdoc} */
    public void setMinimapColor(int hexcode) {
        throw new RuntimeException("Cannot change a Patron's minimap color");
    }

    /** {@inheritdoc} */
    @Override
    public void spawn(GameView view) {
        view.game.world.getTile(this.x, this.y).ifPresent((Tile t) -> {
            t.building = Optional.of(this);
            t.setGlyph(Optional.empty());
        });
        this.initializeDomain(view.game.world);
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
        throw new RuntimeException("Should never call decativate() on a Patron");
    }

    /** {@inheritdoc} */
    @Override
    public MenuNode getMenuContent(GameView view, Optional<Point> p) {
        Optional<Player> favorite = this.getFavoritePlayer();
        ListNode node = new ListNode().add(new HeaderNode(view.av, this.name)).add(new TextNode(view.av, this.desc));
        if (this.favor.size() > 0) {
            for (Player k : this.favor.keySet()) {
                String label = String.format("%s: %d", k.name, this.favor.get(k));
                if (favorite.map((Player f) -> k == f).orElse(false)) {
                    label += " (FAVORITE)";
                }
                node.add(new TextNode(view.av, label));
            }
        } else {
            node.add(new TextNode(view.av, "No players are competing for this patron right now"));
        }
        return node;
    }
}
