package net.lugocorp.kingdom.game.core;
import net.lugocorp.kingdom.game.events.AllEventHandlers;
import net.lugocorp.kingdom.game.events.Event;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.ui.views.GameView;

/**
 * This class contains logic to help generate Units in a mod
 */
public class UnitLogic {

    /**
     * Changes this Unit's speed
     */
    public static void speed(AllEventHandlers events, Unit u, int distance) {
        events.unit.addEventHandler(u.name, "UnitMoveDistanceEvent", (GameView view, Unit receiver, Event event) -> {
            Events.UnitMoveDistanceEvent e = (Events.UnitMoveDistanceEvent) event;
            e.distance = distance;
        });
    }
}
