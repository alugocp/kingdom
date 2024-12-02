package net.lugocorp.kingdom.game.model;

public enum Glyph {
    BATTLE("Battle"), DEFENSE("Defense"), HEALING("Healing"), MINING("Mining"), NATURE("Nature"), TRAVEL(
            "Travel"), TRADE("Trade"), WORSHIP("Worship");

    public final String label;

    private Glyph(String label) {
        this.label = label;
    }
}
