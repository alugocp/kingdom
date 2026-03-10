package net.lugocorp.kingdom.gameplay.mechanics;
import net.lugocorp.kingdom.game.model.Patron;
import net.lugocorp.kingdom.ui.views.GameView;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * This class handles the high-level logistics of Patrons
 */
public class Patronage implements Iterable<Patron> {
    private final Set<Patron> patrons = new HashSet<>();

    /**
     * Returns an Iterator for the Patrons in this Patronage
     */
    @Override
    public Iterator<Patron> iterator() {
        return this.patrons.iterator();
    }

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
    public void recalculateFavor(GameView view) {
        for (Patron p : this.patrons) {
            p.recalculateFavor(view);
        }
    }
}
