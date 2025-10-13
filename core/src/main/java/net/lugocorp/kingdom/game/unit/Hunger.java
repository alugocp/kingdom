package net.lugocorp.kingdom.game.unit;
import net.lugocorp.kingdom.game.model.Item;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.ui.overlay.EntityRisingOverlay;
import net.lugocorp.kingdom.ui.views.GameView;
import java.util.Set;

/**
 * This class handles a Unit's hunger
 */
public class Hunger {
    private final Unit unit;
    private int turnsToGetHungry = 20;
    private String preferred = "fruit";

    public Hunger(Unit unit) {
        this.unit = unit;
    }

    /**
     * Returns this instance's preferred food
     */
    public String getPreferredFood() {
        return this.preferred;
    }

    /**
     * Sets this instance's preferred food
     */
    public void setPreferredFood(String preferred) {
        this.preferred = preferred;
    }

    /**
     * Returns true if this instance's Unit can eat the given Item
     */
    public boolean canEat(Item i) {
        return i.getTag().map((String t) -> t.equals(this.preferred)).orElse(false);
    }

    /**
     * Checks if this instance's Unit can auto eat, and returns true if they can
     */
    public boolean autoEatCheck(GameView view) {
        final Set<Item> food = this.unit.haul.getEdibleItems(view, this.unit);
        if (food.size() > 0) {
            this.unit.haul.remove(food.iterator().next());
            this.eat(view, true);
            return true;
        }
        return false;
    }

    /**
     * Tells this instance to find an edible hauled Item and eat it. If there is no
     * such Item then hunger begins to strike the Unit.
     */
    public void gotHungry(GameView view) {
        if (!this.autoEatCheck(view)) {
            view.game.future.addFutureTick("HungerStrikes", this.unit, 1, true);
        }
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
        final int remainingTurns = view.game.future.getFutureEventRemainingTurns(this.unit, "GetsHungry");
        if (remainingTurns >= 0 && remainingTurns + diff <= 0) {
            view.game.future.handleFutureTicksEarly(view, this.unit, "GetsHungry");
        }
        this.turnsToGetHungry = n;
    }

    /**
     * Resets this instance's hunger
     */
    public void eat(GameView view, boolean visible) {
        if (visible) {
            view.overlays.add(new EntityRisingOverlay(view, this.unit, 0x7d4513, "Hunger reset"));
        }
        view.game.future.removeFutureEvents(this.unit, "GetsHungry");
        view.game.future.removeFutureEvents(this.unit, "HungerStrikes");
        view.game.future.addFutureTick("GetsHungry", this.unit, this.turnsToGetHungry, false);
    }
}
