package net.lugocorp.kingdom.assets;
import net.lugocorp.kingdom.ui.menu.InventoryNode;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import java.util.HashMap;
import java.util.Map;

/**
 * Wraps the logic for loading 2D sprite assets into the game
 */
public class SpritesLoader {
    private final Map<String, TextureRegion> sprites = new HashMap<>();
    private final Map<String, Texture> textures = new HashMap<>();

    /**
     * Loads all textures and registers all sprites for the game
     */
    public void loadAndRegister() {
        this.load("icons");
        this.register("placeholder", "icons", InventoryNode.SIDE, 0, 0);
        this.register("potion", "icons", InventoryNode.SIDE, 1, 0);
    }

    /**
     * Retrieves a registered sprite
     */
    public TextureRegion get(String name) {
        return this.sprites.get(name);
    }

    /**
     * Loads a single named texture
     */
    private void load(String name) {
        this.textures.put(name, new Texture(String.format("%s.png", name)));
    }

    /**
     * Registers a sprite from a previously loaded texture
     */
    private void register(String name, String texture, int side, int x, int y) {
        this.sprites.put(name, new TextureRegion(this.textures.get(texture), x * side, y * side, side, side));
    }

    /**
     * Disposes all loaded resources
     */
    public void dispose() {
        for (Texture t : this.textures.values()) {
            t.dispose();
        }
    }
}
