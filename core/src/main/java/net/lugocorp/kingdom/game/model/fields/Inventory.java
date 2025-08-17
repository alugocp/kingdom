package net.lugocorp.kingdom.game.model.fields;
import net.lugocorp.kingdom.game.model.Item;
import net.lugocorp.kingdom.ui.menu.InventoryNode;
import net.lugocorp.kingdom.ui.menu.MenuNode;
import net.lugocorp.kingdom.ui.menu.MenuSubject;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.math.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Represents a list of items with a max size
 */
public class Inventory implements MenuSubject {
    private final List<Item> items;
    public final int type;
    private int max;

    public Inventory(int type, int max) {
        this.items = new ArrayList<Item>(max);
        this.type = type;
        this.max = max;
    }

    /**
     * This should only be used in conjunction with Kryo rehydration
     */
    public Inventory() {
        this.items = null;
        this.type = 0;
        this.max = 0;
    }

    /**
     * Return current number of items in this Inventory
     */
    public int getSize() {
        return this.items.size();
    }

    /**
     * Return max number of items in this Inventory
     */
    public int getMax() {
        return this.max;
    }

    /**
     * Sets the maximum size for this Inventory (will destroy overflow Items)
     */
    public void setMax(int n) {
        while (this.items.size() > n) {
            this.remove(this.items.get(this.items.size() - 1));
        }
        this.max = n;
    }

    /**
     * Returns true if this Inventory cannot fit any more Items
     */
    public boolean isFull() {
        return this.getSize() >= this.getMax();
    }

    /**
     * Adds an Item to this Inventory
     */
    public void add(Item item) {
        this.items.add(item);
    }

    /**
     * Removes an Item from this Inventory
     */
    public void remove(Item item) {
        this.items.remove(item);
    }

    /**
     * Returns the Item at the given index in this Inventory
     */
    public Item get(int index) {
        return this.items.get(index);
    }

    /**
     * Moves the specified Item from this Inventory to another one
     */
    public void transfer(Inventory inventory, Item item) {
        if (this.items.remove(item)) {
            inventory.add(item);
        }
    }

    /**
     * Returns the sum total of every Item's value in gold
     */
    public int getTotalGold() {
        int sum = 0;
        for (Item i : this.items) {
            sum += i.gold;
        }
        return sum;
    }

    /** {@inheritdoc} */
    @Override
    public MenuNode getMenuContent(GameView view, Optional<Point> p) {
        if (!p.isPresent()) {
            throw new RuntimeException("Cannot display inventories of unspawned units/buildings");
        }
        return new InventoryNode(view, this, p.get().x, p.get().y);
    }

    /**
     * Nested class enum representing the different types of inventory
     */
    public static class InventoryType {
        public static final int BUILDING = 3;
        public static final int EQUIP = 2;
        public static final int HAUL = 1;
        public static final int FREE = 0;
    }
}
