package net.lugocorp.kingdom.game.combat;
import net.lugocorp.kingdom.game.core.Events;
import net.lugocorp.kingdom.game.events.EventReceiver;
import net.lugocorp.kingdom.ui.views.GameView;

/**
 * Stores the hit points of a combatant
 */
public class HitPoints<A extends EventReceiver> {
    private boolean vulnerable = true;
    private final A bearer;
    private int value = 1;
    private int max = 1;

    public HitPoints(A bearer) {
        this.bearer = bearer;
    }

    /**
     * This should only be used in conjunction with Kryo rehydration
     */
    public HitPoints() {
        this.bearer = null;
    }

    /**
     * Returns true if the host entity can take damage
     */
    public boolean isVulnerable() {
        return this.vulnerable;
    }

    /**
     * Makes this object's bearer invulnerable (cannot take damage)
     */
    public void invulnerable() {
        this.vulnerable = false;
    }

    /**
     * Heals some damage from these HitPoints
     */
    public void heal(int points) {
        this.set(this.get() + points);
    }

    /**
     * Runs the logic required for this combatant to take Damage
     */
    public void takeDamage(GameView view, Damage dmg) {
        this.bearer.handleEvent(view, new Events.TakeDamageEvent<A>(this.bearer, dmg));
        this.set(this.get() - dmg.amount);
        if (this.isDead()) {
            this.bearer.deactivate(view);
        }
    }

    /**
     * This combatant attacks another
     */
    public <T extends EventReceiver> void attack(GameView view, HitPoints<T> target, Damage dmg) {
        this.bearer.handleEvent(view, new Events.AttackEvent<A, T>(this.bearer, target.bearer, dmg));
        target.bearer.handleEvent(view, new Events.AttackedEvent<T, A>(target.bearer, this.bearer, dmg));
        target.takeDamage(view, dmg);
    }

    /**
     * Returns this object's remaining health
     */
    public int get() {
        return this.value;
    }

    /**
     * Sets this object's remaining health
     */
    public void set(int value) {
        this.value = Math.max(0, Math.min(this.max, value));
    }

    /**
     * Returns this object's maximum health
     */
    public int getMax() {
        return this.max;
    }

    /**
     * Sets this object's maximum health
     */
    public void setMax(int max) {
        this.max = Math.max(0, max);
        if (this.value > max) {
            this.value = max;
        }
    }

    /**
     * Returns true if this combatant has lost all its health
     */
    public boolean isDead() {
        return this.value == 0;
    }
}
