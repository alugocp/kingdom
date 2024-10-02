package net.lugocorp.kingdom.engine;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Contains all the objects used to render things in the application
 */
public class Graphics {
    public final ShapeRenderer shapes = new ShapeRenderer();
    public final SpriteBatch sprites = new SpriteBatch();
    public final ModelBatch models = new ModelBatch();
    public final Fonts fonts = new Graphics.Fonts();

    public void dispose() {
        this.sprites.dispose();
        this.shapes.dispose();
        this.models.dispose();
        this.fonts.basic.dispose();
    }

    public static class Fonts {
        public final BitmapFont basic = new BitmapFont();
    }
}
