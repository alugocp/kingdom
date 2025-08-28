package net.lugocorp.kingdom.game.properties;

/**
 * This class tracks relative chance to spawn Items. Note, all the chance values
 * should add up to 100.
 */
public enum Rarity {
    COMMON("common", 80), UNCOMMON("uncommon", 15), RARE("rare", 5);

    public final String label;
    public final int chance;

    private Rarity(String label, int chance) {
        this.chance = chance;
        this.label = label;
    }

    @Override
    public String toString() {
        return this.label;
    }
}
