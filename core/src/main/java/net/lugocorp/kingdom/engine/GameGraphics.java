package net.lugocorp.kingdom.engine;
import net.lugocorp.kingdom.engine.assets.AssetsLoader;
import net.lugocorp.kingdom.engine.assets.SpritesLoader;

/**
 * Extra graphical resources for a GameView
 */
public class GameGraphics extends Graphics {
    public final Loaders loaders;

    public GameGraphics(Graphics g, AssetsLoader assets, SpritesLoader sprites) {
        super(g.shapes, g.sprites, g.models, g.outlines, g.previews, g.fonts);
        this.loaders = new Loaders(assets, sprites);
    }

    /**
     * Other views can dispose the resources that come from Graphics
     */
    @Override
    public void dispose() {
        this.loaders.sprites.dispose();
        this.loaders.assets.dispose();
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
}
