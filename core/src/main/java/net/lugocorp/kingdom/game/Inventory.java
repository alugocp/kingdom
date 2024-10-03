package net.lugocorp.kingdom.game;
import java.util.ArrayList;
import java.util.List;
import net.lugocorp.kingdom.engine.GameGraphics;
import net.lugocorp.kingdom.menu.InventoryNode;
import net.lugocorp.kingdom.menu.MenuNode;

/**
 * Represents a list of items with a max size
 */
public class Inventory {
    private final List<Item> items;
    private final int max;

    Inventory(int max) {
        this.items = new ArrayList<Item>(max);
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
     * Returns some nodes for a Menu
     */
    public MenuNode getMenuContent(GameGraphics graphics) {
        return new InventoryNode(graphics.loaders.sprites, this);
    }
}
