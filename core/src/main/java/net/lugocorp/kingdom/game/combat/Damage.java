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
        // return String.format("%d %s damage", this.amount, this.type.label);
        return String.format("%d damage", this.amount);
    }

    /**
     * Describes how this Damage affects the defender
     */
    /*
     * public enum DamageType { // Arrows, blades, frost (pierces the cell membrane)
     * PIERCING("piercing"),
     *
     * // Blunt instruments, waves, hand-to-hand combat IMPACT("impact"),
     *
     * // Lightning, fire, holy light, decay, acid, magic (anything that causes //
     * sufficiently uniform rapid wear and tear to a surface) SPECIAL("special");
     *
     * private final String label;
     *
     * private DamageType(String label) { this.label = label; } }
     */
}
