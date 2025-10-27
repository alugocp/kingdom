package net.lugocorp.kingdom.game.mechanics;
import net.lugocorp.kingdom.game.Game;
import net.lugocorp.kingdom.game.model.Item;
import net.lugocorp.kingdom.game.properties.Rarity;
import net.lugocorp.kingdom.utils.Lambda;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class selects random Items whenever the Game requires it
 */
public class LootTable {
    // TODO optimize this using SQLite
    private final Map<Rarity, List<String>> itemsByRarity = new HashMap<>();

    /**
     * Sets up the data stored in this class
     */
    public void init(Game game) {
        // Populate the Map with Lists
        for (Rarity r : Rarity.values()) {
            this.itemsByRarity.put(r, new ArrayList<>());
        }

        // Populate the Lists with Item names by their Rarity
        for (String s : game.events.item.getStratifiers()) {
            Item i = game.generator.item(s);
            this.itemsByRarity.get(i.rarity).add(s);
        }
    }

    /**
     * Returns a random Item while accounting for Rarity
     */
    public Item drop(Game game) {
        int roll = (int) Math.floor(Math.random() * 100);
        for (Rarity r : Rarity.values()) {
            if (roll < r.chance) {
                String name = Lambda.random(this.itemsByRarity.get(r));
                return game.generator.item(name);
            }
            roll -= r.chance;
        }

        // Should never reach this code
        throw new RuntimeException("No Item was dropped");
    }
}
