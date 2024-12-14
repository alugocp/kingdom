package net.lugocorp.kingdom.engine.assets;
import net.lugocorp.kingdom.ui.menu.ArtifactNode;
import net.lugocorp.kingdom.ui.menu.FateNode;
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
        this.load("fates");
        this.load("artifacts");
        this.load("artifact-mask");
        this.register("placeholder", "icons", InventoryNode.SIDE, InventoryNode.SIDE, 0, 0);
        this.register("potion", "icons", InventoryNode.SIDE, InventoryNode.SIDE, 1, 0);
        this.register("apple", "icons", InventoryNode.SIDE, InventoryNode.SIDE, 2, 0);
        this.register("pouch", "icons", InventoryNode.SIDE, InventoryNode.SIDE, 3, 0);
        this.register("coin", "icons", InventoryNode.SIDE, InventoryNode.SIDE, 0, 1);
        this.register("sword", "icons", InventoryNode.SIDE, InventoryNode.SIDE, 1, 1);
        this.register("shield", "icons", InventoryNode.SIDE, InventoryNode.SIDE, 2, 1);
        this.register("leaf", "icons", InventoryNode.SIDE, InventoryNode.SIDE, 3, 1);
        this.register("mushroom", "icons", InventoryNode.SIDE, InventoryNode.SIDE, 0, 2);
        this.register("emerald", "icons", InventoryNode.SIDE, InventoryNode.SIDE, 1, 2);
        this.register("bone", "icons", InventoryNode.SIDE, InventoryNode.SIDE, 2, 2);
        this.register("golden feather", "artifacts", ArtifactNode.WIDTH, ArtifactNode.HEIGHT, 0, 0);
        this.register("artifact-mask", "artifact-mask", ArtifactNode.WIDTH, ArtifactNode.HEIGHT, 0, 0);
        this.register("raider", "fates", FateNode.WIDTH, FateNode.HEIGHT, 0, 0);
        this.register("merchant", "fates", FateNode.WIDTH, FateNode.HEIGHT, 1, 0);
        this.register("veteran", "fates", FateNode.WIDTH, FateNode.HEIGHT, 2, 0);
        this.register("devout", "fates", FateNode.WIDTH, FateNode.HEIGHT, 3, 0);
        this.register("sentinel", "fates", FateNode.WIDTH, FateNode.HEIGHT, 0, 1);
        this.register("usurper", "fates", FateNode.WIDTH, FateNode.HEIGHT, 1, 1);
        this.register("forager", "fates", FateNode.WIDTH, FateNode.HEIGHT, 2, 1);
        this.register("teacher", "fates", FateNode.WIDTH, FateNode.HEIGHT, 3, 1);
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
    private void register(String name, String texture, int w, int h, int x, int y) {
        this.sprites.put(name, new TextureRegion(this.textures.get(texture), x * w, y * h, w, h));
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
