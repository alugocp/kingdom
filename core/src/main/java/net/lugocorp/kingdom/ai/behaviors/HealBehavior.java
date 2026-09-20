package net.lugocorp.kingdom.ai.behaviors;
import net.lugocorp.kingdom.builtin.Events;
import net.lugocorp.kingdom.game.layers.Entity;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.game.player.CompPlayer;
import net.lugocorp.kingdom.gameplay.events.Event;
import net.lugocorp.kingdom.prediction.Prediction;

/**
 * This Behavior tells the given Unit to follow and cast healing Abilities on
 * the given target
 */
public class HealBehavior extends FollowAndCastBehavior {

    public HealBehavior(CompPlayer player, Unit unit, Entity target) {
        super(player, unit, target,
                () -> target.combat.health.isDead() || target.combat.health.get() == target.combat.health.getMax(),
                (Prediction prediction) -> {
                    for (Event event : prediction.log) {
                        if (event.getClass() == Events.HealEntityEvent.class) {
                            final Events.HealEntityEvent e = (Events.HealEntityEvent) event;
                            return e.target == target && e.healer == unit;
                        }
                    }
                    return false;
                });
    }
}
