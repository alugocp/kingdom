package net.lugocorp.kingdom.engine;
import net.lugocorp.kingdom.engine.assets.FontService;
import net.lugocorp.kingdom.engine.assets.ModelLoader;
import net.lugocorp.kingdom.engine.assets.MusicLoader;
import net.lugocorp.kingdom.engine.assets.SoundLoader;
import net.lugocorp.kingdom.engine.assets.SpriteLoader;
import net.lugocorp.kingdom.engine.assets.TextureLoader;
import net.lugocorp.kingdom.engine.shaders.ShaderZoo;
import net.lugocorp.kingdom.mods.ModAssetsMap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.utils.ShaderProvider;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Contains all the objects used to render things and make sounds in the
 * application
 */
public class AudioVideo {
    public final ShapeRenderer shapes = new ShapeRenderer();
    public final SpriteBatch sprites = new SpriteBatch();
    public final SpriteBatch special = new SpriteBatch();
    public final FontService fonts = new FontService();
    public final ShaderZoo shaders = new ShaderZoo();
    public final Settings settings = new Settings();
    public final ModelBatch outlines;
    public final ModelBatch previews;
    public final ModelBatch models;
    public final Loaders loaders;

    public AudioVideo() {
        this.outlines = new ModelBatch(new BasicShaderProvider(this.shaders.outline));
        this.previews = new ModelBatch(new BasicShaderProvider(this.shaders.preview));
        this.models = new ModelBatch(new BasicShaderProvider(this.shaders.toon));
        this.loaders = new Loaders(this.settings);

        // Initialize shader programs
        this.special.setShader(this.shaders.element);
        this.shaders.toon.setTextureLoader(this.loaders.textures);
        this.shaders.outline.init();
        this.shaders.preview.init();
        this.shaders.toon.init();
    }

    /**
     * Calls the resources' dispose() methods
     */
    public void dispose() {
        this.special.dispose();
        this.sprites.dispose();
        this.shapes.dispose();
        this.models.dispose();
        this.outlines.dispose();
        this.previews.dispose();
        this.loaders.sprites.dispose();
        this.loaders.models.dispose();
        this.loaders.sounds.dispose();
        this.fonts.dispose();
    }

    /**
     * This nested class contains all asset loaders
     */
    public static class Loaders {
        public final TextureLoader textures;
        public final SpriteLoader sprites;
        public final ModelLoader models;
        public final SoundLoader sounds;
        public final MusicLoader music;

        private Loaders(Settings settings) {
            ModAssetsMap modAssetsMap = new ModAssetsMap();
            this.sprites = new SpriteLoader(modAssetsMap);
            this.models = new ModelLoader(modAssetsMap);
            this.textures = new TextureLoader(modAssetsMap);
            this.sounds = new SoundLoader(modAssetsMap, settings);
            this.music = new MusicLoader(modAssetsMap, settings);
        }
    }

    /**
     * Internal class to provide the given Shader
     */
    private static class BasicShaderProvider implements ShaderProvider {
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
