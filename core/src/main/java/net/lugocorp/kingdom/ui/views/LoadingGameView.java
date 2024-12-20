package net.lugocorp.kingdom.ui.views;
import net.lugocorp.kingdom.engine.AudioVideo;
import net.lugocorp.kingdom.game.events.AllEventHandlers;
import net.lugocorp.kingdom.ui.menu.ArtifactNode;
import net.lugocorp.kingdom.utils.math.Coords;
import net.lugocorp.kingdom.utils.mods.ModLoader;
import com.badlogic.gdx.graphics.Color;
import java.util.function.Consumer;

/**
 * View for when we're loading a new game
 */
public class LoadingGameView implements View {
    private final AllEventHandlers events = new AllEventHandlers();
    private final AudioVideo av;
    private Consumer<View> navigate;
    private boolean loaded = false;

    public LoadingGameView(AudioVideo av) {
        this.av = av;

        // Load built-in sprites
        av.loaders.sprites.register("artifact-mask", "ui/artifact-mask", ArtifactNode.WIDTH, ArtifactNode.HEIGHT, 0, 0);
    }

    /** {@inheritdoc} */
    @Override
    public Color getBackgroundColor() {
        return new Color(0f, 0f, 0f, 1f);
    }

    /** {@inheritdoc} */
    @Override
    public void start(Consumer<View> navigate) {
        this.navigate = navigate;

        // Initiate mod loading in a separate Thread
        new Thread(() -> {
            ModLoader mods = new ModLoader();
            try {
                mods.resetModAssetsLocation();
            } catch (Exception e) {
                System.err.println("Could not load any mod data");
                e.printStackTrace();
                this.loaded = true;
                return;
            }
            for (String filepath : mods.getMods()) {
                String key = mods.getModKey(filepath);
                System.out.println(String.format("Loading mod '%s'...", key));

                // Load mod code
                try {
                    mods.loadMod(key, filepath, this.events, this.av.loaders.sprites);
                } catch (Exception e) {
                    System.err.println(String.format("Error while loading mod '%s'", key));
                    e.printStackTrace();
                    continue;
                }

                // Extract mod assets
                try {
                    mods.unzipAssets(key, filepath, this.av.loaders.models.getModAssetsMap());
                } catch (Exception e) {
                    System.err.println(String.format("Did not load any assets from mod '%s'", key));
                    e.printStackTrace();
                }
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.loaded = true;
        }).start();
    }

    /** {@inheritdoc} */
    @Override
    public void render() {
        if (this.loaded) {
            this.navigate.accept(new StartMenuView(this.av, this.events));
        }
        this.av.sprites.begin();
        this.av.fonts.basic.draw(this.av.sprites, "Loading...", Coords.SIZE.y / 3, Coords.SIZE.y / 2);
        this.av.sprites.end();
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
