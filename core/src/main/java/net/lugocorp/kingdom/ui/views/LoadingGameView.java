package net.lugocorp.kingdom.ui.views;
import net.lugocorp.kingdom.engine.GameGraphics;
import net.lugocorp.kingdom.engine.Graphics;
import net.lugocorp.kingdom.engine.assets.AssetsLoader;
import net.lugocorp.kingdom.engine.assets.SpritesLoader;
import net.lugocorp.kingdom.game.Game;
import net.lugocorp.kingdom.game.events.AllEventHandlers;
import net.lugocorp.kingdom.game.model.Generator;
import net.lugocorp.kingdom.game.world.World;
import net.lugocorp.kingdom.game.world.WorldGenerator;
import net.lugocorp.kingdom.utils.Consumer;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;

/**
 * View for when we're loading a new game
 */
public class LoadingGameView implements View {
    private final Graphics graphics;
    private final AllEventHandlers events;
    private Consumer<View> navigate;
    private SpritesLoader sprites;
    private AssetsLoader assets;

    public LoadingGameView(Graphics graphics, AllEventHandlers events) {
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
            // TODO clean up the dependencies between these classes and make sure bad
            // initialization state is impossible
            Game game = new Game(new GameGraphics(this.graphics, this.assets, this.sprites), this.events,
                    new World(10, 5));
            GameView view = new GameView(game);
            game.generator = new Generator(view);
            new WorldGenerator().generateWorld(game);
            this.navigate.run(view);
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
