package net.lugocorp.kingdom.game.combat;

/**
 * Tracks how many HitPoints to reduce from a combatant
 */
public class Damage {
    public DamageType type;
    public int amount;

    public Damage(DamageType type, int amount) {
        this.amount = amount;
        this.type = type;
    }

    @Override
    public String toString() {
        return String.format("%d %s damage", this.amount, this.type.label);
    }

    /**
     * Describes how this Damage affects the defender
     */
    public enum DamageType {
        // Arrows, blades, frost (pierces the cell membrane)
        PIERCING("piercing"),

        // Blunt instruments, waves, hand-to-hand combat
        IMPACT("impact"),

        // Lightning, fire, holy light, decay
        BURNING("burning");

        private final String label;

        private DamageType(String label) {
            this.label = label;
        }
    }
}
