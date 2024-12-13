package net.lugocorp.kingdom.engine;
import net.lugocorp.kingdom.engine.assets.AssetsLoader;
import net.lugocorp.kingdom.engine.assets.SpritesLoader;
import net.lugocorp.kingdom.engine.shaders.OutlineShader;
import net.lugocorp.kingdom.engine.shaders.PreviewShader;
import net.lugocorp.kingdom.engine.shaders.ToonShader;
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
    public final Loaders loaders;
    public final ShapeRenderer shapes;
    public final SpriteBatch sprites;
    public final ModelBatch models;
    public final ModelBatch outlines;
    public final ModelBatch previews;
    public final Fonts fonts;

    public Graphics(ToonShader toon, OutlineShader outline, PreviewShader preview, AssetsLoader assets,
            SpritesLoader sprites) {
        this.shapes = new ShapeRenderer();
        this.sprites = new SpriteBatch();
        this.models = new ModelBatch(new Graphics.BasicShaderProvider(toon));
        this.outlines = new ModelBatch(new Graphics.BasicShaderProvider(outline));
        this.previews = new ModelBatch(new Graphics.BasicShaderProvider(preview));
        this.fonts = new Graphics.Fonts();
        this.loaders = new Loaders(assets, sprites);
    }

    /**
     * Calls the resources' dispose() methods
     */
    public void dispose() {
        this.sprites.dispose();
        this.shapes.dispose();
        this.models.dispose();
        this.outlines.dispose();
        this.previews.dispose();
        this.fonts.basic.dispose();
        this.loaders.sprites.dispose();
        this.loaders.assets.dispose();
    }

    /**
     * Returns the ToonShader associated with this object
     */
    public ToonShader getToonShader() {
        return (ToonShader) (((Graphics.BasicShaderProvider) (this.models.getShaderProvider())).shader);
    }

    /**
     * Returns the OutlineShader associated with this object
     */
    public OutlineShader getOutlineShader() {
        return (OutlineShader) (((Graphics.BasicShaderProvider) (this.outlines.getShaderProvider())).shader);
    }

    /**
     * Returns the PreviewShader associated with this object
     */
    public PreviewShader getPreviewShader() {
        return (PreviewShader) (((Graphics.BasicShaderProvider) (this.previews.getShaderProvider())).shader);
    }

    /**
     * This nested class contains all the application fonts
     */
    public static class Fonts {
        public final BitmapFont basic = new BitmapFont();
        public final BitmapFont header = new BitmapFont();
        public final BitmapFont button = new BitmapFont();

        Fonts() {
            this.button.setColor(new Color(0.6f, 1f, 1f, 1f));
        }
    }

    /**
     * This nested class contains all asset loaders
     */
    public static class Loaders {
        public final SpritesLoader sprites;
        public final AssetsLoader assets;

        Loaders(AssetsLoader assets, SpritesLoader sprites) {
            this.sprites = sprites;
            this.assets = assets;
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
