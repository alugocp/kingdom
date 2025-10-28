package net.lugocorp.kingdom.ui.views;
import net.lugocorp.kingdom.engine.AudioVideo;
import net.lugocorp.kingdom.game.events.AllEventHandlers;
import net.lugocorp.kingdom.menu.game.ArtifactNode;
import net.lugocorp.kingdom.menu.game.DayNightNode;
import net.lugocorp.kingdom.menu.game.FateNode;
import net.lugocorp.kingdom.menu.game.GlyphBadgeNode;
import net.lugocorp.kingdom.menu.game.GlyphIconsNode;
import net.lugocorp.kingdom.menu.misc.HelperNode;
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
        av.loaders.sprites.register("artifact-claimed-mask", "game/artifact-claimed-mask", ArtifactNode.WIDTH,
                ArtifactNode.HEIGHT, 0, 0);
        av.loaders.sprites.register("artifact-hovered-mask", "game/artifact-hovered-mask", ArtifactNode.WIDTH,
                ArtifactNode.HEIGHT, 0, 0);
        av.loaders.sprites.register("fate-highlight", "game/fate-highlight", FateNode.WIDTH, FateNode.HEIGHT, 0, 0);
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
        av.loaders.sprites.register("sun-icon", "game/daynight", DayNightNode.SIDE, DayNightNode.SIDE, 0, 0);
        av.loaders.sprites.register("moon-icon", "game/daynight", DayNightNode.SIDE, DayNightNode.SIDE, 1, 0);
        av.loaders.sprites.register("help-icon", "ui/help", HelperNode.SIDE, HelperNode.SIDE, 0, 0);
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
