package net.lugocorp.kingdom.engine;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import java.util.Optional;
import net.lugocorp.kingdom.assets.AssetsLoader;

/**
 * Represents any object that can have an associated in-game model
 */
public abstract class Modellable {
    private float h = 0f;
    protected Optional<ModelInstance> model = Optional.empty();
    protected int x;
    protected int y;

    public Modellable(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Converts this object's current grid space position into the vector position
     * for its model
     */
    protected abstract Vector3 getPositionVector();

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
     * Gets the current y position (in grid space)
     */
    public float getModelHeight() {
        return this.h;
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
    public void setModelInstance(AssetsLoader assets, String name) {
        ModelInstance model = assets.createModelInstance(name);
        this.h = assets.getModelHeight(name);
        this.model = Optional.of(model);
        this.resetModelPosition();
    }

    /**
     * Moves this object's model to the correct location in grid space
     */
    public void resetModelPosition() {
        this.model.ifPresent((ModelInstance model) -> model.transform.setTranslation(this.getPositionVector()));
    }
}
