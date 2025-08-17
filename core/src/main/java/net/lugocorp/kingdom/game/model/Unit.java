package net.lugocorp.kingdom.game.model;
import net.lugocorp.kingdom.engine.render.DynamicModellable;
import net.lugocorp.kingdom.engine.render.userdata.CoordUserData;
import net.lugocorp.kingdom.game.Game;
import net.lugocorp.kingdom.game.combat.Combat;
import net.lugocorp.kingdom.game.core.Events;
import net.lugocorp.kingdom.game.events.Event;
import net.lugocorp.kingdom.game.events.EventReceiver;
import net.lugocorp.kingdom.game.mechanics.Visibility;
import net.lugocorp.kingdom.game.model.fields.Inventory;
import net.lugocorp.kingdom.game.model.fields.Inventory.InventoryType;
import net.lugocorp.kingdom.game.model.fields.Race;
import net.lugocorp.kingdom.game.model.fields.Tags;
import net.lugocorp.kingdom.game.model.fields.UnitGlyphs;
import net.lugocorp.kingdom.game.player.Player;
import net.lugocorp.kingdom.ui.menu.ActionNode;
import net.lugocorp.kingdom.ui.menu.HeaderNode;
import net.lugocorp.kingdom.ui.menu.ListNode;
import net.lugocorp.kingdom.ui.menu.MenuNode;
import net.lugocorp.kingdom.ui.menu.MenuSubject;
import net.lugocorp.kingdom.ui.menu.TextNode;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.SideEffect;
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
 * A single controllable entity (or NPC) that the player can interact with
 * in-game
 */
public class Unit extends DynamicModellable implements EventReceiver, MenuSubject {
    public static final int MAX_LOYALTY = 10;
    private final CoordUserData userData = new CoordUserData();
    private Optional<Ability> active1 = Optional.empty();
    private Optional<Ability> active2 = Optional.empty();
    private SleepState sleep = SleepState.AWAKE;
    private int loyalty = Unit.MAX_LOYALTY;
    private Optional<Player> leader = Optional.empty();
    private int timeToHunger = 20;
    public final Visibility visibility = new Visibility();
    public final Tags tags = new Tags();
    public final String name;
    public final Combat<Unit> combat;
    public final UnitGlyphs glyphs = new UnitGlyphs();
    public final Inventory equipped = new Inventory(InventoryType.EQUIP, 2);
    public final Inventory haul = new Inventory(InventoryType.HAUL, 4);
    public List<Ability> passives = new ArrayList<>();
    public Race race = Race.UNKNOWN;
    public boolean playable = true;
    public int visibleRadius = 2;
    public String desc = "";

    Unit(String name, int x, int y) {
        super(x, y);
        this.name = name;
        this.combat = new Combat<Unit>(this);
    }

    /**
     * This should only be used in conjunction with Kryo rehydration
     */
    public Unit() {
        super(0, 0);
        this.combat = null;
        this.name = null;
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
    public void wakeUpCheck() {
        if (this.sleep == SleepState.SLEEPING || (this.sleep == SleepState.SLEEPING_INVENTORY && this.haul.isFull())) {
            this.wakeUp();
        }
    }

    /**
     * Reset this Unit's SleepState
     */
    public void wakeUp() {
        this.sleep = SleepState.AWAKE;
    }

    /**
     * Returns the Player that commands this Unit, if there is one
     */
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
    public void loseLoyalty(Game g, int points) {
        this.loyalty = Math.max(0, this.loyalty - points);
        if (this.loyalty == 0) {
            g.mechanics.turns.removeFutureEvents(this, "HungerStrikes");
            g.setLeader(this, Optional.empty());
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
    public void getRecruited(Game game, Player player) {
        if (!this.isFreeRadical()) {
            throw new RuntimeException("Cannot recruit another player's unit");
        }
        game.mechanics.turns.addFutureTick("HungerStrikes", this, 1, true);
        game.setLeader(this, player);
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
     * Returns the maximum range that this Unit can attack from
     */
    public int getAttackRange(GameView view) {
        Events.UnitAttackRangeEvent event = new Events.UnitAttackRangeEvent(this);
        this.handleEvent(view, event);
        return event.range;
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
    public void setPosition(Game g, int x, int y) {
        Tile destin = g.world.getTile(x, y).get();
        destin.unit = Optional.of(this);
        this.x = x;
        this.y = y;
        this.resetModelPosition();
        g.setLeader(destin, this.leader);
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
    public void move(Game g, Point p) {
        this.leader.ifPresent((Player l) -> this.visibility.translate(l, g.world, p.x - this.x, p.y - this.y));
        this.removeFromPosition(g);
        this.setPosition(g, p.x, p.y);
    }

    /**
     * Spawns this loaded object into the World
     */
    public void spawn(GameView view) {
        this.setPosition(view.game, this.x, this.y);
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
        List<SideEffect> effects = new ArrayList<>();
        effects.add(view.game.events.unit.handle(view, this, e));
        if (e.propagate) {
            this.active1.ifPresent((Ability a) -> a.handleEventWithoutSignalBooster(view, e));
            this.active2.ifPresent((Ability a) -> a.handleEventWithoutSignalBooster(view, e));
            for (Ability a : this.passives) {
                effects.add(a.handleEventWithoutSignalBooster(view, e));
            }
            for (int a = 0; a < this.equipped.getSize(); a++) {
                Item i = this.equipped.get(a);
                effects.add(i.handleEventWithoutSignalBooster(view, e));
            }
        }
        return SideEffect.all(effects);
    }

    /** {@inheritdoc} */
    @Override
    public String getStratifier() {
        return this.name;
    }

    /** {@inheritdoc} */
    @Override
    public void deactivate(GameView view) {
        EventReceiver.super.deactivate(view);
        this.leader.ifPresent((Player l) -> this.visibility.removeVision(l, view.game.world));
        this.removeFromPosition(view.game);
        this.dispose();
    }

    /** {@inheritdoc} */
    @Override
    public MenuNode getMenuContent(GameView view, Optional<Point> p) {
        ListNode node = new ListNode().add(new HeaderNode(view.av, this.name)).add(new TextNode(view.av, this.desc));
        if (this.leader.isPresent()) {
            node.add(new TextNode(view.av, String.format("Alignment: %s", this.leader.get().name)));
        }
        node.add(new TextNode(view.av, this.race.toString()));
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
                        this.move(view.game, p1);
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
