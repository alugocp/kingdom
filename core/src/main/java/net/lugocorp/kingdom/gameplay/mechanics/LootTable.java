package net.lugocorp.kingdom.gameplay.mechanics;
import net.lugocorp.kingdom.game.Game;
import net.lugocorp.kingdom.game.model.Item;
import net.lugocorp.kingdom.game.properties.Rarity;
import net.lugocorp.kingdom.utils.Lambda;
import net.lugocorp.kingdom.utils.Tuple;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class selects random Items whenever the Game requires it
 */
public class LootTable {
    // TODO optimize this using SQLite
    private final Map<Tuple<String, Rarity>, List<String>> itemsByTagAndRarity = new HashMap<>();
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
            final Item i = game.generator.item(s);
            this.itemsByRarity.get(i.rarity).add(s);
            for (String tag : i.tags) {
                final Tuple<String, Rarity> key = new Tuple<>(tag, i.rarity);
                if (!this.itemsByTagAndRarity.containsKey(key)) {
                    this.itemsByTagAndRarity.put(key, new ArrayList<>());
                }
                this.itemsByTagAndRarity.get(key).add(s);
            }
        }
    }

    /**
     * Returns a random Item while accounting for Rarity
     */
    public Item drop(Game game) {
        return game.generator.item(this.get());
    }

    /**
     * Returns a random Item's name while accounting for Rarity
     */
    public String get() {
        final Rarity r = this.rollForRarity(Rarity.values());
        return Lambda.random(this.itemsByRarity.get(r));
    }

    /**
     * Returns a random Item while accounting for tag and Rarity
     */
    public Item dropByTag(Game game, String tag) {
        return game.generator.item(this.getByTag(tag));
    }

    /**
     * Returns a random Item's name while accounting for tag and Rarity
     */
    public String getByTag(String tag) {
        final List<Rarity> rarities = new ArrayList<>();
        for (Rarity r : Rarity.values()) {
            final Tuple<String, Rarity> key = new Tuple<>(tag, r);
            if (this.itemsByTagAndRarity.containsKey(key)) {
                rarities.add(r);
            }
        }
        final Rarity r = this.rollForRarity(rarities.toArray(new Rarity[rarities.size()]));
        return Lambda.random(this.itemsByTagAndRarity.get(new Tuple<String, Rarity>(tag, r)));
    }

    /**
     * Selects a random Rarity based off of its weight
     */
    private Rarity rollForRarity(Rarity[] rarities) {
        // Calculate the total chance over all of the Rarities
        int total = 0;
        for (Rarity r : rarities) {
            total += r.chance;
        }

        // Roll on the available Rarities
        int roll = (int) Math.floor(Math.random() * total);
        for (Rarity r : rarities) {
            if (roll < r.chance) {
                return r;
            }
            roll -= r.chance;
        }
        return rarities[0];
    }
}
