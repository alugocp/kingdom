package net.lugocorp.kingdom.ui.views;
import net.lugocorp.kingdom.engine.Graphics;
import net.lugocorp.kingdom.game.events.AllEventHandlers;
import net.lugocorp.kingdom.utils.math.Coords;
import net.lugocorp.kingdom.utils.mods.ModLoader;
import com.badlogic.gdx.graphics.Color;
import java.util.function.Consumer;

/**
 * View for when we're loading a new game
 */
public class LoadingGameView implements View {
    private final AllEventHandlers events = new AllEventHandlers();
    private final Graphics graphics;
    private Consumer<View> navigate;
    private boolean loaded = false;

    public LoadingGameView(Graphics graphics) {
        this.graphics = graphics;
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
                    mods.loadMod(key, filepath, this.events);
                } catch (Exception e) {
                    System.err.println(String.format("Error while loading mod '%s'", key));
                    e.printStackTrace();
                    continue;
                }

                // Extract mod assets
                try {
                    mods.unzipAssets(key, filepath, this.graphics.loaders.models.getModAssetsMap());
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
            this.navigate.accept(new StartMenuView(this.graphics, this.events));
        }
        this.graphics.sprites.begin();
        this.graphics.fonts.basic.draw(this.graphics.sprites, "Loading...", Coords.SIZE.y / 3, Coords.SIZE.y / 2);
        this.graphics.sprites.end();
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
