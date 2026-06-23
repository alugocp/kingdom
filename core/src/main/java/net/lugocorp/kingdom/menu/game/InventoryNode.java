package net.lugocorp.kingdom.menu.game;
import net.lugocorp.kingdom.builtin.Events;
import net.lugocorp.kingdom.color.ColorScheme;
import net.lugocorp.kingdom.engine.AudioVideo;
import net.lugocorp.kingdom.engine.fonts.FontParam;
import net.lugocorp.kingdom.engine.render.Drawable;
import net.lugocorp.kingdom.engine.shaders.ElementShader;
import net.lugocorp.kingdom.game.model.Building;
import net.lugocorp.kingdom.game.model.Item;
import net.lugocorp.kingdom.game.model.Tile;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.game.player.Player;
import net.lugocorp.kingdom.game.properties.Inventory;
import net.lugocorp.kingdom.game.properties.InventoryType;
import net.lugocorp.kingdom.math.Coords;
import net.lugocorp.kingdom.math.Hexagons;
import net.lugocorp.kingdom.math.Point;
import net.lugocorp.kingdom.math.Rect;
import net.lugocorp.kingdom.menu.Menu;
import net.lugocorp.kingdom.menu.MenuNode;
import net.lugocorp.kingdom.menu.MenuPopup;
import net.lugocorp.kingdom.menu.structure.ListNode;
import net.lugocorp.kingdom.menu.text.BadgeNode;
import net.lugocorp.kingdom.menu.text.ButtonNode;
import net.lugocorp.kingdom.menu.text.SubheaderNode;
import net.lugocorp.kingdom.menu.text.TextNode;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.Tuple;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Matrix4;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * A node that displays some Inventory
 */
public class InventoryNode implements MenuNode {
    private static final int MARGIN = 2;
    public static final int SIDE = 50;
    private final MenuPopup popup = new MenuPopup();
    private final Optional<Inventory> equipped;
    private final MenuNode canEquipMenu;
    private final MenuNode canHaulMenu;
    private final Inventory items;
    private final GameView view;
    private final int x;
    private final int y;
    private Optional<Tuple<Item, MenuNode>> cachedHoverMenu = Optional.empty();
    private Optional<Integer> hoveredIndex = Optional.empty();
    private boolean controlsActive = false;
    private Menu menu = null;
    private int cols;
    private int rows;

    public InventoryNode(GameView view, Inventory items, Optional<Inventory> equipped, int x, int y) {
        this.equipped = equipped;
        this.items = items;
        this.view = view;
        this.x = x;
        this.y = y;
        this.canEquipMenu = new ListNode().add(new TextNode(view.av, "You can equip an item here"));
        this.canHaulMenu = new ListNode().add(new TextNode(view.av, "You can haul an item here"));
    }

    /**
     * Returns true if the given Item has an effect when consumed
     */
    private boolean itemIsConsumed(GameView view, Item item) {
        return view.game.events.item.hasEventHandler(item.getStratifier(), Events.ItemConsumedEvent.class);
    }

