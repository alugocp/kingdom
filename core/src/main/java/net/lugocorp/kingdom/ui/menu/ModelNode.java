package net.lugocorp.kingdom.ui.menu;
import net.lugocorp.kingdom.engine.Graphics;
import net.lugocorp.kingdom.engine.assets.ModelLoader;
import net.lugocorp.kingdom.engine.render.Modellable;
import net.lugocorp.kingdom.utils.math.Coords;
import net.lugocorp.kingdom.utils.math.Point;
import net.lugocorp.kingdom.utils.math.Rect;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.math.Matrix4;

/**
 * This MenuNode renders a ModelInstance in a Menu
 */
public class ModelNode implements MenuNode {
    private static int MARGIN = 10;
    private final Modellable model = new Modellable();
    private final float modelHeight;
    private final float modelWidth;
    private PerspectiveCamera camera;
    private Environment environment;
    private float scale = 1f;

    public ModelNode(PerspectiveCamera camera, Environment environment, ModelLoader models, String name) {
        this.model.setModelInstance(models, name);
        this.modelHeight = models.getModelHeight(name);
        this.modelWidth = models.getModelWidth(name);
        this.environment = environment;
        this.camera = camera;
    }

    /** {@inheritdoc} */
    @Override
    public int getHeight() {
        return (ModelNode.MARGIN * 2) + (int) (this.modelHeight * this.scale);
    }

    /** {@inheritdoc} */
    @Override
    public void pack(int width) {
        int maxWidth = width - (ModelNode.MARGIN * 2);
        this.scale = (float) maxWidth / this.modelWidth;
    }

    /** {@inheritdoc} */
    @Override
    public void draw(Graphics graphics, Rect bounds) {
        Matrix4 proj = new Matrix4();
        float halfw = Coords.SIZE.x / 2f;
        float halfh = Coords.SIZE.y / 2f;
        float scale = (bounds.w - (ModelNode.MARGIN * 2)) / (halfw * this.modelWidth);
        proj.scale(scale, scale, scale);
        proj.setTranslation(((bounds.x + (bounds.w / 2)) - halfw) / halfw,
                -(bounds.y + bounds.h - ModelNode.MARGIN - halfh) / halfh, 0f);
        graphics.getPreviewShader().setProjViewMatrix(proj);
        graphics.previews.begin(this.camera);
        this.model.render(graphics.previews, this.environment);
        graphics.previews.end();
    }

    /** {@inheritdoc} */
    @Override
    public void click(Menu menu, Rect bounds, Point p) {
    }
}
