package net.lugocorp.kingdom.game.unit;
import net.lugocorp.kingdom.game.Game;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.ui.views.GameView;

/**
 * This class handles a Unit's hunger
 */
public class Hunger {
    private final Unit unit;
    private int turnsToGetHungry = 20;

    public Hunger(Unit unit) {
        this.unit = unit;
    }

    /**
     * Returns the number of turns before hunger strikes
     */
    public int getTurnsBeforeHunger() {
        return this.turnsToGetHungry;
    }

    /**
     * Changes how long this instance's Unit takes to get hungry
     */
    public void setTimeToHunger(GameView view, int n) {
        final int diff = n - this.turnsToGetHungry;
        final int remainingTurns = view.game.mechanics.turns.getFutureEventRemainingTurns(this.unit, "GetsHungry");
        if (remainingTurns >= 0 && remainingTurns + diff <= 0) {
            view.game.mechanics.turns.handleFutureTicksEarly(view, this.unit, "GetsHungry");
        }
        this.turnsToGetHungry = n;
    }

    /**
     * Resets this instance's hunger
     */
    public void eat(Game game) {
        game.mechanics.turns.removeFutureEvents(this.unit, "GetsHungry");
        game.mechanics.turns.removeFutureEvents(this.unit, "HungerStrikes");
        game.mechanics.turns.addFutureTick("GetsHungry", this.unit, this.turnsToGetHungry, false);
    }
}