    /**
     * Returns true if the Unit on the currently open Tile can take another Item in
     * the specified Inventory. The "unit" parameter will be empty if you're
     * checking against a Building or free Item. Otherwise, it will contain the Unit
     * (if any) on the Tile that you're trying to gift the Item to.
     */
    private boolean canUnitTakeItem(InventoryType type, Optional<Unit> unit) {
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
     * The Unit on the currently open Tile picks up or equips the specified Item
     */
    private void unitTakesItem(InventoryType type, Item item) {
        Unit unit = this.view.game.world.getTile(this.x, this.y).flatMap((Tile t) -> t.unit).get();
        if (type == InventoryType.EQUIP) {
            this.items.transfer(unit.equipped, item);
        }
        if (type == InventoryType.HAUL) {
            this.items.transfer(unit.haul, item);
        }
        this.view.hud.bot.tileMenu.refresh();
    }

    /**
     * Returns true if the given Item can be consumed
     */
    private boolean canUnitConsumeItem(Item item) {
        Optional<Unit> unit = this.view.game.world.getTile(this.x, this.y).flatMap((Tile t) -> t.unit);
        return unit.isPresent()
                && this.view.game.events.item.hasEventHandler(item.getStratifier(), Events.ItemConsumedEvent.class);
    }

    /**
     * The Unit on the currently open Tile consumes the specified Item
     */
    private void unitConsumesItem(Item item) {
        Unit unit = this.view.game.world.getTile(this.x, this.y).flatMap((Tile t) -> t.unit).get();
        Events.ItemConsumedEvent event = new Events.ItemConsumedEvent(item, unit);
        item.handleEvent(this.view, event).execute();
        if (event.consumed) {
            this.items.remove(item);
        }
        this.view.hud.bot.tileMenu.refresh();
    }

    /**
     * Returns a list of Points with Units there that can receive an Item
     */
    private Set<Point> getGiftRecipients() {
        Set<Point> points = Hexagons.getNeighbors(new Point(this.x, this.y), 1);
        Set<Point> valid = new HashSet<>();
        for (Point p : points) {
            Optional<Tile> tile = this.view.game.world.getTile(p);
            if (tile.isPresent() && tile.get().unit.isPresent()
                    && (this.canUnitTakeItem(InventoryType.HAUL, tile.get().unit))) {
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
        this.view.hud.bot.tileMenu.refresh();
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
        this.view.hud.bot.tileMenu.refresh();
    }

    /**
     * Returns the total number of Items represented in this InventoryNode
     */
    private int getTotalItemsSize() {
        return this.items.getMax() + this.equipped.map((Inventory i) -> i.getMax()).orElse(0);
    }

    /**
     * Returns the Item at the given index across either Inventory
     */
    private Optional<Item> getItemFromTotalIndex(int totalIndex) {
        int index = totalIndex;
        if (this.equipped.isPresent()) {
            final int max = this.equipped.get().getMax();
            if (index < max) {
                if (index >= 0 && index < this.equipped.get().getSize()) {
                    return Optional.of(this.equipped.get().get(index));
                }
                return Optional.empty();
            }
            index -= max;
        }
        if (index >= 0 && index < this.items.getSize()) {
            return Optional.of(this.items.get(index));
        }
        return Optional.empty();
    }

    /**
     * Returns which Inventory the given total index refers to
     */
    private Inventory getInventoryByTotalIndex(int i) {
        return this.equipped.map((Inventory inv) -> i < inv.getMax() ? inv : this.items).orElse(this.items);
    }

    /**
     * Returns the total index of the Item that Point p is hovering over
     */
    private int getHoveredItemIndex(Rect bounds, Point p) {
        if (p.x < bounds.x || p.y < bounds.y) {
            return -1;
        }
        final int x = (p.x - bounds.x) / InventoryNode.SIDE;
        final int y = (p.y - bounds.y) / InventoryNode.SIDE;
        if (x > this.cols) {
            return -1;
        }
        return (this.cols * y) + x;
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
        this.rows = this.items.getMax() == 0 ? 0 : (int) Math.ceil(this.getTotalItemsSize() / (float) this.cols);
        this.popup.setMenu(menu);
        this.menu = menu;
    }

    /** {@inheritdoc} */
    @Override
    public void draw(AudioVideo av, Rect bounds) {
        final int max = this.getTotalItemsSize();
        if (max == 0) {
            return;
        }
        for (int a = 0; a < this.rows; a++) {
            final Rect flip = Coords.screen.flip(bounds.x, bounds.y + a * (InventoryNode.SIDE + InventoryNode.MARGIN),
                    bounds.w, InventoryNode.SIDE + InventoryNode.MARGIN);
            for (int b = 0; b < this.cols; b++) {
                final int index = (a * this.cols) + b;
                if (index >= max) {
                    break;
                }
                final Drawable icon = new Drawable(av.loaders.sprites, "placeholder");
                final Optional<Item> target = this.getItemFromTotalIndex(index);
                final InventoryType type = this.getInventoryByTotalIndex(index).type;
                target.flatMap((Item item) -> item.icon).ifPresent((String s) -> icon.setSprite(s));
                av.special.begin();
                av.shaders.element.setMode(this.hoveredIndex.map((Integer hovered) -> index == hovered).orElse(false)
                        ? ElementShader.BRIGHT_MODE
                        : ElementShader.DEFAULT_MODE);
                target.flatMap((Item i) -> i.getRecolor()).ifPresent((Matrix4 m) -> av.shaders.element.recolor(m));
                icon.render(av.special, flip.x + b * (InventoryNode.SIDE + InventoryNode.MARGIN), flip.y);
                av.special.end();
                av.shaders.element.originalColor();
                av.shaders.element.setMode(ElementShader.DEFAULT_MODE);
                if (type == InventoryType.EQUIP) {
                    av.sprites.begin();
                    icon.setSprite("equip frame");
                    icon.render(av.sprites, flip.x + b * (InventoryNode.SIDE + InventoryNode.MARGIN), flip.y);
                    av.sprites.end();
                }
            }
        }
    }

    /** {@inheritdoc} */
    @Override
    public void click(Rect bounds, Point p) {
        final int i = this.getHoveredItemIndex(bounds, p);
        final Optional<Item> target = this.getItemFromTotalIndex(i);
        final InventoryType type = this.getInventoryByTotalIndex(i).type;

        // Return early if no Item was really clicked
        if (!target.isPresent()) {
            this.controlsActive = false;
            return;
        }
        this.controlsActive = true;
        this.popup.close();

        // Set up the controls menu
        final Item item = target.get();
        final ListNode root = new ListNode().add(new SubheaderNode(this.view.av, item.name))
                .add(new TextNode(this.view.av, item.desc));
        boolean hasActions = false;

        // Equip / pick up / drop options (only if the human Player occupies this space)
        final boolean actions = this.view.game.world.getTile(this.x, this.y).flatMap((Tile t1) -> t1.unit)
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
                    hasActions = true;
                }
                if (this.canUnitTakeItem(InventoryType.HAUL, Optional.empty())) {
                    root.add(new ButtonNode(this.view.av, "Give to unit",
                            () -> this.unitTakesItem(InventoryType.HAUL, item)).setNoise("sfx/item-exchange"));
                    hasActions = true;
                }
            } else {
                // Unit actions for this Item
                if (this.items.type == InventoryType.HAUL && this.canUnitTakeItem(InventoryType.EQUIP, Optional.empty())
                        && !this.itemIsConsumed(this.view, item)) {
                    root.add(new ButtonNode(this.view.av, "Equip", () -> this.unitTakesItem(InventoryType.EQUIP, item))
                            .setNoise("sfx/item-check"));
                    hasActions = true;
                }
                if (this.canUnitConsumeItem(item)) {
                    root.add(new ButtonNode(this.view.av, "Consume", () -> this.unitConsumesItem(item))
                            .setNoise("sfx/item-check"));
                    hasActions = true;
                }
                if (this.items.type == InventoryType.HAUL) {
                    root.add(new ButtonNode(this.view.av, "Give",
                            () -> this.view.selector.select(this.getGiftRecipients(),
                                    "No nearby units can receive this gift",
                                    (Point p1) -> this.giftItemToUnit(p1, item)))
                            .setNoise("sfx/item-exchange"));
                    hasActions = true;
                }
                if (this.items.type == InventoryType.HAUL && this.canBuildingTakeItem()) {
                    root.add(new ButtonNode(this.view.av, "Put in vault", () -> this.buildingTakesItem(item))
                            .setNoise("sfx/item-check"));
                    hasActions = true;
                }
            }
        }
        if (!hasActions) {
            root.add(new TextNode(this.view.av, "No actions available"));
        }
        this.menu.setMiniMenu(root, p.x, p.y);
    }

