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
    public static final int BOLD = 1;
    private final FreeTypeFontGenerator generator = new FreeTypeFontGenerator(
            Gdx.files.internal("fonts/DejaVuSans.ttf"));
    private final FreeTypeFontGenerator boldGenerator = new FreeTypeFontGenerator(
            Gdx.files.internal("fonts/DejaVuSans-Bold.ttf"));
    private final FreeTypeFontParameter params = new FreeTypeFontParameter();
    private final Map<String, BitmapFont> generated = new HashMap<>();
    // TODO write a nice FontParams class with some default font settings

    /**
     * Converts the requested font parameters into a hash string
     */
    private String getFontHash(int modifier, int size, int hex) {
        if (size > 255) {
            throw new RuntimeException(String.format("Font size %d is too large", size));
        }
        return ((Integer) ((modifier << 32) + (size << 24) + hex)).toString();
    }

    /**
     * Calls down into getFont() with the default modifier and font size
     */
    public BitmapFont getFont(Color color) {
        return this.getFont(0, FontService.DEFAULT_SIZE, color);
    }

    /**
     * Calls down into getFont() with the default modifier
     */
    public BitmapFont getFont(int size, Color color) {
        return this.getFont(0, size, color);
    }

    /**
     * Returns a new BitmapFont with the given parameters
     */
    public BitmapFont getFont(int modifier, int size, Color color) {
        final int hex = Colors.asInt(color);
        final String hash = this.getFontHash(modifier, size, hex);
        if (!this.generated.containsKey(hash)) {
            this.params.color = color;
            this.params.size = size;
            final BitmapFont font = (modifier == FontService.BOLD ? this.boldGenerator : this.generator)
                    .generateFont(this.params);
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
