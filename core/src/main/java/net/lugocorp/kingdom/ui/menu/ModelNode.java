package net.lugocorp.kingdom.ui.menu;
import net.lugocorp.kingdom.engine.Graphics;
import net.lugocorp.kingdom.engine.assets.AssetsLoader;
import net.lugocorp.kingdom.utils.math.Coords;
import net.lugocorp.kingdom.utils.math.Point;
import net.lugocorp.kingdom.utils.math.Rect;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Matrix4;

/**
 * This MenuNode renders a ModelInstance in a Menu
 */
public class ModelNode implements MenuNode {
    private static int MARGIN = 10;
    private PerspectiveCamera camera;
    private Environment environment;
    private final ModelInstance model;
    private final float modelHeight;
    private final float modelWidth;
    private float scale = 1f;

    public ModelNode(PerspectiveCamera camera, Environment environment, AssetsLoader assets, String name) {
        this.model = assets.createModelInstance(name);
        this.modelHeight = assets.getModelHeight(name);
        this.modelWidth = assets.getModelWidth(name);
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
        int y = Gdx.graphics.getHeight() - bounds.y - ModelNode.MARGIN - 1;
        graphics.previews.begin(this.camera);
        Matrix4 proj = new Matrix4();
        // proj.setScale(this.scale, this.scale, this.scale);
        proj.setTranslation(Coords.grid.vector(0, 1));
        // TODO set the right scale and translation for this model and make sure it is
        // rendered after menu shapes
        graphics.getPreviewShader().setProjViewMatrix(proj);
        graphics.previews.render(this.model, this.environment);
        graphics.previews.end();
    }

    /** {@inheritdoc} */
    @Override
    public void click(Menu menu, Rect bounds, Point p) {
    }
}
