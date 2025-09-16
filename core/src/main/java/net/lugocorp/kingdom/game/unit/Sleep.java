package net.lugocorp.kingdom.game.unit;
import net.lugocorp.kingdom.builtin.Events;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.ui.views.GameView;

/**
 * Handles logic surrounding Unit sleep
 */
public class Sleep {
    private final Unit unit;
    private boolean sleeping = false;

    public Sleep(Unit unit) {
        this.unit = unit;
    }

    /**
     * Sets this instance's sleep state
     */
    public void set(boolean sleeping) {
        this.sleeping = sleeping;
    }

    /**
     * Returns true if we're in a sleep state
     */
    public boolean isSleeping() {
        return this.sleeping;
    }

    /**
     * Checks if we should reset this Unit's sleep state at the start of a turn
     */
    public void wakeUpCheck(GameView view) {
        Events.IsStunnedEvent event = new Events.IsStunnedEvent(this.unit);
        this.unit.handleEvent(view, event).execute();
        if (event.isStunned) {
            this.sleeping = true;
        } else if (this.sleeping) {
            this.wakeUp();
        }
    }

    /**
     * Reset this Unit's sleep state
     */
    public void wakeUp() {
        this.sleeping = false;
    }
}
