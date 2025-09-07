package net.lugocorp.kingdom.game.model;
import net.lugocorp.kingdom.engine.userdata.CoordUserData;
import net.lugocorp.kingdom.game.Game;
import net.lugocorp.kingdom.game.core.Events;
import net.lugocorp.kingdom.game.events.Event;
import net.lugocorp.kingdom.game.glyph.UnitGlyphs;
import net.lugocorp.kingdom.game.player.Player;
import net.lugocorp.kingdom.game.properties.EntityType;
import net.lugocorp.kingdom.game.properties.Inventory;
import net.lugocorp.kingdom.game.properties.Inventory.InventoryType;
import net.lugocorp.kingdom.game.properties.Species;
import net.lugocorp.kingdom.ui.menu.ActionNode;
import net.lugocorp.kingdom.ui.menu.GlyphIconsNode;
import net.lugocorp.kingdom.ui.menu.HeaderNode;
import net.lugocorp.kingdom.ui.menu.ListNode;
import net.lugocorp.kingdom.ui.menu.MenuNode;
import net.lugocorp.kingdom.ui.menu.MenuSubject;
import net.lugocorp.kingdom.ui.menu.TextNode;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.code.SideEffect;
import net.lugocorp.kingdom.utils.math.Coords;
import net.lugocorp.kingdom.utils.math.Hexagons;
import net.lugocorp.kingdom.utils.math.Point;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * A single controllable character (or NPC) that the player can interact with
 * in-game
 */
public class Unit extends Entity implements MenuSubject {
    public static final int MAX_LOYALTY = 10;
    private final CoordUserData userData = new CoordUserData();
    private Optional<Ability> active1 = Optional.empty();
    private Optional<Ability> active2 = Optional.empty();
    private SleepState sleep = SleepState.AWAKE;
    private int loyalty = Unit.MAX_LOYALTY;
    private Optional<Player> leader = Optional.empty();
    private int timeToHunger = 20;
    public final UnitGlyphs glyphs = new UnitGlyphs();
    public final Inventory equipped = new Inventory(InventoryType.EQUIP, 2);
    public final Inventory haul = new Inventory(InventoryType.HAUL, 4);
    public List<Ability> passives = new ArrayList<>();
    public Species species = Species.UNKNOWN;
    public boolean playable = true;

    Unit(String name, int x, int y) {
        super(name, x, y);
    }

    /**
     * This should only be used in conjunction with Kryo rehydration
     */
    public Unit() {
        super(null, 0, 0);
    }

    /** {@inheritdoc} */
    @Override
    public EntityType getEntityType() {
        return EntityType.UNIT;
    }

    /**
     * Returns true if this Unit is sleeping
     */
    public boolean isSleeping() {
        return this.sleep != SleepState.AWAKE;
    }

    /**
     * Checks if we should reset this Unit's SleepState at the start of a turn
     */
    public void wakeUpCheck(GameView view) {
        Events.IsStunnedEvent event = new Events.IsStunnedEvent(this);
        this.handleEvent(view, event).execute();
        if (event.isStunned) {
            this.sleep = SleepState.SLEEPING;
        } else if (this.sleep == SleepState.SLEEPING
                || (this.sleep == SleepState.SLEEPING_INVENTORY && this.haul.isFull())) {
            this.wakeUp();
        }
    }

    /**
     * Reset this Unit's SleepState
     */
    public void wakeUp() {
        this.sleep = SleepState.AWAKE;
    }

    /** {@inheritdoc} */
    @Override
    public Optional<Player> getLeader() {
        return this.leader;
    }

    /**
     * Sets the Player that commands this Unit (this should only ever be used in the
     * Game class)
     */
    public void setLeader(Optional<Player> leader) {
        this.leader = leader;
    }

    /**
     * Returns all the active Abilities this Unit has access to
     */
    public List<Ability> getActiveAbilities() {
        final List<Ability> list = new ArrayList<>();
        this.active1.ifPresent((Ability a) -> list.add(a));
        this.active2.ifPresent((Ability a) -> list.add(a));
        return list;
    }

