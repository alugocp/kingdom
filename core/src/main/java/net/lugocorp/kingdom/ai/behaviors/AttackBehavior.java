package net.lugocorp.kingdom.ai.behaviors;
import net.lugocorp.kingdom.builtin.Events;
import net.lugocorp.kingdom.game.layers.Entity;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.game.player.CompPlayer;
import net.lugocorp.kingdom.gameplay.events.Event;
import net.lugocorp.kingdom.prediction.Prediction;

/**
 * This Behavior tells the given Unit to follow and cast damaging Abilities
 * against the given target
 */
public class AttackBehavior extends FollowAndCastBehavior {

    public AttackBehavior(CompPlayer player, Unit unit, Entity target) {
        super(player, unit, target, () -> target.combat.health.isDead(), (Prediction prediction) -> {
            for (Event e : prediction.log) {
                if (e.getClass() == Events.TakeDamageEvent.class && ((Events.TakeDamageEvent) e).target == target) {
                    return true;
                }
            }
            return false;
        });
    }
}