    /** {@inheritdoc} */
    @Override
    public void unclick() {
        if (this.controlsActive) {
            this.controlsActive = false;
            this.menu.closeMiniMenu();
        }
    }

    /** {@inheritdoc} */
    @Override
    public void mouseMoved(Rect bounds, Point prev, Point curr) {
        // Disable the mouse hover logic if the controls mini menu is active
        if (this.controlsActive) {
            return;
        }

        // Set mini menu for the selected item
        final int i = this.getHoveredItemIndex(bounds, curr);
        final Optional<Item> target = this.getItemFromTotalIndex(i);
        final InventoryType type = this.getInventoryByTotalIndex(i).type;
        final int max = this.getTotalItemsSize();
        if (target.isPresent()) {
            final Item item = target.get();

            // Only reconstruct the mini Menu root if it's not already cached
            if (this.cachedHoverMenu.map((Tuple<Item, MenuNode> tuple) -> tuple.a != item).orElse(true)) {
                final ListNode root = new ListNode().add(new SubheaderNode(this.view.av, item.name))
                        .add(new TextNode(this.view.av, item.tags.pretty("and", "item")) {
                            /** {@inheritdoc} */
                            @Override
                            protected BitmapFont getFont() {
                                return this.av.fonts.getFont(new FontParam().setColor(ColorScheme.GOLD.color));
                            }
                        })
                        .add(new BadgeNode(this.view.av, item.rarity.color, ColorScheme.WHITE.hex,
                                item.rarity.toString()))
                        .add(new TextNode(this.view.av, String.format("%s (click for options)", item.desc)));
                this.cachedHoverMenu = Optional.of(new Tuple<Item, MenuNode>(item, root));
            }
            this.popup.update(bounds, curr, this.cachedHoverMenu.get().b);
            this.hoveredIndex = Optional.of(i);
        } else if (type == InventoryType.EQUIP) {
            this.popup.update(bounds, curr, this.canEquipMenu);
            this.hoveredIndex = Optional.of(i);
        } else if (i < max) {
            this.popup.update(bounds, curr, this.canHaulMenu);
            this.hoveredIndex = Optional.of(i);
        } else {
            this.hoveredIndex = Optional.empty();
            this.popup.close();
        }
    }
}
