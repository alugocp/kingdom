package net.lugocorp.kingdom.ui.menu;
import net.lugocorp.kingdom.builtin.Events;
import net.lugocorp.kingdom.engine.AudioVideo;
import net.lugocorp.kingdom.engine.render.Drawable;
import net.lugocorp.kingdom.game.model.Building;
import net.lugocorp.kingdom.game.model.Item;
import net.lugocorp.kingdom.game.model.Tile;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.game.player.Player;
import net.lugocorp.kingdom.game.properties.Inventory;
import net.lugocorp.kingdom.game.properties.Inventory.InventoryType;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.math.Coords;
import net.lugocorp.kingdom.utils.math.Hexagons;
import net.lugocorp.kingdom.utils.math.Point;
import net.lugocorp.kingdom.utils.math.Rect;
import com.badlogic.gdx.graphics.Color;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * A node that displays some Inventory
 */
public class InventoryNode implements MenuNode {
    private static final int MARGIN = 2;
    public static final int SIDE = 50;
    private final Inventory items;
    private final GameView view;
    private final int x;
    private final int y;
    private Menu menu = null;
    private int cols;
    private int rows;

    public InventoryNode(GameView view, Inventory items, int x, int y) {
        this.items = items;
        this.view = view;
        this.x = x;
        this.y = y;
    }

    /** {@inheritdoc} */
    @Override
    public int getHeight() {
        return this.rows * (InventoryNode.SIDE + InventoryNode.MARGIN);
    }

    /** {@inheritdoc} */
    @Override
    public void pack(Menu menu, int width) {
        this.cols = (int) (width / (InventoryNode.SIDE + InventoryNode.MARGIN));
        this.rows = this.items.getMax() == 0 ? 0 : (int) Math.ceil(this.items.getMax() / (float) this.cols);
        this.menu = menu;
    }

    /** {@inheritdoc} */
    @Override
    public void draw(AudioVideo av, Rect bounds) {
        if (this.items.getMax() == 0) {
            return;
        }
        av.sprites.begin();
        av.sprites.setColor(Color.WHITE);
        for (int a = 0; a < this.rows; a++) {
            Rect flip = Coords.screen.flip(bounds.x, bounds.y + a * (InventoryNode.SIDE + InventoryNode.MARGIN),
                    bounds.w, InventoryNode.SIDE + InventoryNode.MARGIN);
            for (int b = 0; b < this.cols; b++) {
                int index = (a * this.cols) + b;
                if (index >= this.items.getMax()) {
                    break;
                }
                Drawable icon = new Drawable(av.loaders.sprites, "placeholder");
                if (index < this.items.getSize()) {
                    Item item = this.items.get(index);
                    if (item.icon.isPresent()) {
                        icon.setSprite(item.icon.get());
                    }
                }
                icon.render(av.sprites, flip.x + b * (InventoryNode.SIDE + InventoryNode.MARGIN), flip.y);
            }
        }
        av.sprites.end();
    }

