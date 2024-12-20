package net.lugocorp.kingdom.engine;
import net.lugocorp.kingdom.engine.assets.ModelLoader;
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
    private ModelLoader models;
    private String modelName = "PLACEHOLDER";
    private float alpha = 1f;
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
            this.model = this.models.createModelInstance(this.modelName);
            this.model.ifPresent((ModelInstance model) -> {
                this.applyAlpha(model);
                this.resetModelPosition();
            });
        }
        return this.model;
    }

    /**
     * Triggers a new Model load request for this object
     */
    public void setModelInstance(ModelLoader assets, String name) {
        this.model = Optional.empty();
        this.modelName = name;
        this.models = assets;
    }

    /**
     * Moves this object's model to the correct location in grid space
     */
    protected void resetModelPosition() {
        // No-op to be implemented by subclasses
    }

    /**
     * Tells this object how transparent it should be
     */
    public void setAlpha(float alpha) {
        this.alpha = Math.max(0f, Math.min(1f, alpha));
        this.getModelInstance().ifPresent((ModelInstance model) -> this.applyAlpha(model));
    }

    /**
     * Applies the given alpha value to our model
     */
    protected void applyAlpha(ModelInstance model) {
        for (Material m : this.model.get().materials) {
            m.set(new BlendingAttribute(false, GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, this.alpha));
        }
    }

    /**
     * Unloads the assets associated with this object if they are no longer needed
     */
    protected void dispose() {
        this.models.checkForUnload(this.modelName);
    }

    /**
     * Renders this Modellable's Model if it has one
     */
    public void render(ModelBatch batch, Environment environment) {
        this.getModelInstance().ifPresent((ModelInstance model) -> batch.render(model, environment));
    }
}
