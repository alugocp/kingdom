package net.lugocorp.kingdom.gameplay.combat;

/**
 * Stores the hit points of a combatant
 */
public class HitPoints {
    private boolean vulnerable = true;
    private int value = 1;
    private int max = 1;

    /**
     * Returns true if the host entity can take damage
     */
    public boolean isVulnerable() {
        return this.vulnerable;
    }

    /**
     * Makes this object's combatant invulnerable (cannot take damage)
     */
    public void invulnerable() {
        this.vulnerable = false;
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
     * Sets this object's maximum health and current health
     */
    public void setMaxAndValue(int max) {
        this.value = max;
        this.max = max;
    }

    /**
     * Returns true if this combatant has lost all its health
     */
    public boolean isDead() {
        return this.value == 0;
    }

    /**
     * Returns true if the health is currently at or below the given percentage of
     * the max value
     */
    public boolean atOrBelowPercent(int percent) {
        return this.value <= this.max * percent / 100;
    }
}
