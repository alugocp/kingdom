package net.lugocorp.kingdom.engine.assets;
import com.badlogic.gdx.assets.AssetManager;
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
    private final Map<String, AssetsLoader.ModelBounds> bounds = new HashMap<>();
    private final AssetManager assets;

    public AssetsLoader(AssetManager assets) {
        this.assets = assets;
    }

    /**
     * Returns the filename for the given 3D model asset
     */
    private String getFilename(String name) {
        return String.format("%s.g3db", name);
    }

    /**
     * Creates a new ModelInstance for the Model with the given name
     */
    public Optional<ModelInstance> createModelInstance(String name) {
        this.assets.update();
        String filename = this.getFilename(name);
        Model model = this.assets.get(filename, false);
        if (model == null) {
            if (!this.assets.contains(filename, Model.class)) {
                this.assets.load(filename, Model.class);
            }
            return Optional.empty();
        }
        return Optional.of(new ModelInstance(model));
    }

    /**
     * Calculates the width and height of the given model
     */
    private boolean calculateDimensions(String name) {
        Model model = this.assets.get(this.getFilename(name), false);
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
        this.assets.dispose();
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