    /** {@inheritdoc} */
    @Override
    public void click(Rect bounds, Point p) {
        int x = (p.x - bounds.x) / InventoryNode.SIDE;
        int y = (p.y - bounds.y) / InventoryNode.SIDE;
        int i = (cols * y) + x;

        // No item in particular was clicked, nothing to do here
        if (i >= this.items.getSize()) {
            return;
        }

        // Set mini menu for the selected item
        final Item item = this.items.get(i);
        ListNode root = new ListNode().add(new HeaderNode(this.view.av, item.name))
                .add(new BadgeNode(this.view.av, item.rarity.color, 0xffffff, item.rarity.toString()))
                .add(new TextNode(this.view.av, item.desc));

        // Equip / pick up / drop options (only if the human Player occupies this space)
        boolean actions = this.view.game.world.getTile(this.x, this.y).flatMap((Tile t1) -> t1.unit)
                .flatMap((Unit u1) -> u1.getLeader())
                .map((Player p1) -> p1.isHumanPlayer() && this.view.game.mechanics.turns.canHumanPlayerAct())
                .orElse(false);
        if (actions) {
            if (this.items.type == InventoryType.BUILDING) {
                // Building actions for this Item
                if (this.canUnitTakeItem(InventoryType.EQUIP, Optional.empty())
                        && !this.itemIsConsumed(this.view, item)) {
                    root.add(new ButtonNode(this.view.av, "Equip onto unit",
                            () -> this.unitTakesItem(InventoryType.EQUIP, item)).setNoise("sfx/item-exchange"));
                }
                if (this.canUnitTakeItem(InventoryType.HAUL, Optional.empty())) {
                    root.add(new ButtonNode(this.view.av, "Give to unit",
                            () -> this.unitTakesItem(InventoryType.HAUL, item)).setNoise("sfx/item-exchange"));
                }
            } else {
                // Unit actions for this Item
                if ((this.items.type == InventoryType.FREE || this.items.type == InventoryType.HAUL)
                        && this.canUnitTakeItem(InventoryType.EQUIP, Optional.empty())
                        && !this.itemIsConsumed(this.view, item)) {
                    root.add(new ButtonNode(this.view.av, "Equip", () -> this.unitTakesItem(InventoryType.EQUIP, item))
                            .setNoise("sfx/item-check"));
                }
                if (this.items.type == InventoryType.FREE
                        && this.canUnitTakeItem(InventoryType.HAUL, Optional.empty())) {
                    root.add(new ButtonNode(this.view.av, "Pick up", () -> this.unitTakesItem(InventoryType.HAUL, item))
                            .setNoise("sfx/item-check"));
                }
                if (this.canUnitConsumeItem(item)) {
                    root.add(new ButtonNode(this.view.av, "Consume", () -> this.unitConsumesItem(item))
                            .setNoise("sfx/item-check"));
                }
                if (this.items.type == InventoryType.HAUL && this.canUnitDropItem()) {
                    root.add(new ButtonNode(this.view.av, "Drop", () -> this.unitDropsItem(item))
                            .setNoise("sfx/item-check"));
                }
                if ((this.items.type == InventoryType.FREE
                        && this.canUnitTakeItem(InventoryType.HAUL, Optional.empty()))
                        || this.items.type == InventoryType.HAUL) {
                    root.add(new ButtonNode(this.view.av, "Give",
                            () -> this.view.selector.select(this.getGiftRecipients(),
                                    "No nearby units can receive this gift",
                                    (Point p1) -> this.giftItemToUnit(p1, item)))
                            .setNoise("sfx/item-exchange"));
                }
                if ((this.items.type == InventoryType.FREE || this.items.type == InventoryType.HAUL)
                        && this.canBuildingTakeItem()) {
                    root.add(new ButtonNode(this.view.av, "Put in vault", () -> this.buildingTakesItem(item))
                            .setNoise("sfx/item-check"));
                }
            }
        }
        this.menu.setMiniMenu(root, p.x, p.y);
    }

    /**
     * Returns true if the given Item has an effect when consumed
     */
    public boolean itemIsConsumed(GameView view, Item item) {
        return view.game.events.item.hasEventHandler(item.getStratifier(), "ItemConsumedEvent");
    }

    /**
     * Returns true if the Unit on the currently open Tile can take another Item in
     * the specified Inventory. The "unit" parameter will be empty if you're
     * checking against a Building or free Item. Otherwise, it will contain the Unit
     * (if any) on the Tile that you're trying to gift the Item to.
     */
    private boolean canUnitTakeItem(int type, Optional<Unit> unit) {
        Optional<Unit> focal = this.view.game.world.getTile(this.x, this.y).flatMap((Tile t) -> t.unit);
        if (unit.isPresent()) {
            if (!unit.get().getLeader().equals(focal.get().getLeader())) {
                return false;
            }
        } else {
            unit = focal;
            if (!unit.isPresent()) {
                return false;
            }
        }
        return (type == InventoryType.EQUIP && !unit.get().equipped.isFull())
                || (type == InventoryType.HAUL && !unit.get().haul.isFull());
    }

    /**
     * Returns true if the given Unit can be recruited using an Item
     */
    private boolean canUnitBeRecruitedWithItem(Unit unit) {
        return unit.isFreeRadical() && !unit.haul.isFull();
    }

