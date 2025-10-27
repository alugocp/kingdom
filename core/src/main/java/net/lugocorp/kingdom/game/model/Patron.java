package net.lugocorp.kingdom.game.model;
import net.lugocorp.kingdom.builtin.Events;
import net.lugocorp.kingdom.color.ColorScheme;
import net.lugocorp.kingdom.game.events.Event;
import net.lugocorp.kingdom.game.player.Player;
import net.lugocorp.kingdom.game.properties.EntityType;
import net.lugocorp.kingdom.game.world.World;
import net.lugocorp.kingdom.math.Hexagons;
import net.lugocorp.kingdom.math.Point;
import net.lugocorp.kingdom.menu.MenuNode;
import net.lugocorp.kingdom.menu.structure.ListNode;
import net.lugocorp.kingdom.menu.text.HeaderNode;
import net.lugocorp.kingdom.menu.text.HelperNode;
import net.lugocorp.kingdom.menu.text.SubheaderNode;
import net.lugocorp.kingdom.menu.text.TextNode;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.SideEffect;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Represents a local spirit that Players can compete for favor with
 */
public class Patron extends Building {
    private static final int MIN_FAVOR = 5;
    private final Map<Player, Integer> favor = new HashMap<>();
    private final Set<Point> domain = new HashSet<>();
    public Function<Unit, Boolean> isPreferredUnitType = (Unit u) -> false;
    private Optional<Player> favorite = Optional.empty();
    public String preference = "";

    Patron(String name, int x, int y, Supplier<Tile> getTile) {
        super(name, x, y, getTile);
        this.combat.health.invulnerable();
        super.setMinimapColor(ColorScheme.BLACK.hex);
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

    /** {@inheritdoc} */
    @Override
    public Optional<Player> getLeader() {
        return Optional.empty();
    }

    /** {@inheritdoc} */
    @Override
    public String getMenuTabLabel() {
        return "Patron";
    }

    /**
     * Checks how much favor this Patron has with each Player. Favor is determined
     * by the Units in a Patron's domain.
     */
    public void recalculateFavor(GameView view) {
        // Reset the values
        this.favor.clear();
        this.favorite = Optional.empty();

        // Count up favor from Units in this Patron's domain
        for (Point p : this.domain) {
            final Optional<Unit> unit = view.game.world.getTile(p).flatMap((Tile t) -> t.unit);
            if (!unit.flatMap((Unit u) -> u.getLeader()).isPresent()) {
                continue;
            }

            // Calculate favor from the given Unit
            final Unit u = unit.get();
            final Events.GenerateFavorEvent event = new Events.GenerateFavorEvent(this, u,
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

        // Allow for Player-level favor modifiers
        for (Player p : view.game.getAllPlayers()) {
            final Events.CalculateFavorEvent event = new Events.CalculateFavorEvent(this, p,
                    this.favor.getOrDefault(p, 0));
            this.handleEvent(view, event);
            this.favor.put(p, event.favor);
        }

        // The Unit with the most favor is the favorite
        for (Player p : this.favor.keySet()) {
            final int favor = this.favor.get(p);
            if (favor > this.favorite.map((Player f) -> this.favor.get(f)).orElse(0)) {
                this.favorite = Optional.of(p);
            }
        }
    }

    /**
     * Returns this Patron's current favorite Player (if any)
     */
    public Optional<Player> getFavoritePlayer() {
        return this.favorite;
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
        this.handleEvent(view, new Events.SpawnEvent<Patron>(this)).execute();
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
        ListNode node = new ListNode().add(new HeaderNode(view.av, this.name)).add(new TextNode(view.av, this.desc))
                .add(new TextNode(view.av, String.format("Preferred units: %s", this.preference)))
                .add(new HelperNode(view.av,
                        "Patrons are special buildings that cannot be traversed on. You can gain favor with a patron by moving your units within its domain. During each turn, a patron chooses the player with the most favor and gives them a powerful bonus for the rest of that turn."));
        if (this.favor.size() > 0) {
            for (Player k : this.favor.keySet()) {
                final String label = String.format("%s: %d", k.name, this.favor.get(k));
                final boolean fav = this.favorite.map((Player f) -> k == f).orElse(false);
                if (fav) {
                    node.add(new SubheaderNode(view.av, String.format("%s (FAVORITE)", label)));
                } else {
                    node.add(new TextNode(view.av, label));
                }
            }
        } else {
            node.add(new TextNode(view.av, "No players are competing for this patron right now"));
        }
        return node;
    }
}
