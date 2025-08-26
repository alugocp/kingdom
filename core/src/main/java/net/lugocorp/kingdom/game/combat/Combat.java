package net.lugocorp.kingdom.game.combat;
import net.lugocorp.kingdom.game.core.Events;
import net.lugocorp.kingdom.game.events.EventReceiver;
import net.lugocorp.kingdom.game.model.Building;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.game.player.CompPlayer;
import net.lugocorp.kingdom.game.player.Player;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.code.SideEffect;
import java.util.ArrayList;
import java.util.List;

/**
 * This class handles all combat logic for any Unit or Building
 */
public class Combat<B extends EventReceiver> {
    protected final B bearer;
    public final HitPoints health = new HitPoints();

    public Combat(B bearer) {
        this.bearer = bearer;
    }

    /**
     * This should only be used in conjunction with Kryo rehydration
     */
    public Combat() {
        this.bearer = null;
    }

    /**
     * This method gets called when a combatant is killed in battle
     */
    protected <A extends EventReceiver> SideEffect onDeath(GameView view, A attacker) {
        List<SideEffect> effects = SideEffect.list();
        // TODO handle Buildings attackers here
        if (attacker instanceof Unit) {
            if (this.bearer instanceof Unit) {
                effects.add(
                        this.bearer.handleEvent(view, new Events.UnitDiedEvent((Unit) this.bearer, (Unit) attacker)));
                effects.add(
                        attacker.handleEvent(view, new Events.KilledUnitEvent((Unit) attacker, (Unit) this.bearer)));
                effects.add(() -> {
                    if (((Unit) this.bearer).getLeader().map((Player p) -> !p.isHumanPlayer()).orElse(false)) {
                        CompPlayer comp = (CompPlayer) ((Unit) this.bearer).getLeader().get();
                        comp.stats.unitsLost.add(1);
                    }
                    if (((Unit) attacker).getLeader().map((Player p) -> !p.isHumanPlayer()).orElse(false)) {
                        CompPlayer comp = (CompPlayer) ((Unit) attacker).getLeader().get();
                        comp.stats.enemiesKilled.add(1);
                    }
                });
            }
            if (this.bearer instanceof Building) {
                // TODO implement this
            }
        }
        effects.add(() -> this.bearer.deactivate(view));
        return SideEffect.all(effects);
    }

    /**
     * Runs the logic required for this bearer to take Damage
     */
    public <A extends EventReceiver> SideEffect takeDamage(GameView view, Damage dmg, A attacker) {
        if (!this.health.isVulnerable()) {
            return SideEffect.none;
        }
        List<SideEffect> effects = new ArrayList<>();
        Events.TakeDamageEvent<B> damageEvent = new Events.TakeDamageEvent<B>(this.bearer, dmg);
        effects.add(this.bearer.handleEvent(view, damageEvent));
        effects.add(() -> this.health.set(this.health.get() - damageEvent.dmg.amount));
        if (this.health.get() <= damageEvent.dmg.amount) {
            effects.add(this.onDeath(view, attacker));
        }
        return SideEffect.all(effects);
    }

    /**
     * This bearer attacks another
     */
    public <T extends EventReceiver> SideEffect attack(GameView view, Combat<T> target, Damage dmg) {
        // TODO trigger a GetCriticalHitChanceEvent here to set isCriticalHit fields
        return SideEffect.all(
                this.bearer.handleEvent(view, new Events.AttackEvent<B, T>(this.bearer, target.bearer, dmg)),
                target.bearer.handleEvent(view, new Events.AttackedEvent<T, B>(target.bearer, this.bearer, dmg)),
                target.takeDamage(view, dmg, this.bearer));
    }
}
