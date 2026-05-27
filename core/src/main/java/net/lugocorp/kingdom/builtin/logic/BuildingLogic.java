package net.lugocorp.kingdom.builtin.logic;
import net.lugocorp.kingdom.builtin.Events;
import net.lugocorp.kingdom.game.model.Building;
import net.lugocorp.kingdom.gameplay.events.StratifiedPayload;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.SideEffect;

/**
 * This class contains logic to help generate Buildings in a mod
 */
public class BuildingLogic {

    /**
     * Changes this Building's vision
     */
    public static StratifiedPayload<Building, Events.GetVisionEvent> vision(int radius) {
        return new StratifiedPayload<>(Events.GetVisionEvent.class,
                (GameView view, Building receiver, Events.GetVisionEvent e) -> {
                    e.radius = radius;
                    return new SideEffect();
                });
    }
}
