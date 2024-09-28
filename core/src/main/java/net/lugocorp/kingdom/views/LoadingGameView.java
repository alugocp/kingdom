package net.lugocorp.kingdom.views;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import java.util.function.Function;
import net.lugocorp.kingdom.assets.AssetsLoader;
import net.lugocorp.kingdom.game.Game;
import net.lugocorp.kingdom.views.GameView;
import net.lugocorp.kingdom.world.World;
import net.lugocorp.kingdom.world.WorldGenerator;

/**
 * View for when we're loading a new game
 */
public class LoadingGameView implements View {
    private Function<View, Void> navigate;
    private AssetsLoader assets;

    @Override
    public Color getBackgroundColor() {
        return new Color(0.2f, 0.2f, 0.2f, 1.0f);
    }

    @Override
    public void start(Function<View, Void> navigate) {
        this.navigate = navigate;
        this.assets = new AssetsLoader(new AssetManager());
        this.assets.load();
    }

    @Override
    public void render() {
        this.assets.doOnLoad(() -> {
            World world = new WorldGenerator().generateWorld(this.assets, 10, 5);
            Game game = new Game(world);
            this.navigate.apply(new GameView(this.assets, game));
        });
    }

    @Override
    public void dispose() {
    }
}
