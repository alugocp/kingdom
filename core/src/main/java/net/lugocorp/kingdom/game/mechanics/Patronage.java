package net.lugocorp.kingdom.game.mechanics;
import net.lugocorp.kingdom.game.model.Patron;
import java.util.HashSet;
import java.util.Set;

/**
 * This class handles the high-level logistics of Patrons
 */
public class Patronage {
    private final Set<Patron> patrons = new HashSet<>();

    /**
     * Registers the given Patron
     */
    public void addPatron(Patron p) {
        this.patrons.add(p);
    }

    /**
     * Tells each Patron to recalculate how much favor each Player has earned with
     * it
     */
    public void recalculateFavor() {
        for (Patron p : this.patrons) {
            p.recalculateFavor();
        }
    }
}
