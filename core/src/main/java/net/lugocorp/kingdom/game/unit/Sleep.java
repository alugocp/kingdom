package net.lugocorp.kingdom.game.unit;
import net.lugocorp.kingdom.builtin.Events;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.ui.views.GameView;

/**
 * Handles logic surrounding Unit sleep
 */
public class Sleep {
    private final Unit unit;
    private SleepState state = SleepState.AWAKE;

    public Sleep(Unit unit) {
        this.unit = unit;
    }

    /**
     * Sets this instance's SleepState
     */
    public void set(SleepState state) {
        this.state = state;
    }

    /**
     * Returns true if we're in a sleep state
     */
    public boolean isSleeping() {
        return this.state != SleepState.AWAKE;
    }

    /**
     * Checks if we should reset this Unit's SleepState at the start of a turn
     */
    public void wakeUpCheck(GameView view) {
        Events.IsStunnedEvent event = new Events.IsStunnedEvent(this.unit);
        this.unit.handleEvent(view, event).execute();
        if (event.isStunned) {
            this.state = SleepState.SLEEPING;
        } else if (this.state == SleepState.SLEEPING
                || (this.state == SleepState.SLEEPING_INVENTORY && this.unit.haul.isFull())) {
            this.wakeUp();
        }
    }

    /**
     * Reset this Unit's SleepState
     */
    public void wakeUp() {
        this.state = SleepState.AWAKE;
    }
}
