package net.lugocorp.kingdom.game.model.fields;

/**
 * This class tracks relative chance to spawn Items
 */
public enum Rarity {
    COMMON("common"), UNCOMMON("uncommon"), RARE("rare");

    public final String label;

    private Rarity(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return this.label;
    }
}
