package net.lugocorp.kingdom.engine.assets;
import net.lugocorp.kingdom.utils.logic.Colors;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import java.util.HashMap;
import java.util.Map;

/**
 * This class handles the creation of new fonts at runtime
 */
public class FontService {
    private static final int DEFAULT_SIZE = 18;
    private final FreeTypeFontGenerator generator = new FreeTypeFontGenerator(
            Gdx.files.internal("fonts/DejaVuSans.ttf"));
    private final FreeTypeFontParameter params = new FreeTypeFontParameter();
    private final Map<String, BitmapFont> generated = new HashMap<>();

    /**
     * Converts the requested font parameters into a hash string
     */
    private String getFontHash(int size, int hex) {
        return ((Integer) ((size << 24) + hex)).toString();
    }

    /**
     * Calls down into getFont() with the default font size
     */
    public BitmapFont getFont(Color color) {
        return this.getFont(FontService.DEFAULT_SIZE, color);
    }

    /**
     * Returns a new BitmapFont with the given parameters
     */
    public BitmapFont getFont(int size, Color color) {
        final int hex = Colors.asInt(color);
        final String hash = this.getFontHash(size, hex);
        if (!this.generated.containsKey(hash)) {
            this.params.color = color;
            this.params.size = size;
            BitmapFont font = this.generator.generateFont(this.params);
            this.generated.put(hash, font);
            return font;
        }
        return this.generated.get(hash);
    }

    /**
     * Disposes all assets loaded by this service
     */
    public void dispose() {
        this.generator.dispose();
        for (BitmapFont font : this.generated.values()) {
            font.dispose();
        }
    }
}
