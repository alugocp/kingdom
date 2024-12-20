package net.lugocorp.kingdom.engine.assets;
import net.lugocorp.kingdom.utils.mods.ModAssetsMap;
import net.lugocorp.kingdom.utils.mods.ModLoader;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.ExternalFileHandleResolver;
import java.util.Optional;

/**
 * Wraps the logic for loading any built-in or mod assets into the game
 */
public abstract class AssetsPool<T> {
    private final AssetManager external = new AssetManager(new ExternalFileHandleResolver());
    private final AssetManager internal = new AssetManager();
    private final ModAssetsMap modAssetsMap;
    private final Class<T> classInstance;
    private final String extension;

    public AssetsPool(ModAssetsMap modAssetsMap, Class<T> classInstance, String extension) {
        this.classInstance = classInstance;
        this.modAssetsMap = modAssetsMap;
        this.extension = extension;
    }

    /**
     * Returns the mod assets map object
     */
    public ModAssetsMap getModAssetsMap() {
        return this.modAssetsMap;
    }

    /**
     * Returns the appropriate AssetManager to handle the given resource
     */
    protected AssetManager getAssetManager(String name) {
        if (this.modAssetsMap.get(String.format("%s.%s", name, this.extension)).isPresent()) {
            return this.external;
        }
        return this.internal;
    }

    /**
     * Returns the filename for the given asset
     */
    protected String getFilename(String name) {
        String filepath = String.format("%s.%s", name, this.extension);
        return this.modAssetsMap
                .get(filepath).map((String key) -> Gdx.files
                        .external(String.format("%s/%s/%s", ModLoader.ASSETS_BASE, key, filepath)).path())
                .orElse(filepath);
    }

    /**
     * Returns this asset if it has been loaded and nothing otherwise
     */
    protected Optional<T> getAsset(String name) {
        AssetManager assets = this.getAssetManager(name);
        assets.update();
        String filename = this.getFilename(name);
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
        AssetManager assets = this.getAssetManager(name);
        if (assets.getReferenceCount(filename) == 0) {
            assets.unload(filename);
        }
    }

    /**
     * Calls into the AssetManagers' dispose() methods
     */
    public void dispose() {
        this.internal.dispose();
        this.external.dispose();
    }
}
