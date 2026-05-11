package net.lugocorp.kingdom.builtin.logic;
import net.lugocorp.kingdom.builtin.Events;
import net.lugocorp.kingdom.game.model.Building;
import net.lugocorp.kingdom.gameplay.events.AllEventHandlers;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.SideEffect;

/**
 * This class contains logic to help generate Buildings in a mod
 */
public class BuildingLogic {

    /**
     * Changes this Building's vision
     */
    public static void vision(AllEventHandlers events, Building b, int radius) {
        events.building.addEventHandler(b.name, Events.GetVisionEvent.class,
                (GameView view, Building receiver, Events.GetVisionEvent e) -> {
                    e.radius = radius;
                    return new SideEffect();
                });
    }
}
