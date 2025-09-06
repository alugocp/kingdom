package net.lugocorp.kingdom.game.glyph;

/**
 * This class represents the different categories of Units in the game
 */
public enum Glyph {
    BATTLE("Battle", "battle"), DEFENSE("Defense", "defense"), HEALING("Healing", "healing"), MINING("Mining",
            "mining"), NATURE("Nature", "nature"), TRADE("Trade", "trade");

    private final String label;
    public final String key;

    private Glyph(String label, String key) {
        this.label = label;
        this.key = key;
    }

    /** {@inheritdoc} */
    @Override
    public String toString() {
        return this.label;
    }
}
