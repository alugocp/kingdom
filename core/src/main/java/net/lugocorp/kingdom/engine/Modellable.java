package net.lugocorp.kingdom.engine;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import java.util.Optional;
import net.lugocorp.kingdom.assets.AssetsLoader;

/**
 * Represents any object that can have an associated in-game model
 */
public abstract class Modellable {
    protected Optional<ModelInstance> model = Optional.empty();
    protected int x;
    protected int y;

    public Modellable(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Gets the current x position (in grid space)
     */
    public int getX() {
        return this.x;
    }

    /**
     * Gets the current y position (in grid space)
     */
    public int getY() {
        return this.y;
    }

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
    public abstract void setModelInstance(AssetsLoader assets, String name);
}
