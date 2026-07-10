package net.lugocorp.kingdom.game.model;
import net.lugocorp.kingdom.builtin.Events;
import net.lugocorp.kingdom.color.ColorScheme;
import net.lugocorp.kingdom.game.Game;
import net.lugocorp.kingdom.game.player.Player;
import net.lugocorp.kingdom.game.properties.Domain;
import net.lugocorp.kingdom.game.properties.EntityType;
import net.lugocorp.kingdom.gameplay.events.Event;
import net.lugocorp.kingdom.math.Point;
import net.lugocorp.kingdom.menu.MenuNode;
import net.lugocorp.kingdom.menu.icon.ActionNode;
import net.lugocorp.kingdom.menu.icon.HeaderDescNode;
import net.lugocorp.kingdom.menu.icon.HelperNode;
import net.lugocorp.kingdom.menu.icon.IconNode;
import net.lugocorp.kingdom.menu.structure.ListNode;
import net.lugocorp.kingdom.menu.structure.RowNode;
import net.lugocorp.kingdom.menu.text.HeaderNode;
import net.lugocorp.kingdom.menu.text.PlayerBadgeNode;
import net.lugocorp.kingdom.menu.text.SubheaderNode;
import net.lugocorp.kingdom.menu.text.TextNode;
import net.lugocorp.kingdom.ui.overlay.RisingOverlay;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.Lambda;
import net.lugocorp.kingdom.utils.SideEffect;
import com.esotericsoftware.kryo.serializers.FieldSerializer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Represents a local spirit that Players can compete for favor with
 */
public class Patron extends Building {
    private static final int MIN_FAVOR = 1;
    private final Map<Player, Integer> favor = new HashMap<>();
    @FieldSerializer.Optional("isPreferredUnitType")
    public Function<Unit, Boolean> isPreferredUnitType = (Unit u) -> false;
    private Optional<Player> favorite = Optional.empty();
    private String preferenceIcon = "apple";
    private String effectIcon = "apple";
    public String preference = "";
    public String effect = "";

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

    /**
     * Call this when we're reloading this instance from saved game state
     */
    public void rehydratePatronFromKryo(Game game) {
        this.isPreferredUnitType = game.generator.patronDryRun(this.name, 0, 0).isPreferredUnitType;
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
     * Sets the effect and preference icons for this Patron
     */
    public void setIcons(String effectIcon, String preferenceIcon) {
        this.preferenceIcon = preferenceIcon;
        this.effectIcon = effectIcon;
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
        final Set<Point> domain = view.game.world.getTile(this.getPoint())
                .map((Tile t) -> t.getDomainCenter().domain.get()).orElse(new HashSet<Point>());
        for (Point p : domain) {
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
            view.overlays.entity(u).addRising(
                    new RisingOverlay(view, u, ColorScheme.BLUE.hex, String.format("%d favor", event.favor)));

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
     * Returns the Domain associated with this Patron's closest Tower
     */
    public Domain getHostDomain(GameView view) {
        return view.game.world.getTile(this.getPoint()).get().getDomainCenter().domain;
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
        });
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
        final ListNode favors = new ListNode().add(new SubheaderNode(view.av, "Favor"));
        final RowNode node = new RowNode().add(new ListNode().add(new HeaderNode(view.av, this.name)).add(new RowNode()
                .addExact(IconNode.SIDE + 10, new HelperNode(view.av,
                        "Patrons are guardians of the natural world. You can gain favor with a patron by maintaining a presence in its surrounding domain. Your units determine your favor with all nearby patrons each turn. Each patron gives its favorite player some bonus for that turn."))
                .addExact(ActionNode.SIDE + 10, new HeaderDescNode(view.av, this.effectIcon, "Effect", this.effect))
                .addExact(ActionNode.SIDE + 10,
                        new HeaderDescNode(view.av, this.preferenceIcon, "Preferred Units", this.preference)))
                .add(new TextNode(view.av, this.desc))).add(favors);
        if (this.favor.size() > 0) {
            final List<Player> sorted = Lambda.sort((Player player) -> this.favor.get(player),
                    Lambda.toList(this.favor.keySet()));
            for (Player k : sorted) {
                final boolean fav = this.favorite.map((Player f) -> k == f).orElse(false);
                favors.add(new RowNode().addRatio(30, new PlayerBadgeNode(view.av, k)).add(
                        new TextNode(view.av, String.format("%d%s", this.favor.get(k), fav ? " (favorite)" : ""))));
            }
        } else {
            favors.add(new TextNode(view.av, "No players are competing for this patron right now"));
        }
        return node;
    }
}
