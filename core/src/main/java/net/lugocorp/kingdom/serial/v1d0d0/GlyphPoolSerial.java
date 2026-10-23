package net.lugocorp.kingdom.serial.v1d0d0;
import net.lugocorp.kingdom.game.glyph.Glyph;
import net.lugocorp.kingdom.gameplay.mechanics.GlyphPools;
import net.lugocorp.kingdom.serial.Serial;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Serial<GlyphPools> implementation for v1.0.0
 */
public class GlyphPoolSerial implements Serial<GlyphPools> {
    public final Map<String, List<String>> pools = new HashMap<>();
    public final Map<String, Integer> originals = new HashMap<>();

    public GlyphPoolSerial(Map<Glyph, List<String>> pools, Map<Glyph, Integer> originals) {
        for (Glyph glyph : Glyph.values()) {
            this.originals.put(glyph.toString(), originals.get(glyph));
            this.pools.put(glyph.toString(), pools.get(glyph));
        }
    }

    /** {@inheritdoc} */
    public GlyphPools deserialize() {
        final GlyphPools pools = new GlyphPools();
        pools.populate(this.pools, this.originals);
        return pools;
    }
}
