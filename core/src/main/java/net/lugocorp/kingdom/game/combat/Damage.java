package net.lugocorp.kingdom.game.combat;

/**
 * Tracks how many HitPoints to reduce from a combatant
 */
public class Damage {
    private boolean critical = false;
    public int base;

    public Damage(int base) {
        this.base = base;
    }

    /**
     * Returns the final value of Damage dealt, accounting for critical hits
     */
    public int total() {
        return this.critical ? (int) Math.floor(this.base * 1.25) : this.base;
    }

    /**
     * Sets whether or not this Damage is from a critical hit
     */
    public void setCritical(boolean critical) {
        this.critical = critical;
    }

    @Override
    public String toString() {
        return String.format("%d damage", this.base);
    }
}
