package net.lugocorp.kingdom.engine.render;
import net.lugocorp.kingdom.engine.AudioVideo;
import net.lugocorp.kingdom.engine.assets.ModelLoader;
import net.lugocorp.kingdom.engine.assets.TextureLoader;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
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
    private TextureLoader textures;
    private String modelName = "PLACEHOLDER";
    private Optional<String> textureName = Optional.empty();
    private float alpha = 1f;
    @FieldSerializer.Optional("model")
    protected Optional<ModelInstance> model = Optional.empty();
    @FieldSerializer.Optional("textureOverride")
    protected Optional<Texture> textureOverride = Optional.empty();

    /**
     * Called when this Modellable is loaded from a saved Game file
     */
    public void rehydrateFromKryo(AudioVideo av) {
        this.textures = av.loaders.textures;
        this.models = av.loaders.models;
    }

    /**
     * Sets the overriding Material associated with this Modellable
     */
    public void setMaterial(String name) {
        this.textureName = Optional.of(name);
        this.textureOverride = Optional.empty();
    }

    /**
     * Returns the name associated with the current model
     */
    public String getModelName() {
        return this.modelName;
    }

    /**
     * Perform the final stage for instantiating our ModelInstance
     */
    protected void setupModelInstance(ModelInstance model) {
        // No-op in the base class
    }

    /**
     * Retrieves this Modellable's ModelInstance if one exists
     */
    public Optional<ModelInstance> getModelInstance() {
        // Load the ModelInstance if it's not present
        if (!this.model.isPresent()) {
            this.model = this.models.createModelInstance(this.modelName);
            this.model.ifPresent((ModelInstance model) -> {
                this.applyAlpha(model);
                this.resetModelPosition();
                this.setupModelInstance(model);
            });
        }
        // Load an override Texture if we've requested one and it's not present
        if (this.model.isPresent() && this.textureName.isPresent() && !this.textureOverride.isPresent()) {
            this.textureOverride = this.textures.getTexture(this.textureName.get());
            if (this.textureOverride.isPresent()) {
                this.model.get().materials.first()
                        .set(new TextureAttribute(TextureAttribute.Diffuse, this.textureOverride.get()));
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
        this.textures = av.loaders.textures;
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
        this.textureName.ifPresent((String s) -> this.textures.checkForUnload(s));
    }

    /**
     * Renders this Modellable's Model if it has one
     */
    public void render(ModelBatch batch, Environment environment) {
        this.getModelInstance().ifPresent((ModelInstance model) -> batch.render(model, environment));
    }
}
