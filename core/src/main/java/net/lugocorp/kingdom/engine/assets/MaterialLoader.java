package net.lugocorp.kingdom.engine.assets;
import net.lugocorp.kingdom.utils.mods.ModAssetsMap;
import com.badlogic.gdx.graphics.g3d.Material;
import java.util.Optional;

/**
 * Wraps the logic for loading Material assets into the game
 */
public class MaterialLoader extends AssetsPool<Material> {

    public MaterialLoader(ModAssetsMap modAssetsMap) {
        super(modAssetsMap, Material.class, "mtl");
    }

    /**
     * Retrieves a Material asset with the given name
     */
    public Optional<Material> getMaterial(String name) {
        return this.getAsset(name);
    }
}
