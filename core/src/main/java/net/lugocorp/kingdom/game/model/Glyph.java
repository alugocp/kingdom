package net.lugocorp.kingdom.game.model;

/**
 * This class represents the different categories of Units in the game
 */
public enum Glyph {
    BATTLE("Battle"), DEFENSE("Defense"), HEALING("Healing"), MINING("Mining"), NATURE("Nature"), TRADE("Trade");

    private final String label;

    private Glyph(String label) {
        this.label = label;
    }

    /** {@inheritdoc} */
    @Override
    public String toString() {
        return this.label;
    }
}
