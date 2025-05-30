package net.lugocorp.kingdom.game.model;

/**
 * This class represents the different categories of Glyphs in the game
 */
public enum GlyphCategory {
    STRATEGIC("ui/glyph-strategy", Glyph.BATTLE, Glyph.DEFENSE, Glyph.HEALING), HARVEST("ui/glyph-harvest",
            Glyph.NATURE, Glyph.MINING, Glyph.TRAVEL), SPECIAL("ui/glyph-special", Glyph.TRADE, Glyph.WORSHIP);

    public final Glyph[] glyphs;
    public final String icon;

    private GlyphCategory(String icon, Glyph... glyphs) {
        this.glyphs = glyphs;
        this.icon = icon;
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
        final StringBuilder builder = new StringBuilder();
        builder.append(this.glyphs[0].toString());
        for (int a = 1; a < this.glyphs.length; a++) {
            builder.append(", ");
            builder.append(this.glyphs[a].toString());
        }
        return builder.toString();
    }
}
