package net.lugocorp.kingdom.game.model;
import net.lugocorp.kingdom.game.core.Events;
import net.lugocorp.kingdom.game.events.Event;
import net.lugocorp.kingdom.game.player.Player;
import net.lugocorp.kingdom.game.properties.EntityType;
import net.lugocorp.kingdom.game.world.World;
import net.lugocorp.kingdom.ui.menu.HeaderNode;
import net.lugocorp.kingdom.ui.menu.ListNode;
import net.lugocorp.kingdom.ui.menu.MenuNode;
import net.lugocorp.kingdom.ui.menu.TextNode;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.code.SideEffect;
import net.lugocorp.kingdom.utils.math.Hexagons;
import net.lugocorp.kingdom.utils.math.Point;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

/**
 * Represents a local spirit that Players can compete for favor with
 */
public class Patron extends Building {
    private static final int MIN_FAVOR = 5;
    private final Map<Player, Integer> favor = new HashMap<>();
    private final Set<Point> domain = new HashSet<>();
    public Function<Unit, Boolean> isPreferredUnitType = (Unit u) -> false;
    public String preference = "";

    Patron(String name, int x, int y) {
        super(name, x, y, null);
        this.combat.health.invulnerable();
        super.setMinimapColor(0x000000);
    }

    /**
     * This should only be used in conjunction with Kryo rehydration
     */
    public Patron() {
        super(null, 0, 0, null);
    }

    /** {@inheritdoc} */
    @Override
    public EntityType getEntityType() {
        return EntityType.PATRON;
    }

    /**
     * Checks how much favor this Patron has with each Player. Favor is determined
     * by the Units in a Patron's domain.
     */
    public void recalculateFavor(GameView view) {
        this.favor.clear();
        for (Point p : this.domain) {
            final Optional<Unit> unit = view.game.world.getTile(p).flatMap((Tile t) -> t.unit);
            if (!unit.flatMap((Unit u) -> u.getLeader()).isPresent()) {
                continue;
            }

            // Calculate favor from the given Unit
            final Unit u = unit.get();
            final Events.GenerateFavorEvent event = new Events.GenerateFavorEvent(this,
                    this.isPreferredUnitType.apply(u) ? Patron.MIN_FAVOR * 2 : Patron.MIN_FAVOR);
            u.handleEvent(view, event);
            if (event.favor <= 0) {
                continue;
            }

            // Add favor to the ledger
            final Player leader = u.getLeader().get();
            if (this.favor.containsKey(leader)) {
                this.favor.put(leader, this.favor.get(leader) + event.favor);
            } else {
                this.favor.put(leader, event.favor);
            }
        }
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
            this.domain.add(p);
        }
    }

    /**
     * Returns true if this Patron's domain includes the given Point
     */
    public boolean domainContains(Point p) {
        return this.domain.contains(p);
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
    public SideEffect handleEventWithoutSignalBooster(GameView view, Event e) {
        return view.game.events.patron.handle(view, this, e);
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
        ListNode node = new ListNode().add(new HeaderNode(view.av, this.name)).add(new TextNode(view.av, this.desc))
                .add(new TextNode(view.av, String.format("Preferred units: %s", this.preference)));
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
