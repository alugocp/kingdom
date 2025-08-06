package net.lugocorp.kingdom.game.combat;
import net.lugocorp.kingdom.game.core.Events;
import net.lugocorp.kingdom.game.events.EventReceiver;
import net.lugocorp.kingdom.ui.views.GameView;

/**
 * This class handles all combat logic for any Unit or Building
 */
public class Combat<B extends EventReceiver> {
    private final B bearer;
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
    protected <A extends EventReceiver> void onDeath(GameView view, A attacker) {
        // No-op
    }

    /**
     * Runs the logic required for this bearer to take Damage
     */
    public <A extends EventReceiver> void takeDamage(GameView view, Damage dmg, A attacker) {
        if (!this.health.isVulnerable()) {
            return;
        }
        this.bearer.handleEvent(view, new Events.TakeDamageEvent<B>(this.bearer, dmg));
        this.health.set(this.health.get() - dmg.amount);
        if (this.health.isDead()) {
            this.bearer.deactivate(view);
            this.onDeath(view, attacker);
        }
    }

    /**
     * This bearer attacks another
     */
    public <T extends EventReceiver> void attack(GameView view, Combat<T> target, Damage dmg) {
        this.bearer.handleEvent(view, new Events.AttackEvent<B, T>(this.bearer, target.bearer, dmg));
        target.bearer.handleEvent(view, new Events.AttackedEvent<T, B>(target.bearer, this.bearer, dmg));
        target.takeDamage(view, dmg, this.bearer);
    }
}
