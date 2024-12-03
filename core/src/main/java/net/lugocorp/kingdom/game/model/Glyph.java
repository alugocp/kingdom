package net.lugocorp.kingdom.game.model;

/**
 * This class represents the different categories of Units in the game
 */
public enum Glyph {
    BATTLE("Battle"), DEFENSE("Defense"), HEALING("Healing"), MINING("Mining"), NATURE("Nature"), TRAVEL(
            "Travel"), TRADE("Trade"), WORSHIP("Worship");

    private final String label;

    private Glyph(String label) {
        this.label = label;
    }

    /**
     * Returns a random Glyph value
     */
    public static Glyph random() {
        Glyph[] values = Glyph.values();
        return values[(int) Math.floor(Math.random() * values.length)];
    }

    /** {@inheritdoc} */
    @Override
    public String toString() {
        return this.label;
    }
}
