package net.lugocorp.kingdom.game.properties;

/**
 * This class tracks relative chance to spawn Items. Note, all the chance values
 * should add up to 100.
 */
public enum Rarity {
    COMMON("common", 80, 0x0073ff), UNCOMMON("uncommon", 15, 0x32c219), RARE("rare", 5, 0x8224d1);

    public final String label;
    public final int chance;
    public final int color;

    private Rarity(String label, int chance, int color) {
        this.chance = chance;
        this.color = color;
        this.label = label;
    }

    @Override
    public String toString() {
        return this.label;
    }
}
