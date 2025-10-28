package net.lugocorp.kingdom.engine.assets;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import java.util.HashMap;
import java.util.Map;

/**
 * This class handles the creation of new fonts at runtime
 */
public class FontService {
    private final FreeTypeFontParameter params = new FreeTypeFontParameter();
    private final Map<String, FreeTypeFontGenerator> generators = new HashMap<>();
    private final Map<String, BitmapFont> generated = new HashMap<>();

    /**
     * Returns a new BitmapFont with the given parameters
     */
    public BitmapFont getFont(FontParam f) {
        if (!this.generators.containsKey(f.getFont())) {
            this.generators.put(f.getFont(),
                    new FreeTypeFontGenerator(Gdx.files.internal(String.format("fonts/%s.ttf", f.getFont()))));
        }
        final String hash = f.toString();
        if (!this.generated.containsKey(hash)) {
            this.params.color = f.getColor();
            this.params.size = f.getSize();
            final BitmapFont font = this.generators.get(f.getFont()).generateFont(this.params);
            this.generated.put(hash, font);
            return font;
        }
        return this.generated.get(hash);
    }

    /**
     * Disposes all assets loaded by this service
     */
    public void dispose() {
        for (FreeTypeFontGenerator generator : this.generators.values()) {
            generator.dispose();
        }
        for (BitmapFont font : this.generated.values()) {
            font.dispose();
        }
    }
}
