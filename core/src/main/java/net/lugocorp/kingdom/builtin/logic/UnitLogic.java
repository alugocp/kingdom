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
     * Changes this Unit's speed
     */
    public static StratifiedPayload<Unit, Events.UnitMoveDistanceEvent> speed(int distance) {
        return new StratifiedPayload<>(Events.UnitMoveDistanceEvent.class,
                (GameView view, Unit receiver, Events.UnitMoveDistanceEvent e) -> {
                    e.distance = distance;
                    return new SideEffect();
                });
    }

    /**
     * Changes this Unit's vision
     */
    public static StratifiedPayload<Unit, Events.GetVisionEvent> vision(int radius) {
        return new StratifiedPayload<>(Events.GetVisionEvent.class,
                (GameView view, Unit receiver, Events.GetVisionEvent e) -> {
                    e.radius = radius;
                    return new SideEffect();
                });
    }
}
