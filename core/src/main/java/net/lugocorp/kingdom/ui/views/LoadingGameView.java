package net.lugocorp.kingdom.ui.views;
import net.lugocorp.kingdom.assets.AssetsLoader;
import net.lugocorp.kingdom.assets.SpritesLoader;
import net.lugocorp.kingdom.engine.GameGraphics;
import net.lugocorp.kingdom.engine.Graphics;
import net.lugocorp.kingdom.events.EventHandlerBundle;
import net.lugocorp.kingdom.game.Game;
import net.lugocorp.kingdom.utils.Consumer;
import net.lugocorp.kingdom.world.World;
import net.lugocorp.kingdom.world.WorldGenerator;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;

/**
 * View for when we're loading a new game
 */
public class LoadingGameView implements View {
    private final Graphics graphics;
    private final EventHandlerBundle events;
    private Consumer<View> navigate;
    private SpritesLoader sprites;
    private AssetsLoader assets;

    public LoadingGameView(Graphics graphics, EventHandlerBundle events) {
        this.graphics = graphics;
        this.events = events;
    }

    /** {@inheritdoc} */
    @Override
    public Color getBackgroundColor() {
        return new Color(0.2f, 0.2f, 0.2f, 1.0f);
    }

    /** {@inheritdoc} */
    @Override
    public void start(Consumer<View> navigate) {
        this.navigate = navigate;
        this.sprites = new SpritesLoader();
        this.sprites.loadAndRegister();
        this.assets = new AssetsLoader(new AssetManager());
        this.assets.load();
    }

    /** {@inheritdoc} */
    @Override
    public void render() {
        this.assets.doOnLoad(() -> {
            Game game = new Game(new GameGraphics(this.graphics, this.assets, this.sprites), this.events,
                    new World(10, 5));
            new WorldGenerator().generateWorld(game);
            this.navigate.run(new GameView(game));
        });
    }

    /** {@inheritdoc} */
    @Override
    public void resize(int w, int h) {
    }

    /** {@inheritdoc} */
    @Override
    public void dispose() {
    }
}
