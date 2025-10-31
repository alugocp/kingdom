package net.lugocorp.kingdom.menu.game;
import net.lugocorp.kingdom.engine.AudioVideo;
import net.lugocorp.kingdom.engine.render.Modellable;
import net.lugocorp.kingdom.math.Coords;
import net.lugocorp.kingdom.math.Rect;
import net.lugocorp.kingdom.menu.MenuNode;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.math.Matrix4;

/**
 * This MenuNode renders a ModelInstance in a Menu
 */
public class ModelNode implements MenuNode {
    private static final int MARGIN = 10;
    private final Modellable model = new Modellable();
    private final Matrix4 transform = new Matrix4();
    private final PerspectiveCamera camera;
    private final Environment environment;
    private final String name;
    private boolean loaded = false;
    private float modelHeight = 1f;
    private float modelWidth = 1f;
    private float scale = 1f;

    // TODO figure out how to include the entire unit model in the preview (right
    // now the back part gets cut off, but if we raise the z position then some
    // front faces are missing)
    public ModelNode(AudioVideo av, PerspectiveCamera camera, Environment environment, String name) {
        this.model.setModelInstance(av, name);
        this.environment = environment;
        this.camera = camera;
        this.name = name;
    }

    /**
     * This method runs when the ModelInstance is fully loaded and ready for use
     */
    private final void modelLoaded(AudioVideo av, Rect bounds) {
        this.modelHeight = av.loaders.models.getModelHeight(this.name);
        this.modelWidth = av.loaders.models.getModelWidth(this.name);

        // The dimensions returned from our ModelLoader should be scaled according to
        // the viewport's width in the range of (0, 1)
        this.scale = (bounds.w - (ModelNode.MARGIN * 2)) / (Coords.SIZE.x * this.modelWidth);
        this.transform.scale(this.scale, this.scale, this.scale);
    }

    /** {@inheritdoc} */
    @Override
    public int getHeight() {
        final float halfh = Coords.SIZE.y / 2f;
        return (ModelNode.MARGIN * 2) + (int) (this.modelHeight * this.scale * halfh);
    }

    /** {@inheritdoc} */
    @Override
    public void draw(AudioVideo av, Rect bounds) {
        if (!this.loaded) {
            if (this.model.getModelInstance().isPresent()) {
                this.modelLoaded(av, bounds);
                this.loaded = true;
            } else {
                return;
            }
        }

        // OpenGL represents the viewport as a grid from top-left (-1, 1) to
        // bottom-right (1, -1), so the middle of the screen is at (0, 0)
        final float halfw = Coords.SIZE.x / 2f;
        final float halfh = Coords.SIZE.y / 2f;
        this.transform.setTranslation((bounds.x + (bounds.w / 2) - halfw) / halfw,
                -(bounds.y + bounds.h - ModelNode.MARGIN - halfh) / halfh, -1.0f);

        // Render the model preview
        av.shaders.preview.setProjViewMatrix(this.transform);
        av.previews.begin(this.camera);
        this.model.render(av.previews, this.environment);
        av.previews.end();
    }
}
