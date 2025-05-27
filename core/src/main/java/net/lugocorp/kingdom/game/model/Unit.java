package net.lugocorp.kingdom.game.model;
import net.lugocorp.kingdom.engine.render.DynamicModellable;
import net.lugocorp.kingdom.game.Game;
import net.lugocorp.kingdom.game.combat.HitPoints;
import net.lugocorp.kingdom.game.core.Events;
import net.lugocorp.kingdom.game.events.Event;
import net.lugocorp.kingdom.game.events.EventReceiver;
import net.lugocorp.kingdom.game.mechanics.Visibility;
import net.lugocorp.kingdom.game.model.Inventory.InventoryType;
import net.lugocorp.kingdom.ui.menu.ActionNode;
import net.lugocorp.kingdom.ui.menu.HeaderNode;
import net.lugocorp.kingdom.ui.menu.ListNode;
import net.lugocorp.kingdom.ui.menu.MenuNode;
import net.lugocorp.kingdom.ui.menu.MenuSubject;
import net.lugocorp.kingdom.ui.menu.TextNode;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.math.Coords;
import net.lugocorp.kingdom.utils.math.Hexagons;
import net.lugocorp.kingdom.utils.math.Point;
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
    private Optional<Ability> active1 = Optional.empty();
    private Optional<Ability> active2 = Optional.empty();
    private SleepState sleep = SleepState.AWAKE;
    private int loyalty = Unit.MAX_LOYALTY;
    public final Visibility visibility = new Visibility();
    public final Tags tags = new Tags();
    public final String name;
    public final HitPoints<Unit> health;
    public final UnitGlyphs glyphs = new UnitGlyphs();
    public Optional<Player> leader = Optional.empty();
    public List<Ability> passives = new ArrayList<>();
    public Inventory equipped = new Inventory(InventoryType.EQUIP, 2);
    public Inventory haul = new Inventory(InventoryType.HAUL, 4);
    public boolean playable = true;
    public String desc = "";

    Unit(String name, int x, int y) {
        super(x, y);
        this.name = name;
        this.health = new HitPoints<Unit>(this);
    }

    /**
     * This should only be used in conjunction with Kryo rehydration
     */
    public Unit() {
        super(0, 0);
        this.health = null;
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
     * Resets this Unit's hunger
     */
    public void eat(Game game) {
        game.mechanics.turns.removeFutureEvents(this, "GetsHungry");
        game.mechanics.turns.removeFutureEvents(this, "HungerStrikes");
        game.mechanics.turns.addFutureTick("GetsHungry", this, 20, false);
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
    private Set<Point> getMoveTargets(GameView view) {
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
    private void move(Game g, Point p) {
        if (this.belongsToHuman()) {
            this.visibility.translate(g.world, p.x - this.x, p.y - this.y);
        }
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
    public void handleEventWithoutSignalBooster(GameView view, Event e) {
        view.game.events.unit.handle(view, this, e);
        if (e.propagate) {
            this.active1.ifPresent((Ability a) -> a.handleEventWithoutSignalBooster(view, e));
            this.active2.ifPresent((Ability a) -> a.handleEventWithoutSignalBooster(view, e));
            for (Ability a : this.passives) {
                a.handleEventWithoutSignalBooster(view, e);
            }
            for (int a = 0; a < this.equipped.getSize(); a++) {
                Item i = this.equipped.get(a);
                i.handleEventWithoutSignalBooster(view, e);
            }
        }
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
        if (this.belongsToHuman()) {
            this.visibility.removeVision(view.game.world);
        }
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
        node.add(new TextNode(view.av, String.format("Health: %d/%d", this.health.get(), this.health.getMax())));
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

    /**
     * This nested class handles access to a Unit's Glyphs
     */
    public static class UnitGlyphs {
        private Optional<Glyph> g2 = Optional.empty();
        private Glyph g1 = Glyph.BATTLE;

        // No one needs to instantiate this class apart from its host Unit
        private UnitGlyphs() {
            this.setDefault();
        }

        /**
         * Sets a single Glyph on this Unit
         */
        public void set(Glyph g) {
            this.g2 = Optional.empty();
            this.g1 = g;
        }

        /**
         * Sets two Glyphs on this Unit
         */
        public void set(Glyph g1, Glyph g2) {
            this.g2 = Optional.of(g2);
            this.g1 = g1;
        }

        /**
         * Sets the default Glyphs for this Unit
         */
        public void setDefault() {
            this.set(Glyph.BATTLE);
        }

        /**
         * Returns true if this Unit has the given Glyph
         */
        public boolean has(Glyph g) {
            return this.g1 == g || this.g2.map((Glyph g2) -> g2 == g).orElse(false);
        }

        /**
         * Retrieves the Glyphs associated with this Unit
         */
        public Glyph[] get() {
            if (this.g2.isPresent()) {
                return new Glyph[]{this.g1, this.g2.get()};
            }
            return new Glyph[]{this.g1};
        }
    }
}
