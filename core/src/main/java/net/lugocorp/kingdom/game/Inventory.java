package net.lugocorp.kingdom.game;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a list of items with a max size
 */
class Inventory {
    private final List<Item> items;
    private final int max;

    Inventory(int max) {
        this.items = new ArrayList<Item>(max);
        this.max = max;
    }

    /**
     * Return current number of items in this Inventory
     */
    int getSize() {
        return this.items.size();
    }

    /**
     * Return max number of items in this Inventory
     */
    int getMax() {
        return this.max;
    }

    /**
     * Adds an Item to this Inventory
     */
    void add(Item item) {
        this.items.add(item);
    }
}
