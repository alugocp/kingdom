package net.lugocorp.kingdom.engine;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.utils.ShaderProvider;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Contains all the objects used to render things in the application
 */
public class Graphics {
    public final ShapeRenderer shapes;
    public final SpriteBatch sprites;
    public final ModelBatch models;
    public final Fonts fonts;

    /**
     * Constructor used for initial use
     */
    public Graphics(ToonShader shader) {
        this(new ShapeRenderer(), new SpriteBatch(), new ModelBatch(new ShaderProvider() {
            @Override
            public Shader getShader(Renderable r) {
                return shader;
            }
            @Override
            public void dispose() {
                shader.dispose();
            }
        }), new Graphics.Fonts());
    }

    /**
     * Constructor used for subclasses
     */
    Graphics(ShapeRenderer shapes, SpriteBatch sprites, ModelBatch models, Fonts fonts) {
        this.shapes = shapes;
        this.sprites = sprites;
        this.models = models;
        this.fonts = fonts;
    }

    /**
     * Calls the resources' dispose() methods
     */
    public void dispose() {
        this.sprites.dispose();
        this.shapes.dispose();
        this.models.dispose();
        this.fonts.basic.dispose();
    }

    /**
     * This nested class contains all the application fonts
     */
    public static class Fonts {
        public final BitmapFont basic = new BitmapFont();
        public final BitmapFont header = new BitmapFont();
        public final BitmapFont button = new BitmapFont();

        Fonts() {
            this.button.setColor(Color.TEAL);
        }
    }
}
