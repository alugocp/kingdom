package net.lugocorp.kingdom.game.model;
import net.lugocorp.kingdom.builtin.Events;
import net.lugocorp.kingdom.engine.userdata.CoordUserData;
import net.lugocorp.kingdom.game.actions.SkipAction;
import net.lugocorp.kingdom.game.actions.SkipInventoryAction;
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
import net.lugocorp.kingdom.game.unit.Hunger;
import net.lugocorp.kingdom.game.unit.Leadership;
import net.lugocorp.kingdom.game.unit.Loyalty;
import net.lugocorp.kingdom.game.unit.Movement;
import net.lugocorp.kingdom.game.unit.Sleep;
import net.lugocorp.kingdom.ui.MenuNode;
import net.lugocorp.kingdom.ui.MenuSubject;
import net.lugocorp.kingdom.ui.nodes.ActionNode;
import net.lugocorp.kingdom.ui.nodes.BadgeNode;
import net.lugocorp.kingdom.ui.nodes.GlyphIconsNode;
import net.lugocorp.kingdom.ui.nodes.HeaderNode;
import net.lugocorp.kingdom.ui.nodes.ListNode;
import net.lugocorp.kingdom.ui.nodes.ResourceBarsNode;
import net.lugocorp.kingdom.ui.nodes.RowNode;
import net.lugocorp.kingdom.ui.nodes.TextNode;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.Colors;
import net.lugocorp.kingdom.utils.code.SideEffect;
import net.lugocorp.kingdom.utils.math.Coords;
import net.lugocorp.kingdom.utils.math.Hexagons;
import net.lugocorp.kingdom.utils.math.Point;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import java.util.List;
import java.util.Optional;

/**
 * A single controllable character (or NPC) that the player can interact with
 * in-game
 */
public class Unit extends Entity implements MenuSubject, Spawnable {
    private final CoordUserData userData = new CoordUserData();
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
        this.handleEvent(view, new Events.SpawnEvent<Unit>(this));
        this.hunger.eat(view.game);
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
        ListNode node = new ListNode().add(new HeaderNode(view.av, this.name));

        // Unit stats section
        MenuNode glyphsNode = new GlyphIconsNode(view.av, this.glyphs.get());
        int turnsUntilHungry = Math.max(0, view.game.future.getFutureEventRemainingTurns(this, "GetsHungry"));
        node.add(new BadgeNode(view.av, this.species.color, 0xffffff, this.species.toString()));
        node.add(this.getLeader().isPresent()
                ? new RowNode().add(glyphsNode)
                        .add(new BadgeNode(view.av, Colors.asInt(this.getLeader().get().color), 0xffffff,
                                this.getLeader().get().name))
                : glyphsNode);
        node.add(new TextNode(view.av, this.desc));
        node.add(new ResourceBarsNode(view.av,
                new ResourceBarsNode.Bar("Health", 0x3d9e33, this.combat.health.get(), this.combat.health.getMax()),
                new ResourceBarsNode.Bar("Loyalty", 0x203fab, this.loyalty.get(), Loyalty.MAX_LOYALTY),
                new ResourceBarsNode.Bar("Hunger", 0x7d4513, turnsUntilHungry, this.hunger.getTurnsBeforeHunger())));

        // Abilities section
        if (this.getLeader().map((Player p1) -> p1.isHumanPlayer()).orElse(false)) {
            node.add(new TextNode(view.av, view.game.actions.getUnitActionLabel(this)));

            // Move Action
            final int remainingDistance = view.game.actions.getRemainingMoveDistance(view, this);
            node.add(new ActionNode(view, "Move", Optional.empty(), remainingDistance > 0,
                    () -> view.selector.move(this)));

            // Skip Action
            node.add(new ActionNode(view, "Skip turn", Optional.empty(), !view.game.actions.hasUnitActed(this), () -> {
                view.game.actions.unitHasActed(view, this, new SkipAction());
                view.game.actions.goToNextUnit(view);
            }));

            // Skip Inventory Action
            node.add(new ActionNode(view, "Skip until inventory is full", Optional.empty(),
                    !view.game.actions.hasUnitActed(this), () -> {
                        view.game.actions.unitHasActed(view, this, new SkipInventoryAction(this.haul));
                        view.game.actions.goToNextUnit(view);
                    }));
        }
        for (Ability a : this.abilities.getActives()) {
            node.add(a.getMenuContent(view, p));
        }
        for (Ability a : this.abilities.getPassives()) {
            node.add(a.getMenuContent(view, p));
        }

        // Items section
        if (this.getLeader().map((Player p1) -> p1.isHumanPlayer()).orElse(false)) {
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
}
