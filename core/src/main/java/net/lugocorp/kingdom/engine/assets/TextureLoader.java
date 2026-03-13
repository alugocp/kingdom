package net.lugocorp.kingdom.engine.assets;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.utils.TextureDescriptor;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Wraps the logic for loading Texture assets into the game
 */
public class TextureLoader extends AssetsPool<Texture> {
    private final Map<String, TextureDescriptor> descriptors = new HashMap<>();

    public TextureLoader() {
        super(Texture.class, "png");
    }

    /**
     * Retrieves a Texture asset with the given name
     */
    public Optional<Texture> getTexture(String name) {
        return this.getAsset(name);
    }

    /**
     * Retrieves a TextureDescriptor from the given Texture asset name
     */
    public Optional<TextureDescriptor> getTextureDescriptor(String name) {
        Optional<Texture> t = this.getAsset(name);
        if (!t.isPresent()) {
            return Optional.empty();
        }
        if (!this.descriptors.containsKey(name)) {
            this.descriptors.put(name, new TextureDescriptor(t.get()));
        }
        return Optional.of(this.descriptors.get(name));
    }
}
