package net.lugocorp.kingdom.builtin.logic;
import net.lugocorp.kingdom.builtin.Events;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.gameplay.events.StratifiedPayload;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.SideEffect;

/**
 * This class contains logic to help generate Units in a mod
 */
public class UnitLogic {

    /**
     * Makes the given Unit get hungry faster than normal
     */
    public static void hungry(GameView view, Unit unit) {
        unit.hunger.setTimeToHunger(view, 10);
    }

    /**
     * Changes this Unit's speed
     */
    public static StratifiedPayload<Unit, Events.UnitMoveDistanceEvent> speed(int distance) {
        return new StratifiedPayload<>(Events.UnitMoveDistanceEvent.class,
                (GameView view, Unit receiver, Events.UnitMoveDistanceEvent e) -> {
                    e.distance = distance;
                    return new SideEffect();
                });
    }
}
