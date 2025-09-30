package net.lugocorp.kingdom.engine.assets;
import net.lugocorp.kingdom.mods.ModAssetsMap;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.collision.BoundingBox;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Wraps the logic for loading 3D model assets into the game
 */
public class ModelLoader extends AssetsPool<Model> {
    private final Map<String, ModelLoader.ModelBounds> bounds = new HashMap<>();

    public ModelLoader(ModAssetsMap modAssetsMap) {
        super(modAssetsMap, Model.class, "g3db");
    }

    /**
     * Creates a new ModelInstance for the Model with the given name
     */
    public Optional<ModelInstance> createModelInstance(String name) {
        return this.getAsset(name).map((Model model) -> new ModelInstance(model));
    }

    /**
     * Calculates the width and height of the given model
     */
    private boolean calculateDimensions(String name) {
        final Optional<Model> model = this.getAsset(name);
        if (!model.isPresent()) {
            return false;
        }
        final BoundingBox box = new BoundingBox();
        model.get().calculateBoundingBox(box);
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
