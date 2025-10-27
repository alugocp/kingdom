package net.lugocorp.kingdom.game.model;
import net.lugocorp.kingdom.builtin.Events;
import net.lugocorp.kingdom.color.ColorScheme;
import net.lugocorp.kingdom.color.Colors;
import net.lugocorp.kingdom.engine.userdata.CoordUserData;
import net.lugocorp.kingdom.game.actions.ActionType;
import net.lugocorp.kingdom.game.actions.SkipAction;
import net.lugocorp.kingdom.game.events.Event;
import net.lugocorp.kingdom.game.glyph.UnitGlyphs;
import net.lugocorp.kingdom.game.layers.Entity;
import net.lugocorp.kingdom.game.layers.Spawnable;
import net.lugocorp.kingdom.game.player.Player;
import net.lugocorp.kingdom.game.properties.EntityType;
import net.lugocorp.kingdom.game.properties.Inventory;
import net.lugocorp.kingdom.game.properties.Inventory.InventoryType;
import net.lugocorp.kingdom.game.properties.Species;
import net.lugocorp.kingdom.game.unit.Abilities;
import net.lugocorp.kingdom.game.unit.Adjacency;
import net.lugocorp.kingdom.game.unit.Hunger;
import net.lugocorp.kingdom.game.unit.Leadership;
import net.lugocorp.kingdom.game.unit.Loyalty;
import net.lugocorp.kingdom.game.unit.Movement;
import net.lugocorp.kingdom.game.unit.Sleep;
import net.lugocorp.kingdom.math.Coords;
import net.lugocorp.kingdom.math.Hexagons;
import net.lugocorp.kingdom.math.Point;
import net.lugocorp.kingdom.menu.MenuNode;
import net.lugocorp.kingdom.menu.MenuSubject;
import net.lugocorp.kingdom.menu.game.GlyphIconsNode;
import net.lugocorp.kingdom.menu.game.ResourceBarsNode;
import net.lugocorp.kingdom.menu.structure.ListNode;
import net.lugocorp.kingdom.menu.structure.RowNode;
import net.lugocorp.kingdom.menu.text.ActionNode;
import net.lugocorp.kingdom.menu.text.BadgeNode;
import net.lugocorp.kingdom.menu.text.HelperNode;
import net.lugocorp.kingdom.menu.text.SubheaderNode;
import net.lugocorp.kingdom.menu.text.TextNode;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.SideEffect;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * A single controllable character (or NPC) that the player can interact with
 * in-game
 */
public class Unit extends Entity implements MenuSubject, Spawnable {
    private final CoordUserData userData = new CoordUserData(() -> false);
    public final Adjacency nextTo = new Adjacency(this);
    public final Leadership leadership = new Leadership(this);
    public final Sleep sleep = new Sleep(this);
    public final Loyalty loyalty = new Loyalty(this);
    public final Abilities abilities = new Abilities(this);
    public final Hunger hunger = new Hunger(this);
    public final Movement movement = new Movement(this, this.userData);
    public final UnitGlyphs glyphs = new UnitGlyphs();
    public final Inventory equipped = new Inventory(InventoryType.EQUIP, 2);
    public final Inventory haul = new Inventory(InventoryType.HAUL, 4);
    public Species species = Species.UNKNOWN;