    /**
     * Sets up to 2 active Abilities for this Unit
     */
    public void setActiveAbilities(Generator g, Optional<String> a1, Optional<String> a2) {
        a1.ifPresent((String active1) -> {
            this.active1 = Optional.of(g.ability(this, active1));
        });
        a2.ifPresent((String active2) -> {
            this.active2 = Optional.of(g.ability(this, active2));
        });
    }

    /**
     * Sets some passive Abilities for this Unit
     */
    public void setPassiveAbilities(Generator g, String... passives) {
        for (String p : passives) {
            this.passives.add(g.ability(this, p));
        }
    }

    /**
     * Returns true if this Unit has a passive ability by the given name
     */
    public boolean hasPassiveAbility(String name) {
        for (Ability a : this.passives) {
            if (a.name.equals(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Adds a status effect (Ability under the hood) to this Unit. Also triggers a
     * special Event on the Ability so it can kick off tick events
     */
    public SideEffect addStatusEffect(GameView view, String name) {
        Ability status = view.game.generator.ability(this, name);
        return SideEffect.all(() -> this.passives.add(status),
                status.handleEvent(view, new Events.StatusEffectAddedEvent(status, this)));
    }

    /**
     * Removes a status effect (Ability under the hood) from this Unit
     */
    public void removeStatusEffect(Ability status) {
        this.passives.remove(status);
    }

    /**
     * Changes how long this Unit takes to get hungry
     */
    public void setTimeToHunger(GameView view, int n) {
        final int diff = n - this.timeToHunger;
        final int remainingTurns = view.game.mechanics.turns.getFutureEventRemainingTurns(this, "GetsHungry");
        if (remainingTurns >= 0 && remainingTurns + diff <= 0) {
            view.game.mechanics.turns.handleFutureTicksEarly(view, this, "GetsHungry");
        }
        this.timeToHunger = n;
    }

    /**
     * Resets this Unit's hunger
     */
    public void eat(Game game) {
        game.mechanics.turns.removeFutureEvents(this, "GetsHungry");
        game.mechanics.turns.removeFutureEvents(this, "HungerStrikes");
        game.mechanics.turns.addFutureTick("GetsHungry", this, this.timeToHunger, false);
    }

    /**
     * This Unit loses loyalty and may abandon the cause
     */
    public void loseLoyalty(GameView view, int points) {
        this.loyalty = Math.max(0, this.loyalty - points);
        if (this.loyalty == 0) {
            view.game.mechanics.turns.removeFutureEvents(this, "HungerStrikes");
            view.game.setLeader(view, this, Optional.empty());
        }
    }

    /**
     * Resets this Unit's loyalty
     */
    public void resetLoyalty() {
        this.loyalty = Unit.MAX_LOYALTY;
    }

    /**
     * Returns true if this Unit is leaderless and can be picked up by anyone
     */
    public boolean isFreeRadical() {
        return !this.leader.isPresent() && this.loyalty == 0;
    }

    /**
     * Returns true if this Unit belongs to the human Player
     */
    public boolean belongsToHuman() {
        return this.leader.map((Player p) -> p.isHumanPlayer()).orElse(false);
    }

    /**
     * Recruits this Unit into to a new Player
     */
    public void getRecruited(GameView view, Player player) {
        if (!this.isFreeRadical()) {
            throw new RuntimeException("Cannot recruit another player's unit");
        }
        view.game.mechanics.turns.addFutureTick("HungerStrikes", this, 1, true);
        view.game.setLeader(view, this, player);
        this.resetLoyalty();
    }

    /**
     * Returns the maximum distance that this Unit can move in a turn
     */
    private int getMaxMoveDistance(GameView view) {
        Events.UnitMoveDistanceEvent event = new Events.UnitMoveDistanceEvent(this);
        this.handleEvent(view, event);
        return event.distance;
    }

    /**
     * Returns the list of Points that this Unit can move to
     */
    public Set<Point> getMoveTargets(GameView view) {
        int max = this.getMaxMoveDistance(view);
        if (max == 0) {
            return new HashSet<Point>();
        }
        Point origin = new Point(this.x, this.y);
        Set<Point> next = new HashSet<>();
        Set<Point> targets = new HashSet<>();
        Set<Point> visited = new HashSet<>();
        Set<Point> adj = Hexagons.getNeighbors(origin, 1);
        visited.add(origin);
        for (int a = 0; a < max; a++) {
            for (Point p : adj) {
                // Optimization: skip already visited Points
                if (visited.contains(p)) {
                    continue;
                }
                visited.add(p);

                // Units cannot walk on Tiles that don't exist or already have a Unit
                Optional<Tile> t = view.game.world.getTile(p);
                if (!t.isPresent()) {
                    continue;
                }
                Tile tile = t.get();
                if (tile.unit.isPresent()) {
                    continue;
                }

                // Use event handler to check if this Unit can move here
                Events.CanUnitMoveEvent event = new Events.CanUnitMoveEvent(this, tile);
                this.handleEvent(view, event);
                if (!event.possible()) {
                    continue;
                }
                targets.add(p);
                if (a < max - 1) {
                    next.addAll(Hexagons.getNeighbors(p, 1));
                }
            }
            visited.addAll(adj);
            adj.clear();
            adj.addAll(next);
            next.clear();
        }
        return targets;
    }

    /** {@inheritdoc} */
    @Override
    protected void setupModelInstance(ModelInstance model) {
        model.userData = this.userData;
    }

    /**
     * Sets this Unit's position in the World. Useful for spawning or movement.
     */
    public void setPosition(GameView view, int x, int y) {
        Tile destin = view.game.world.getTile(x, y).get();
        destin.unit = Optional.of(this);
        this.x = x;
        this.y = y;
        this.resetModelPosition();
        view.game.setLeader(view, destin, this.leader);
        destin.building.ifPresent((Building b) -> b.setAlpha(0.5f));
        this.userData.point.x = x;
        this.userData.point.y = y;
    }

    /**
     * Removes this Unit from its current position in the World
     */
    private void removeFromPosition(Game g) {
        Tile origin = g.world.getTile(this.x, this.y).get();
        origin.building.ifPresent((Building b) -> b.setAlpha(1f));
        origin.unit = Optional.empty();
    }

    /**
     * Moves this Unit to another Tile in the grid
     */
    public SideEffect move(GameView view, Point p) {
        int x = this.x;
        int y = this.y;
        this.leader.ifPresent((Player l) -> this.vision.translate(l, view.game.world, p.x - x, p.y - y));
        this.removeFromPosition(view.game);
        this.setPosition(view, p.x, p.y);
        return this.handleEvent(view, new Events.UnitMovedEvent(this, x, y, p.x, p.y));
    }

    /**
     * Spawns this loaded object into the World
     */
    public void spawn(GameView view) {
        this.setPosition(view, this.x, this.y);
        this.handleEvent(view, new Events.SpawnEvent<Unit>(this));
        this.eat(view.game);
    }

    /** {@inheritdoc} */
    @Override
    public Vector3 getPositionVector() {
        return Coords.grid.vector(this.x, this.y).add(Coords.raw.vector(0, Hexagons.HEIGHT, 0));
    }

    /** {@inheritdoc} */
    @Override
    public SideEffect handleEventWithoutSignalBooster(GameView view, Event e) {
        List<SideEffect> effects = SideEffect.list();
        effects.add(view.game.events.unit.handle(view, this, e));
        if (e.propagate) {
            this.active1.ifPresent((Ability a) -> effects.add(a.handleEventWithoutSignalBooster(view, e)));
            this.active2.ifPresent((Ability a) -> effects.add(a.handleEventWithoutSignalBooster(view, e)));
            for (Ability a : this.passives) {
                effects.add(a.handleEventWithoutSignalBooster(view, e));
            }
            for (Item i : this.equipped) {
                effects.add(i.handleEventWithoutSignalBooster(view, e));
            }
        }
        return SideEffect.all(effects);
    }

    /** {@inheritdoc} */
    @Override
    public void deactivate(GameView view) {
        super.deactivate(view);
        this.leader.ifPresent((Player l) -> {
            this.vision.remove(l, view.game.world);
            l.units.remove(this);
        });
        this.removeFromPosition(view.game);
        this.dispose();
    }

    /** {@inheritdoc} */
    @Override
    public MenuNode getMenuContent(GameView view, Optional<Point> p) {
        ListNode node = new ListNode().add(new HeaderNode(view.av, this.name));
        node.add(new GlyphIconsNode(view.av, this.glyphs.get())).add(new TextNode(view.av, this.desc));
        if (this.leader.isPresent()) {
            node.add(new TextNode(view.av, String.format("Alignment: %s", this.leader.get().name)));
        }
        node.add(new TextNode(view.av, this.species.toString()));
        node.add(new TextNode(view.av,
                String.format("Health: %d/%d", this.combat.health.get(), this.combat.health.getMax())));
        node.add(new TextNode(view.av, String.format("%d / %d loyalty", this.loyalty, Unit.MAX_LOYALTY)));
        int turnsUntilHungry = view.game.mechanics.turns.getFutureEventRemainingTurns(this, "GetsHungry");
        if (turnsUntilHungry < 0) {
            node.add(new TextNode(view.av, "This unit is hungry and will lose loyalty until it is fed"));
        } else {
            node.add(new TextNode(view.av, String.format("%d turn(s) until hunger strikes", turnsUntilHungry)));
        }
        if (this.leader.map((Player p1) -> p1.isHumanPlayer()).orElse(false)) {
            if (view.game.mechanics.turns.hasUnitActed(this)) {
                node.add(new TextNode(view.av, "This unit has already acted this turn"));
            } else if (this.isSleeping()) {
                node.add(new TextNode(view.av, "This unit does not have to act this turn"));
            }
            node.add(new ActionNode(view.av, "Move", Optional.empty(), !view.game.mechanics.turns.hasUnitActed(this),
                    () -> view.selector.select(this.getMoveTargets(view), "This unit cannot move", (Point p1) -> {
                        this.move(view, p1).execute();
                        view.game.mechanics.turns.unitHasActed(view, this);
                        view.hud.minimap.refresh(view.game.world);
                    })));
            node.add(new ActionNode(view.av, "Skip turn", Optional.empty(),
                    !view.game.mechanics.turns.hasUnitActed(this), () -> {
                        this.sleep = SleepState.SLEEPING;
                        view.game.mechanics.turns.goToNextUnit(view);
                    }));
            node.add(new ActionNode(view.av, "Skip until inventory is full", Optional.empty(),
                    !view.game.mechanics.turns.hasUnitActed(this), () -> {
                        this.sleep = SleepState.SLEEPING_INVENTORY;
                        view.game.mechanics.turns.goToNextUnit(view);
                    }));
        }
        this.active1.ifPresent((Ability a) -> node.add(a.getMenuContent(view, p)));
        this.active2.ifPresent((Ability a) -> node.add(a.getMenuContent(view, p)));
        for (Ability a : this.passives) {
            node.add(a.getMenuContent(view, p));
        }
        if (this.leader.map((Player p1) -> p1.isHumanPlayer()).orElse(false)) {
            node.add(new TextNode(view.av, "Equipped Items"));
            node.add(this.equipped.getMenuContent(view, p));
            node.add(new TextNode(view.av, "Hauled Items"));
            node.add(this.haul.getMenuContent(view, p));
        } else {
            node.add(new TextNode(view.av, String.format("Can equip %d items", this.equipped.getMax())));
            node.add(new TextNode(view.av, String.format("Can haul %d items", this.haul.getMax())));
        }
        return node;
    }

    /**
     * Enums to control sleeping state (how long to sleep for)
     */
    public static enum SleepState {
        AWAKE, SLEEPING, SLEEPING_INVENTORY
    }
}
