package net.lugocorp.kingdom.engine.assets;
import net.lugocorp.kingdom.mods.ModAssetsMap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Wraps the logic for loading 2D sprite assets into the game
 */
public class SpriteLoader extends AssetsPool<Texture> {
    private final Map<String, TextureRegionData> sprites = new HashMap<>();

    public SpriteLoader(ModAssetsMap modAssetsMap) {
        super(modAssetsMap, Texture.class, "png");
    }

    /**
     * Registers a sprite from a previously loaded texture
     */
    public void register(String name, String texture, int dx, int dy, int ix, int iy) {
        this.sprites.put(name, new TextureRegionData(texture, ix * dx, iy * dy, dx, dy));
    }

    /**
     * Retrieves a registered sprite
     */
    public Optional<TextureRegion> getTextureRegion(String name) {
        if (!this.sprites.containsKey(name)) {
            return Optional.empty();
        }
        TextureRegionData data = this.sprites.get(name);
        if (data.region.isPresent()) {
            return data.region;
        }
        return this.getAsset(data.filepath).map((Texture t) -> {
            TextureRegion tr = new TextureRegion(t, data.x, data.y, data.w, data.h);
            data.region = Optional.of(tr);
            return tr;
        });
    }

    /**
     * This nested class contains data on
     */
    private static class TextureRegionData {
        private final String filepath;
        private final int x;
        private final int y;
        private final int w;
        private final int h;
        private Optional<TextureRegion> region = Optional.empty();

        private TextureRegionData(String filepath, int x, int y, int w, int h) {
            this.filepath = filepath;
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
        }
    }
}
