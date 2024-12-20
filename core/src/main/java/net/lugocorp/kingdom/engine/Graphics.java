package net.lugocorp.kingdom.engine;
import net.lugocorp.kingdom.engine.assets.ModelLoader;
import net.lugocorp.kingdom.engine.assets.SpriteLoader;
import net.lugocorp.kingdom.engine.shaders.OutlineShader;
import net.lugocorp.kingdom.engine.shaders.PreviewShader;
import net.lugocorp.kingdom.engine.shaders.ToonShader;
import net.lugocorp.kingdom.utils.mods.ModAssetsMap;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.utils.ShaderProvider;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Contains all the objects used to render things in the application
 */
public class Graphics {
    public final ShapeRenderer shapes = new ShapeRenderer();
    public final SpriteBatch sprites = new SpriteBatch();
    public final Loaders loaders = new Loaders();
    public final Fonts fonts = new Fonts();
    public final ModelBatch models;
    public final ModelBatch outlines;
    public final ModelBatch previews;

    public Graphics() {
        this.models = new ModelBatch(new BasicShaderProvider(new ToonShader()));
        this.outlines = new ModelBatch(new BasicShaderProvider(new OutlineShader()));
        this.previews = new ModelBatch(new BasicShaderProvider(new PreviewShader()));

        // Initialize shader programs
        this.getPreviewShader().init();
        this.getOutlineShader().init();
        this.getToonShader().init();
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
        this.loaders.models.dispose();
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
        public final BitmapFont header;
        public final BitmapFont button;
        public final BitmapFont basic;

        Fonts() {
            FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("ui/DejaVuSans.ttf"));
            FreeTypeFontParameter param = new FreeTypeFontParameter();
            param.size = 36;
            this.header = generator.generateFont(param);
            param.size = 18;
            this.basic = generator.generateFont(param);
            param.color = new Color(0.6f, 1f, 1f, 1f);
            param.size = 24;
            this.button = generator.generateFont(param);
            generator.dispose();
        }
    }

    /**
     * This nested class contains all asset loaders
     */
    public static class Loaders {
        public final SpriteLoader sprites;
        public final ModelLoader models;

        private Loaders() {
            ModAssetsMap modAssetsMap = new ModAssetsMap();
            this.sprites = new SpriteLoader(modAssetsMap);
            this.models = new ModelLoader(modAssetsMap);
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
