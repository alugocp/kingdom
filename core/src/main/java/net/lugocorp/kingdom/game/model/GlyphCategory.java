package net.lugocorp.kingdom.game.model;

/**
 * This class represents the different categories of Glyphs in the game
 */
public enum GlyphCategory {
    STRATEGIC(Glyph.BATTLE, Glyph.DEFENSE, Glyph.HEALING), HARVEST(Glyph.NATURE, Glyph.MINING,
            Glyph.TRAVEL), SPECIAL(Glyph.TRADE, Glyph.WORSHIP);

    public final Glyph[] glyphs;

    private GlyphCategory(Glyph... glyphs) {
        this.glyphs = glyphs;
    }

    /**
     * Returns a random Glyph value
     */
    public static GlyphCategory random() {
        GlyphCategory[] values = GlyphCategory.values();
        return values[(int) Math.floor(Math.random() * values.length)];
    }

    /** {@inheritdoc} */
    @Override
    public String toString() {
        // TODO use a StringBuilder
        String value = this.glyphs[0].toString();
        for (int a = 1; a < this.glyphs.length; a++) {
            value = String.format("%s, %s", value, this.glyphs[a].toString());
        }
        return value;
    }
}
