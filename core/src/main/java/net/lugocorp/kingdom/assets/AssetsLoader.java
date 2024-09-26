package net.lugocorp.kingdom.assets;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Model;

/**
 * Wraps the logic for loading 3D model assets into the game
 */
public class AssetsLoader {
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
    }

    /**
     * Creates a new ModelInstance for the Model with the given name
     */
    public ModelInstance createModelInstance(String name) {
        Model model = assets.get(String.format("%s.g3db", name), Model.class);
        return new ModelInstance(model);
    }

    /**
     * Calls into the AssetManager's dispose() method
     */
    public void dispose() {
        this.assets.dispose();
    }
}
