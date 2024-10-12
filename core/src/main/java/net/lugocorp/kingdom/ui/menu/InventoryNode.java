package net.lugocorp.kingdom.ui.menu;
import net.lugocorp.kingdom.engine.Graphics;
import net.lugocorp.kingdom.game.model.Inventory;
import net.lugocorp.kingdom.game.model.Inventory.InventoryType;
import net.lugocorp.kingdom.game.model.Item;
import net.lugocorp.kingdom.game.model.Player;
import net.lugocorp.kingdom.game.model.Tile;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.math.Coords;
import net.lugocorp.kingdom.utils.math.Hexagons;
import net.lugocorp.kingdom.utils.math.Point;
import net.lugocorp.kingdom.utils.math.Rect;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
    public void pack(int width) {
        this.cols = (int) (width / (InventoryNode.SIDE + InventoryNode.MARGIN));
        this.rows = this.items.getMax() == 0 ? 0 : (int) Math.ceil(this.items.getMax() / (float) this.cols);
    }

    /** {@inheritdoc} */
    @Override
    public void draw(Graphics graphics, Rect bounds) {
        if (this.items.getMax() == 0) {
            return;
        }
        graphics.sprites.begin();
        graphics.sprites.setColor(Color.WHITE);
        for (int a = 0; a < this.rows; a++) {
            Rect flip = Coords.screen.flip(bounds.x, bounds.y + a * (InventoryNode.SIDE + InventoryNode.MARGIN),
                    bounds.w, InventoryNode.SIDE + InventoryNode.MARGIN);
            for (int b = 0; b < this.cols; b++) {
                int index = (a * this.cols) + b;
                if (index >= this.items.getMax()) {
                    break;
                }
                TextureRegion icon = this.view.game.graphics.loaders.sprites.get("placeholder");
                if (index < this.items.getSize()) {
                    Item item = this.items.get(index);
                    if (item.icon.isPresent()) {
                        icon = this.view.game.graphics.loaders.sprites.get(item.icon.get());
                    }
                }
                graphics.sprites.draw(icon, flip.x + b * (InventoryNode.SIDE + InventoryNode.MARGIN), flip.y);
            }
        }
        graphics.sprites.end();
    }

    /** {@inheritdoc} */
    @Override
    public void click(Menu menu, Rect bounds, Point p) {
        int x = (p.x - bounds.x) / InventoryNode.SIDE;
        int y = (p.y - bounds.y) / InventoryNode.SIDE;
        int i = (cols * y) + x;

        // No item in particular was clicked, nothing to do here
        if (i >= this.items.getSize()) {
            return;
        }

        // Set mini menu for the selected item
        final Item item = this.items.get(i);
        ListNode root = new ListNode().add(new HeaderNode(this.view.game.graphics, item.name))
                .add(new TextNode(this.view.game.graphics, item.desc));

        // Equip / pick up / drop options (only if the human Player occupies this space)
        boolean actions = this.view.game.world.getTile(this.x, this.y).flatMap((Tile t1) -> t1.unit)
                .flatMap((Unit u1) -> u1.leader)
                .map((Player p1) -> p1.isHumanPlayer() && this.view.game.canHumanPlayerAct()).orElse(false);
        if (actions) {
            if ((this.items.type == InventoryType.FREE || this.items.type == InventoryType.HAUL)
                    && this.canUnitTakeItem(InventoryType.EQUIP, Optional.empty())) {
                root.add(new ButtonNode(this.view.game.graphics, "Equip",
                        () -> this.unitTakesItem(InventoryType.EQUIP, item)));
            }
            if (this.items.type == InventoryType.FREE && this.canUnitTakeItem(InventoryType.HAUL, Optional.empty())) {
                root.add(new ButtonNode(this.view.game.graphics, "Pick up",
                        () -> this.unitTakesItem(InventoryType.HAUL, item)));
            }
            if (this.items.type == InventoryType.HAUL && this.canUnitDropItem()) {
                root.add(new ButtonNode(this.view.game.graphics, "Drop", () -> this.unitDropsItem(item)));
            }
            if ((this.items.type == InventoryType.FREE && this.canUnitTakeItem(InventoryType.HAUL, Optional.empty()))
                    || this.items.type == InventoryType.HAUL) {
                root.add(new ButtonNode(this.view.game.graphics, "Give",
                        () -> this.view.selectTiles(this.getGiftRecipients(), "No nearby units can receive this gift",
                                (Point p1) -> this.giftItemToUnit(p1, item))));
            }
        }
        menu.setMiniMenu(root, p.x, p.y);
    }

    /**
     * Returns true if the Unit on the currently open Tile can take another Item in
     * the specified Inventory. The "unit" parameter will be empty if you're
     * checking against a free Item. Otherwise, it will contain the Unit (if any) on
     * the tile that you're trying to gift the Item to.
     */
    private boolean canUnitTakeItem(int type, Optional<Unit> unit) {
        Optional<Unit> focal = this.view.game.world.getTile(this.x, this.y).flatMap((Tile t) -> t.unit);
        if (unit.isPresent()) {
            if (!unit.get().leader.equals(focal.get().leader)) {
                return false;
            }
        } else {
            unit = focal;
        }
        return (type == InventoryType.EQUIP && !unit.get().equipped.isFull())
                || (type == InventoryType.HAUL && !unit.get().haul.isFull());
    }

    /**
     * The Unit on the currently open tile picks up or equips the specified Item
     */
    private void unitTakesItem(int type, Item item) {
        Unit unit = this.view.game.world.getTile(this.x, this.y).flatMap((Tile t) -> t.unit).get();
        if (type == InventoryType.EQUIP) {
            this.items.transfer(unit.equipped, item);
        }
        if (type == InventoryType.HAUL) {
            this.items.transfer(unit.haul, item);
        }
        this.view.refreshMenu(false);
    }

    /**
     * Returns true if the Unit on the currently open Tile can drop an item here
     */
    private boolean canUnitDropItem() {
        Optional<Tile> tile = this.view.game.world.getTile(this.x, this.y);
        return tile.isPresent() && !tile.get().items.isFull();
    }

    /**
     * The Unit on the currently open tile drops the specified Item
     */
    private void unitDropsItem(Item item) {
        Tile tile = this.view.game.world.getTile(this.x, this.y).get();
        this.items.transfer(tile.items, item);
        this.view.refreshMenu(false);
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
                    && this.canUnitTakeItem(InventoryType.HAUL, tile.get().unit)) {
                valid.add(p);
            }
        }
        return valid;
    }

    /**
     * Transfers the specified Item to the Unit at the given location
     */
    private void giftItemToUnit(Point p, Item item) {
        Unit unit = this.view.game.world.getTile(p.x, p.y).get().unit.get();
        this.items.transfer(unit.haul, item);
        this.view.refreshMenu(false);
    }
}
