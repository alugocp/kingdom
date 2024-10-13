package net.lugocorp.kingdom.engine.assets;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.collision.BoundingBox;
import java.util.HashMap;
import java.util.Map;

/**
 * Wraps the logic for loading 3D model assets into the game
 */
public class AssetsLoader {
    private final Map<String, Float> heights = new HashMap<>();
    private final AssetManager assets;
    private boolean loading = true;

    public AssetsLoader(AssetManager assets) {
        this.assets = assets;
    }

    /**
     * Runs the given code when all our assets are loaded
     */
    public void doOnLoad(Runnable lambda) {
        if (this.loading && this.assets.update()) {
            this.loading = false;
            lambda.run();
        }
    }

    /**
     * Loads all the model assets to be used in the game
     */
    public void load() {
        this.assets.load("tile.g3db", Model.class);
        this.assets.load("crystal.g3db", Model.class);
        this.assets.load("mine.g3db", Model.class);
        this.assets.load("Selector.g3db", Model.class);
        this.assets.load("vault.g3db", Model.class);
    }

    /**
     * Creates a new ModelInstance for the Model with the given name
     */
    public ModelInstance createModelInstance(String name) {
        Model model = assets.get(String.format("%s.g3db", name), Model.class);
        return new ModelInstance(model);
    }

    /**
     * Returns the height of a given model by its name (calculates the height if
     * necessary)
     */
    public float getModelHeight(String name) {
        if (!this.heights.containsKey(name)) {
            Model model = assets.get(String.format("%s.g3db", name), Model.class);
            BoundingBox box = new BoundingBox();
            model.calculateBoundingBox(box);
            this.heights.put(name, box.getHeight());
        }
        return this.heights.get(name);
    }

    /**
     * Calls into the AssetManager's dispose() method
     */
    public void dispose() {
        this.assets.dispose();
    }
}
