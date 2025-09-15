package net.lugocorp.kingdom.game.unit;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.ui.views.GameView;
import java.util.Optional;

/**
 * This class tracks a Unit's loyalty value
 */
public class Loyalty {
    public static final int MAX_LOYALTY = 10;
    private final Unit unit;
    private int loyalty = Loyalty.MAX_LOYALTY;

    public Loyalty(Unit unit) {
        this.unit = unit;
    }

    /**
     * Returns this instance's current loyalty value
     */
    public int get() {
        return this.loyalty;
    }

    /**
     * This instance loses loyalty and may abandon the cause
     */
    public void decrease(GameView view, int points) {
        this.loyalty = Math.max(0, this.loyalty - points);
        if (this.loyalty == 0) {
            view.game.mechanics.turns.removeFutureEvents(this.unit, "HungerStrikes");
            view.game.setLeader(view, this.unit, Optional.empty());
        }
    }

    /**
     * Resets this instance's loyalty
     */
    public void reset() {
        this.loyalty = Loyalty.MAX_LOYALTY;
    }
}
