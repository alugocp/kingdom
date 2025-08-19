package net.lugocorp.kingdom.game.combat;

/**
 * Tracks how many HitPoints to reduce from a combatant
 */
public class Damage {
    public int amount;

    public Damage(int amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return String.format("%d damage", this.amount);
    }
}
