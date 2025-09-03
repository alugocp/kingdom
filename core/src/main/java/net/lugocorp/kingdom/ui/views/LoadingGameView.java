package net.lugocorp.kingdom.ui.views;
import net.lugocorp.kingdom.engine.AudioVideo;
import net.lugocorp.kingdom.game.events.AllEventHandlers;
import net.lugocorp.kingdom.mods.GameMod;
import net.lugocorp.kingdom.mods.ModLoader;
import net.lugocorp.kingdom.ui.menu.ArtifactNode;
import net.lugocorp.kingdom.utils.serial.SaveLoad;
import java.util.ArrayList;
import java.util.List;

/**
 * View for when we're loading a new game
 */
public class LoadingGameView extends ThreadedTaskView {
    private final AllEventHandlers events = new AllEventHandlers();
    private final List<GameMod> mods = new ArrayList<>();

    public LoadingGameView(AudioVideo av) {
        super(av);

        // Load built-in sprites
        av.loaders.sprites.register("artifact-mask", "ui/artifact-mask", ArtifactNode.WIDTH, ArtifactNode.HEIGHT, 0, 0);
    }

    /** {@inheritdoc} */
    @Override
    protected void performTask() {
        ModLoader modLoader = new ModLoader();
        try {
            modLoader.resetModAssetsLocation();
        } catch (Exception e) {
            System.err.println("Could not load any mod data");
            e.printStackTrace();
            return;
        }

        for (String filepath : modLoader.getMods()) {
            System.out.println(String.format("Loading mod at %s", filepath));

            // Load mod code
            try {
                GameMod m = modLoader.loadMod(filepath, this.events, this.av.loaders.sprites,
                        this.av.loaders.models.getModAssetsMap());
                this.mods.add(m);
            } catch (Exception e) {
                System.err.println(String.format("Error while loading mod at %s", filepath));
                e.printStackTrace();
                continue;
            }
        }
    }

    /** {@inheritdoc} */
    @Override
    protected String getLoadingText() {
        return "Loading...";
    }

    /** {@inheritdoc} */
    @Override
    protected View getNextView() {
        return new StartMenuView(new StartMenuView.Params(this.av, this.events, new SaveLoad(), this.mods));
    }
}
