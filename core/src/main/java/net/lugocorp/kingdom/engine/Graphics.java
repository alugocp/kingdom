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
    public final ModelBatch outlines;
    public final Fonts fonts;

    /**
     * Constructor used for initial use
     */
    public Graphics(ToonShader toon, OutlineShader outline) {
        this(new ShapeRenderer(), new SpriteBatch(), new ModelBatch(new Graphics.BasicShaderProvider(toon)),
                new ModelBatch(new Graphics.BasicShaderProvider(outline)), new Graphics.Fonts());
    }

    /**
     * Constructor used for subclasses
     */
    Graphics(ShapeRenderer shapes, SpriteBatch sprites, ModelBatch models, ModelBatch outlines, Fonts fonts) {
        this.shapes = shapes;
        this.sprites = sprites;
        this.models = models;
        this.outlines = outlines;
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

    /**
     * Internal class to provide the given Shader
     */
    public static class BasicShaderProvider implements ShaderProvider {
        private final Shader shader;

        private BasicShaderProvider(Shader shader) {
            this.shader = shader;
        }

        @Override
        public Shader getShader(Renderable r) {
            return this.shader;
        }

        @Override
        public void dispose() {
            this.shader.dispose();
        }
    }
}
