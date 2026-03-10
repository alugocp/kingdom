package net.lugocorp.kingdom.gameplay.combat;

/**
 * Tracks how many HitPoints to reduce from a combatant
 */
public class Damage {
    private float multiplier = 1f;
    public int base;

    public Damage(int base) {
        this.base = base;
    }

    /**
     * Returns the final value of Damage dealt, accounting for critical hits
     */
    public int total() {
        return (int) Math.floor(Math.max(this.base, 0) * this.multiplier);
    }

    /**
     * Sets a multiplier on top of the base value (used for critical hits)
     */
    public void setMultiplier(float multiplier) {
        this.multiplier = multiplier;
    }

    @Override
    public String toString() {
        return String.format("%d damage", this.base);
    }
}