    /**
     * The Unit on the currently open Tile picks up or equips the specified Item
     */
    private void unitTakesItem(int type, Item item) {
        Unit unit = this.view.game.world.getTile(this.x, this.y).flatMap((Tile t) -> t.unit).get();
        if (type == InventoryType.EQUIP) {
            this.items.transfer(unit.equipped, item);
        }
        if (type == InventoryType.HAUL) {
            this.items.transfer(unit.haul, item);
        }
        this.view.menu.refresh(false);
    }

    /**
     * Returns true if the Unit on the currently open Tile can drop an item here
     */
    private boolean canUnitDropItem() {
        Optional<Tile> tile = this.view.game.world.getTile(this.x, this.y);
        return tile.isPresent() && !tile.get().items.isFull();
    }

    /**
     * The Unit on the currently open Tile drops the specified Item
     */
    private void unitDropsItem(Item item) {
        Tile tile = this.view.game.world.getTile(this.x, this.y).get();
        this.items.transfer(tile.items, item);
        this.view.menu.refresh(false);
    }

    /**
     * Returns true if the given Item can be consumed
     */
    private boolean canUnitConsumeItem(Item item) {
        Optional<Unit> unit = this.view.game.world.getTile(this.x, this.y).flatMap((Tile t) -> t.unit);
        return unit.isPresent()
                && this.view.game.events.item.hasEventHandler(item.getStratifier(), "ItemConsumedEvent");
    }

    /**
     * The Unit on the currently open Tile consumes the specified Item
     */
    private void unitConsumesItem(Item item) {
        Unit unit = this.view.game.world.getTile(this.x, this.y).flatMap((Tile t) -> t.unit).get();
        Events.ItemConsumedEvent event = new Events.ItemConsumedEvent(item, unit);
        item.handleEvent(this.view, event);
        if (event.consumed) {
            this.items.remove(item);
        }
        this.view.menu.refresh(false);
    }

    /**
     * Returns a list of Points with Units there that can receive an Item
     */
    private Set<Point> getGiftRecipients() {
        Set<Point> points = Hexagons.getNeighbors(new Point(this.x, this.y), 1);
        Set<Point> valid = new HashSet<>();
        for (Point p : points) {
            Optional<Tile> tile = this.view.game.world.getTile(p);
            if (tile.isPresent() && tile.get().unit.isPresent() && (this.canUnitTakeItem(InventoryType.HAUL,
                    tile.get().unit)
                    || (tile.get().unit.isPresent() && this.canUnitBeRecruitedWithItem(tile.get().unit.get())))) {
                valid.add(p);
            }
        }
        return valid;
    }

    /**
     * Transfers the specified Item to the Unit at the given location
     */
    private void giftItemToUnit(Point p, Item item) {
        Player leader = this.view.game.world.getTile(this.x, this.y).flatMap((Tile t) -> t.unit)
                .flatMap((Unit u) -> u.getLeader()).get();
        Unit unit = this.view.game.world.getTile(p.x, p.y).get().unit.get();
        this.items.transfer(unit.haul, item);
        if (unit.isFreeRadical()) {
            unit.getRecruited(this.view, leader);
        }
        this.view.menu.refresh(false);
    }

    /**
     * Returns true if the Building on the currently open Tile can take another Item
     * in its Inventory.
     */
    private boolean canBuildingTakeItem() {
        Optional<Building> building = this.view.game.world.getTile(this.x, this.y).flatMap((Tile t) -> t.building);
        Optional<Unit> unit = this.view.game.world.getTile(this.x, this.y).flatMap((Tile t) -> t.unit);
        return unit.isPresent()
                && building.flatMap((Building b) -> b.items).map((Inventory i) -> !i.isFull()).orElse(false);
    }

    /**
     * The Building on the currently open Tile picks up or equips the specified Item
     */
    private void buildingTakesItem(Item item) {
        Building building = this.view.game.world.getTile(this.x, this.y).flatMap((Tile t) -> t.building).get();
        this.items.transfer(building.items.get(), item);
        this.view.menu.refresh(false);
    }
}
