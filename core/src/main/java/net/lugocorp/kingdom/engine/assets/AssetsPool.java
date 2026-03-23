package net.lugocorp.kingdom.engine.assets;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import java.util.Optional;

/**
 * Wraps the logic for loading any built-in or mod assets into the game
 */
public abstract class AssetsPool<T> {
    private final AssetManager internal = new AssetManager();
    private final Class<T> classInstance;
    private final String extension;

    public AssetsPool(Class<T> classInstance, String extension) {
        this.classInstance = classInstance;
        this.extension = extension;
    }

    /**
     * Returns the filename for the given asset
     */
    protected String getFilename(String name) {
        String filepath = String.format("%s.%s", name, this.extension);
        return Gdx.files.internal(filepath).path();
    }

    /**
     * Returns this asset if it has been loaded and nothing otherwise
     */
    protected Optional<T> getAsset(String name) {
        AssetManager assets = this.internal;
        String filename = this.getFilename(name);
        assets.update();
        T asset = assets.get(filename, false);
        if (asset == null) {
            if (!assets.contains(filename, this.classInstance)) {
                assets.load(filename, this.classInstance);
            }
            return Optional.empty();
        }
        return Optional.of(asset);
    }

    /**
     * Unloads the assets associated with the given name if they are no longer
     * needed
     */
    public void checkForUnload(String name) {
        String filename = this.getFilename(name);
        AssetManager assets = this.internal;
        if (assets.isLoaded(filename) && assets.getReferenceCount(filename) == 0) {
            assets.unload(filename);
        }
    }

    /**
     * Calls into the AssetManagers' dispose() methods
     */
    public void dispose() {
        this.internal.dispose();
    }
}
