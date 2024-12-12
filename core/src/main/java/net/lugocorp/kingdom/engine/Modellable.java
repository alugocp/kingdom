package net.lugocorp.kingdom.engine;
import net.lugocorp.kingdom.engine.assets.AssetsLoader;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import java.util.Optional;

/**
 * Represents any object that can have an associated model and some fixed
 * position
 */
public class Modellable {
    private AssetsLoader assets;
    private String modelName = "PLACEHOLDER";
    protected Optional<ModelInstance> model = Optional.empty();

    /**
     * Returns the name associated with the current model
     */
    public String getModelName() {
        return this.modelName;
    }

    /**
     * Retrieves this Modellable's ModelInstance if one exists
     */
    public Optional<ModelInstance> getModelInstance() {
        if (!this.model.isPresent()) {
            this.model = this.assets.createModelInstance(this.modelName);
            this.model.ifPresent((ModelInstance model) -> {
                for (Material m : model.materials) {
                    m.set(new BlendingAttribute(false, GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, 1.0f));
                }
                this.resetModelPosition();
            });
        }
        return this.model;
    }

    /**
     * Triggers a new Model load request for this object
     */
    public void setModelInstance(AssetsLoader assets, String name) {
        this.model = Optional.empty();
        this.modelName = name;
        this.assets = assets;
    }

    /**
     * Moves this object's model to the correct location in grid space
     */
    protected void resetModelPosition() {
        // No-op to be implemented by subclasses
    }

    /**
     * Renders this Modellable's Model if it has one
     */
    public void render(ModelBatch batch, Environment environment) {
        this.model.ifPresent((ModelInstance model) -> batch.render(model, environment));
    }
}
