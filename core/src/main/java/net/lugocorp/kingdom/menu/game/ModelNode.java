package net.lugocorp.kingdom.menu.game;
import net.lugocorp.kingdom.engine.AudioVideo;
import net.lugocorp.kingdom.engine.render.Modellable;
import net.lugocorp.kingdom.math.Coords;
import net.lugocorp.kingdom.math.Point;
import net.lugocorp.kingdom.math.Rect;
import net.lugocorp.kingdom.menu.MenuNode;
import net.lugocorp.kingdom.utils.Tuple;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Matrix4;
import java.util.Optional;

/**
 * This MenuNode renders a ModelInstance in a Menu
 */
public class ModelNode implements MenuNode {
    private static final int MAX_H = 200;
    private static final int MARGIN = 10;
    private final Modellable model = new Modellable();
    private final Point size = new Point();
    private final Environment environment;
    private final Camera camera;
    private final String name;
    private boolean loaded = false;
    private float modelHeight = 1f;
    private float modelWidth = 1f;

    public ModelNode(AudioVideo av, Camera camera, Environment environment, String name,
            Optional<Tuple<Integer, String>> material) {
        this.model.setModelInstance(av, name);
        material.ifPresent((Tuple<Integer, String> t) -> this.model.setMaterial(t.b, t.a));
        this.environment = environment;
        this.camera = camera;
        this.name = name;
    }

    /**
     * Returns true if the associated model has been fully loaded
     */
    public boolean isLoaded() {
        return this.loaded;
    }

    /**
     * Returns the model associated with this MenuNode
     */
    public Optional<ModelInstance> getModel() {
        return this.model.getModelInstance();
    }

    /**
     * This method runs when the ModelInstance is fully loaded and ready for use
     */
    private final void modelLoaded(AudioVideo av, Rect bounds) {
        final Matrix4 transform = this.getModel().get().transform;
        this.modelHeight = av.loaders.models.getModelHeight(this.name);
        this.modelWidth = av.loaders.models.getModelWidth(this.name);

        // Get target dimensions
        final float maxw = bounds.w - (ModelNode.MARGIN * 2);
        final float maxh = ModelNode.MAX_H - (ModelNode.MARGIN * 2);
        this.size.set((int) maxw, (int) (maxw * this.modelHeight / this.modelWidth));
        if (this.size.y > maxh) {
            this.size.set((int) (maxh * this.modelWidth / this.modelHeight), (int) maxh);
        }

        // Read: (target dimension / half the screen dimension) / model dimension. This
        // is because OpenGL uses (-1, 1) for the coordinate grid, so we have to half
        // the screen width. Then we divide by the model width to make it a ratio.
        transform.scale((float) this.size.x * 2f / ((float) Coords.SIZE.x * this.modelWidth),
                (float) this.size.y * 2f / ((float) Coords.SIZE.y * this.modelHeight), 1f);
        transform.rotate(0f, 1f, 0f, 180f);
    }

    /** {@inheritdoc} */
    @Override
    public int getHeight() {
        return (ModelNode.MARGIN * 2) + this.size.y;
    }

    /** {@inheritdoc} */
    @Override
    public void draw(AudioVideo av, Rect bounds) {
        if (!this.loaded) {
            if (this.getModel().isPresent()) {
                this.modelLoaded(av, bounds);
                this.loaded = true;
            } else {
                return;
            }
        }

        // Just grab this for easy use
        final Matrix4 transform = this.getModel().get().transform;

        // OpenGL represents the viewport as a grid from top-left (-1, 1) to
        // bottom-right (1, -1), so the middle of the screen is at (0, 0)
        final float halfw = Coords.SIZE.x / 2f;
        final float halfh = Coords.SIZE.y / 2f;
        transform.setTranslation((bounds.x + (bounds.w / 2) - halfw) / halfw,
                -(bounds.y + bounds.h - ModelNode.MARGIN - halfh) / halfh, 0f);

        // Render the model preview
        av.shaders.preview.setProjViewMatrix(transform);
        av.previews.begin(this.camera);
        this.model.render(av.previews, this.environment);
        av.previews.end();
    }
}
