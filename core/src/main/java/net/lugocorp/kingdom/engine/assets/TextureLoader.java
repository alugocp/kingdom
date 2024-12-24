package net.lugocorp.kingdom.engine.assets;
import net.lugocorp.kingdom.utils.mods.ModAssetsMap;
import com.badlogic.gdx.graphics.Texture;
import java.util.Optional;

/**
 * Wraps the logic for loading Texture assets into the game
 */
public class TextureLoader extends AssetsPool<Texture> {

    public TextureLoader(ModAssetsMap modAssetsMap) {
        super(modAssetsMap, Texture.class, "png");
    }

    /**
     * Retrieves a Texture asset with the given name
     */
    public Optional<Texture> getTexture(String name) {
        return this.getAsset(name);
    }
}
