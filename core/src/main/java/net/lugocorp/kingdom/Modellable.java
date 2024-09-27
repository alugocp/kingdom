package net.lugocorp.kingdom;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import java.util.Optional;
import net.lugocorp.kingdom.assets.AssetsLoader;

/**
 * Represents any object that can have an associated in-game model
 */
public abstract class Modellable {
    protected Optional<ModelInstance> model = Optional.empty();

    /**
     * Retrieves this Modellable's ModelInstance if one exists
     */
    public Optional<ModelInstance> getModelInstance() {
        return this.model;
    }

    /**
     * Applies a ModelInstance to this object and sets its position in the render
     * area
     */
    public abstract void setModelInstance(AssetsLoader assets, String name, int x, int y, int z);
}