    public Unit(String name, int x, int y) {
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
    public void spawn(GameView view) {
        this.movement.setPosition(view, this.x, this.y);
        this.handleEvent(view, new Events.SpawnEvent<Unit>(this)).execute();
        this.hunger.eat(view, false);
        view.game.units.add(this);
    }

    /** {@inheritdoc} */
    @Override
    public EntityType getEntityType() {
        return EntityType.UNIT;
    }

    /** {@inheritdoc} */
    @Override
    public Optional<Player> getLeader() {
        return this.leadership.getLeader();
    }

    /** {@inheritdoc} */
    @Override
    protected void setupModelInstance(ModelInstance model) {
        model.userData = this.userData;
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
            for (Ability a : this.abilities.getActives()) {
                effects.add(a.handleEventWithoutSignalBooster(view, e));
            }
            for (Ability a : this.abilities.getPassives()) {
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
        this.getLeader().ifPresent((Player l) -> {
            this.vision.remove(l, view.game.world);
            l.units.remove(this);
        });
        view.game.units.remove(this);
        this.movement.removeFromPosition(view.game);
        this.dispose();
    }

    /** {@inheritdoc} */
    @Override
    public MenuNode getMenuContent(GameView view, Optional<Point> p) {
        final RowNode node = new RowNode();

        // Unit stats section
        final ListNode col1 = new ListNode().add(new SubheaderNode(view.av, this.name));
        final MenuNode glyphsNode = new GlyphIconsNode(view.av, this.glyphs.get());
        final int turnsUntilHungry = Math.max(0, view.game.future.getFutureEventRemainingTurns(this, "GetsHungry"));
        col1.add(new BadgeNode(view.av, this.species.color, ColorScheme.WHITE.hex, this.species.toString()));
        col1.add(this.getLeader().isPresent()
                ? new RowNode().add(glyphsNode)
                        .add(new BadgeNode(view.av, Colors.asInt(this.getLeader().get().color), ColorScheme.WHITE.hex,
                                this.getLeader().get().name))
                : glyphsNode);
        col1.add(new TextNode(view.av, this.desc))
                .add(new TextNode(view.av, String.format("This unit eats %s items", this.hunger.getPreferredFood())));
        col1.add(new ResourceBarsNode(view.av,
                new ResourceBarsNode.Bar("Health", 0x3d9e33, this.combat.health.get(), this.combat.health.getMax()),
                new ResourceBarsNode.Bar("Loyalty", 0x203fab, this.loyalty.get(), Loyalty.MAX_LOYALTY),
                new ResourceBarsNode.Bar("Hunger", 0x7d4513, turnsUntilHungry, this.hunger.getTurnsBeforeHunger())));
        col1.add(new HelperNode(view.av, new ListNode().add(new SubheaderNode(view.av, "Health"))
                .add(new TextNode(view.av, "If a unit's health bar hits zero then they disappear off the map."))
                .add(new SubheaderNode(view.av, "Loyalty"))
                .add(new TextNode(view.av,
                        "If a unit's loyalty bar hits zero then it will abandon you and become independent. You can recruit an independent unit by giving it an item."))
                .add(new SubheaderNode(view.av, "Hunger")).add(new TextNode(view.av,
                        "The hunger bar decreases each turn until it's empty, then loyalty will decrease each turn. A unit can refill its hunger bar by consuming an edible item."))));

        // Actions / spells section
        final ListNode col2 = new ListNode().add(new SubheaderNode(view.av, "Spells"))
                .add(new TextNode(view.av, view.game.actions.getUnitActionLabel(this)));
        if (this.leadership.belongsToHuman() && view.game.mechanics.turns.canHumanPlayerAct()) {

            // Move unit
            if (view.game.actions.canUnitDoThis(this, ActionType.MOVE)) {
                col2.add(new ActionNode(view.av, "Move",
                        Optional.of("Moves this unit to the target tile (may exhaust this unit's actions)"),
                        () -> view.selector.move(this)));
            }

            // Deposit Items
            if (this.nextTo.vault(view.game)) {
                col2.add(new ActionNode(view.av, "Deposit",
                        Optional.of(
                                "Gives all stored items to an adjacent vault (does not exhaust this unit's actions)"),
                        () -> view.selector.deposit(this)));
            }

            // Give Food
            final Set<Point> unitsToFeed = this.nextTo.unitsToFeed(view);
            if (this.haul.hasItems() && unitsToFeed.size() > 0) {
                col2.add(new ActionNode(view.av, "Give Food", Optional.of(
                        "This unit gives one of its edible stored items to an adjacent unit (does not exhaust this unit's actions)"),
                        () -> this.getLeader().get()
                                .select(view, unitsToFeed, "No adjacent units to feed", (Point consumer) -> {
                                    final Unit u = view.game.world.getTile(consumer).flatMap((Tile t) -> t.unit).get();
                                    final Set<Item> food = this.haul.getEdibleItems(view, u);
                                    return () -> this.haul.transfer(u.haul, food.iterator().next());
                                }).execute()));
            }

            // Skip turn until haul Inventory is full
            if (!this.haul.isFull() && view.game.actions.canUnitDoThis(this, ActionType.SKIP)) {
                col2.add(new ActionNode(view.av, "Store items", Optional.of(
                        "This unit won't ask for commands until it runs out of stored item space (this avoids micromanaging units with harvest spells) (does not exhaust this unit's actions)"),
                        () -> {
                            view.hud.logger.log(String.format("%s will wait where they are", this.name));
                            view.game.actions.unitHasActed(view, this, new SkipAction(
                                    "This unit is waiting for maximum stored items, but you can give it a different command",
                                    () -> this.haul.isFull()));
                            view.hud.bot.tileMenu.refresh();
                            view.game.actions.goToNextUnit(view);
                        }));
            }

            // Skip turn
            if (view.game.actions.canUnitDoThis(this, ActionType.SKIP)) {
                col2.add(new ActionNode(view.av, "Skip turn",
                        Optional.of(
                                "This unit won't ask for commands this turn (does not exhaust this unit's actions)"),
                        () -> {
                            view.game.actions.unitHasActed(view, this,
                                    new SkipAction(
                                            "This unit is skipping its turn, but you can give it a different command",
                                            () -> true));
                            view.hud.bot.tileMenu.refresh();
                            view.game.actions.goToNextUnit(view);
                        }));
            }
        }
        for (Ability a : this.abilities.getActives()) {
            col2.add(a.getMenuContent(view, p));
        }
        for (Ability a : this.abilities.getPassives()) {
            col2.add(a.getMenuContent(view, p));
        }

        // Items section
        final ListNode col3 = new ListNode();
        if (this.getLeader().map((Player p1) -> p1.isHumanPlayer()).orElse(false)) {
            col3.add(new SubheaderNode(view.av, "Equipped Items"));
            col3.add(this.equipped.getMenuContent(view, p));
            col3.add(new SubheaderNode(view.av, "Stored Items"));
            col3.add(this.haul.getMenuContent(view, p));
        } else {
            col3.add(new SubheaderNode(view.av, "Inventory"));
            col3.add(new TextNode(view.av, String.format("Can equip up to %d items", this.equipped.getMax())));
            col3.add(new TextNode(view.av, String.format("Can store up to %d items", this.haul.getMax())));
        }
        return node.add(col1).add(col2).add(col3);
    }
}
