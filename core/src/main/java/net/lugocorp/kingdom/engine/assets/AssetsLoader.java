package net.lugocorp.kingdom.engine.assets;
import net.lugocorp.kingdom.utils.mods.ModAssetsMap;
import net.lugocorp.kingdom.utils.mods.ModLoader;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.ExternalFileHandleResolver;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.collision.BoundingBox;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Wraps the logic for loading 3D model assets into the game
 */
public class AssetsLoader {
    private final ModAssetsMap modAssetsMap = new ModAssetsMap();
    private final Map<String, AssetsLoader.ModelBounds> bounds = new HashMap<>();
    private final AssetManager external = new AssetManager(new ExternalFileHandleResolver());
    private final AssetManager internal = new AssetManager();

    /**
     * Returns the mod assets map object
     */
    public ModAssetsMap getModAssetsMap() {
        return this.modAssetsMap;
    }

    /**
     * Returns the appropriate AssetManager to handle the given resource
     */
    private AssetManager getAssetManager(String name) {
        if (this.modAssetsMap.get(String.format("%s.g3db", name)).isPresent()) {
            return this.external;
        }
        return this.internal;
    }

    /**
     * Returns the filename for the given 3D model asset
     */
    private String getFilename(String name) {
        String filepath = String.format("%s.g3db", name);
        return this.modAssetsMap
                .get(filepath).map((String key) -> Gdx.files
                        .external(String.format("%s/%s/%s", ModLoader.ASSETS_BASE, key, filepath)).path())
                .orElse(filepath);
    }

    /**
     * Creates a new ModelInstance for the Model with the given name
     */
    public Optional<ModelInstance> createModelInstance(String name) {
        AssetManager assets = this.getAssetManager(name);
        assets.update();
        String filename = this.getFilename(name);
        Model model = assets.get(filename, false);
        if (model == null) {
            if (!assets.contains(filename, Model.class)) {
                assets.load(filename, Model.class);
            }
            return Optional.empty();
        }
        return Optional.of(new ModelInstance(model));
    }

    /**
     * Unloads the assets associated with the given name if they are no longer
     * needed
     */
    public void checkForUnload(String name) {
        String filename = this.getFilename(name);
        AssetManager assets = this.getAssetManager(name);
        if (assets.getReferenceCount(filename) == 0) {
            assets.unload(filename);
        }
    }

    /**
     * Calculates the width and height of the given model
     */
    private boolean calculateDimensions(String name) {
        AssetManager assets = this.getAssetManager(name);
        Model model = assets.get(this.getFilename(name), false);
        if (model == null) {
            return false;
        }
        BoundingBox box = new BoundingBox();
        model.calculateBoundingBox(box);
        this.bounds.put(name, new ModelBounds(box.getDepth(), box.getHeight(), 0f));
        return true;
    }

    /**
     * Returns the height of a given model by its name (calculates the height if
     * necessary)
     */
    public float getModelHeight(String name) {
        if (!this.bounds.containsKey(name) && !this.calculateDimensions(name)) {
            return 1f;
        }
        return this.bounds.get(name).y;
    }

    /**
     * Returns the width of a given model by its name (calculates the width if
     * necessary)
     */
    public float getModelWidth(String name) {
        if (!this.bounds.containsKey(name) && !this.calculateDimensions(name)) {
            return 1f;
        }
        return this.bounds.get(name).x;
    }

    /**
     * Calls into the AssetManager's dispose() method
     */
    public void dispose() {
        this.internal.dispose();
        this.external.dispose();
    }

    /**
     * This nested class contains model bounds data for loaded models
     */
    private static class ModelBounds {
        private final float x;
        private final float y;
        private final float z;

        private ModelBounds(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }
}
