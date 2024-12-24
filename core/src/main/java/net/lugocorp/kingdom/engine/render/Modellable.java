package net.lugocorp.kingdom.engine.render;
import net.lugocorp.kingdom.engine.AudioVideo;
import net.lugocorp.kingdom.engine.assets.MaterialLoader;
import net.lugocorp.kingdom.engine.assets.ModelLoader;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.esotericsoftware.kryo.serializers.FieldSerializer;
import java.util.Optional;

/**
 * Represents any object that can have an associated model and some fixed
 * position
 */
public class Modellable {
    @FieldSerializer.Optional("models")
    private ModelLoader models;
    @FieldSerializer.Optional("materials")
    private MaterialLoader materials;
    private String modelName = "PLACEHOLDER";
    private Optional<String> materialName = Optional.empty();
    private float alpha = 1f;
    @FieldSerializer.Optional("model")
    protected Optional<ModelInstance> model = Optional.empty();
    @FieldSerializer.Optional("materialOverride")
    protected Optional<Material> materialOverride = Optional.empty();

    /**
     * Called when this Modellable is loaded from a saved Game file
     */
    public void rehydrateFromKryo(AudioVideo av) {
        this.materials = av.loaders.materials;
        this.models = av.loaders.models;
    }

    /**
     * Sets the overriding Material associated with this Modellable
     */
    public void setMaterial(String name) {
        this.materialName = Optional.of(name);
        this.materialOverride = Optional.empty();
    }

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
        if (this.model.isPresent() && this.materialName.isPresent() && !this.materialOverride.isPresent()) {
            this.materialOverride = this.materials.getMaterial(this.materialName.get());
            if (this.materialOverride.isPresent()) {
                this.model.get().materials.clear();
                this.model.get().materials.add(this.materialOverride.get());
            } else {
                return Optional.empty();
            }
        }
        return this.model;
    }

    /**
     * Triggers a new Model load request for this object
     */
    public void setModelInstance(AudioVideo av, String name) {
        this.model = Optional.empty();
        this.modelName = name;
        this.models = av.loaders.models;
        this.materials = av.loaders.materials;
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
