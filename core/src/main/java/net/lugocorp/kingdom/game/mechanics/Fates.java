package net.lugocorp.kingdom.game.mechanics;
import net.lugocorp.kingdom.game.Game;
import net.lugocorp.kingdom.game.model.Fate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * This utility class handles logic surrounding the Fates system
 */
public class Fates {
    private final List<Fate> fates = new ArrayList<>();

    /**
     * Generates all registered Fates at the start of the Game
     */
    public void init(Game g) {
        Set<String> stratifiers = g.events.fate.getStratifiers();
        for (String name : stratifiers) {
            this.fates.add(g.generator.fate(name));
        }
    }

    /**
     * Returns a List of all Fates in the Game
     */
    public List<Fate> getFates() {
        return this.fates;
    }

    /**
     * Returns the first registered Fate
     */
    public Fate getFirstFate() {
        return this.fates.get(0);
    }

    /**
     * Returns a random Fate
     */
    public Fate chooseRandomFate() {
        return this.fates.get((int) Math.floor(Math.random() * this.fates.size()));
    }
}
