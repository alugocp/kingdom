package net.lugocorp.kingdom.ui.views;
import net.lugocorp.kingdom.engine.AudioVideo;
import net.lugocorp.kingdom.game.events.AllEventHandlers;
import net.lugocorp.kingdom.menu.game.ArtifactNode;
import net.lugocorp.kingdom.menu.game.GlyphBadgeNode;
import net.lugocorp.kingdom.menu.game.GlyphIconsNode;
import net.lugocorp.kingdom.menu.icon.IconNode;
import net.lugocorp.kingdom.mods.GameMod;
import net.lugocorp.kingdom.mods.ModLoader;
import net.lugocorp.kingdom.serial.SaveLoad;
import net.lugocorp.kingdom.ui.View;
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
        av.loaders.sprites.register("artifact-mask", "game/artifact-mask", ArtifactNode.WIDTH, ArtifactNode.HEIGHT, 0,
                0);
        av.loaders.sprites.register("glyph-icon-battle", "game/glyph-icons", GlyphIconsNode.SIDE, GlyphIconsNode.SIDE,
                0, 0);
        av.loaders.sprites.register("glyph-icon-defense", "game/glyph-icons", GlyphIconsNode.SIDE, GlyphIconsNode.SIDE,
                1, 0);
        av.loaders.sprites.register("glyph-icon-healing", "game/glyph-icons", GlyphIconsNode.SIDE, GlyphIconsNode.SIDE,
                2, 0);
        av.loaders.sprites.register("glyph-icon-nature", "game/glyph-icons", GlyphIconsNode.SIDE, GlyphIconsNode.SIDE,
                0, 1);
        av.loaders.sprites.register("glyph-icon-mining", "game/glyph-icons", GlyphIconsNode.SIDE, GlyphIconsNode.SIDE,
                1, 1);
        av.loaders.sprites.register("glyph-icon-trade", "game/glyph-icons", GlyphIconsNode.SIDE, GlyphIconsNode.SIDE, 2,
                1);
        av.loaders.sprites.register("glyph-badge-battle", "game/glyph-badges", GlyphBadgeNode.SIDE, GlyphBadgeNode.SIDE,
                0, 0);
        av.loaders.sprites.register("glyph-badge-defense", "game/glyph-badges", GlyphBadgeNode.SIDE,
                GlyphBadgeNode.SIDE, 1, 0);
        av.loaders.sprites.register("glyph-badge-healing", "game/glyph-badges", GlyphBadgeNode.SIDE,
                GlyphBadgeNode.SIDE, 2, 0);
        av.loaders.sprites.register("glyph-badge-nature", "game/glyph-badges", GlyphBadgeNode.SIDE, GlyphBadgeNode.SIDE,
                0, 1);
        av.loaders.sprites.register("glyph-badge-mining", "game/glyph-badges", GlyphBadgeNode.SIDE, GlyphBadgeNode.SIDE,
                1, 1);
        av.loaders.sprites.register("glyph-badge-trade", "game/glyph-badges", GlyphBadgeNode.SIDE, GlyphBadgeNode.SIDE,
                2, 1);
        av.loaders.sprites.register("help-icon", "ui/icons", IconNode.SIDE, IconNode.SIDE, 0, 0);
        av.loaders.sprites.register("settings-icon", "ui/icons", IconNode.SIDE, IconNode.SIDE, 1, 0);
        av.loaders.sprites.register("guide-icon", "ui/icons", IconNode.SIDE, IconNode.SIDE, 2, 0);
        av.loaders.sprites.register("sun-icon", "ui/icons", IconNode.SIDE, IconNode.SIDE, 3, 0);
        av.loaders.sprites.register("moon-icon", "ui/icons", IconNode.SIDE, IconNode.SIDE, 4, 0);
    }

    /** {@inheritdoc} */
    @Override
    protected void performTask() {
        final ModLoader modLoader = new ModLoader();
        try {
            modLoader.resetModAssetsLocation();
        } catch (Exception e) {
            System.err.println("Could not clear the mod asset unzip directory");
            e.printStackTrace();
            return;
        }

        // Check for mods and if none are found then create the default mod
        if (modLoader.getMods().size() == 0) {
            System.out.println("No mods found - unpacking vanilla mod");
            modLoader.createDefaultMod();
        }

        for (String filepath : modLoader.getMods()) {
            System.out.println(String.format("Loading mod at %s", filepath));

            // Load mod code
            try {
                final GameMod m = modLoader.loadMod(filepath, this.events, this.av.loaders.sprites,
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
