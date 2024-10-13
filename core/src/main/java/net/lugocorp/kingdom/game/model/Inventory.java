package net.lugocorp.kingdom.game.model;
import net.lugocorp.kingdom.ui.menu.InventoryNode;
import net.lugocorp.kingdom.ui.menu.MenuNode;
import net.lugocorp.kingdom.ui.menu.MenuSubject;
import net.lugocorp.kingdom.ui.views.GameView;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a list of items with a max size
 */
public class Inventory implements MenuSubject {
    private final List<Item> items;
    private final int max;
    public final int type;

    public Inventory(int type, int max) {
        this.items = new ArrayList<Item>(max);
        this.type = type;
        this.max = max;
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
    public MenuNode getMenuContent(GameView view, int x, int y) {
        return new InventoryNode(view, this, x, y);
    }

    /**
     * Nested class enum representing the different types of inventory
     */
    public static class InventoryType {
        public static final int EQUIP = 2;
        public static final int HAUL = 1;
        public static final int FREE = 0;
    }
}
