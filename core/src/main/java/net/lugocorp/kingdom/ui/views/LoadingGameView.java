package net.lugocorp.kingdom.ui.views;
import net.lugocorp.kingdom.engine.Graphics;
import net.lugocorp.kingdom.game.Game;
import net.lugocorp.kingdom.game.events.AllEventHandlers;
import net.lugocorp.kingdom.game.model.Generator;
import net.lugocorp.kingdom.game.world.World;
import net.lugocorp.kingdom.game.world.WorldGenerator;
import net.lugocorp.kingdom.utils.ModLoader;
import com.badlogic.gdx.graphics.Color;
import java.util.function.Consumer;

/**
 * View for when we're loading a new game
 */
public class LoadingGameView implements View {
    private final AllEventHandlers events = new AllEventHandlers();
    private final Graphics graphics;
    private Consumer<View> navigate;

    public LoadingGameView(Graphics graphics) {
        this.graphics = graphics;
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

        // TODO move this to another loading screen
        ModLoader mods = new ModLoader();
        for (String filepath : mods.getMods()) {
            System.out.println(String.format("Loading mod %s...", filepath));
            try {
                mods.loadMod(filepath, this.events);
            } catch (Exception e) {
                System.err.println(String.format("Error while loading mod %s", filepath));
                e.printStackTrace();
            }
        }
    }

    /** {@inheritdoc} */
    @Override
    public void render() {
        Game game = new Game(this.graphics, this.events, new World(10, 5));
        GameView view = new GameView(game, this.graphics);
        game.generator = new Generator(view);
        game.mechanics.auction.init(game);
        game.mechanics.pools.init(game);
        new WorldGenerator().generateWorld(view);
        this.navigate.accept(view);
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
