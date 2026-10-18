package net.lugocorp.kingdom.builtin.logic;
import net.lugocorp.kingdom.builtin.Events;
import net.lugocorp.kingdom.context.GameViewContext;
import net.lugocorp.kingdom.game.model.Building;
import net.lugocorp.kingdom.gameplay.events.StratifiedPayload;
import net.lugocorp.kingdom.utils.SideEffect;

/**
 * This class contains logic to help generate Buildings in a mod
 */
public class BuildingLogic {

    /**
     * Changes this Building's vision
     */
    public static StratifiedPayload<GameViewContext, Building, Events.GetVisionEvent> vision(int radius) {
        return new StratifiedPayload<>(Events.GetVisionEvent.class,
                (GameViewContext ctx, Building receiver, Events.GetVisionEvent e) -> {
                    e.radius = radius;
                    return new SideEffect();
                });
    }
}
