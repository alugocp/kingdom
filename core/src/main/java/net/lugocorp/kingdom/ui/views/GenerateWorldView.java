package net.lugocorp.kingdom.ui.views;
import net.lugocorp.kingdom.ai.memory.MemoryMap;
import net.lugocorp.kingdom.game.player.CompPlayer;
import net.lugocorp.kingdom.game.world.WorldGenOptions;
import net.lugocorp.kingdom.game.world.WorldGenerator;
import net.lugocorp.kingdom.ui.View;

/**
 * View for when we're generating a new World
 */
public class GenerateWorldView extends ThreadedTaskView {
    private final WorldGenOptions worldGenOpts;
    private final GameView view;

    GenerateWorldView(GameView view, WorldGenOptions worldGenOpts) {
        super(view.av, "Generating world");
        this.worldGenOpts = worldGenOpts;
        this.view = view;
    }

    /** {@inheritdoc} */
    @Override
    protected void performTask() {
        WorldGenerator generator = new WorldGenerator();
        this.view.game.world.init(this.worldGenOpts.size);
        for (CompPlayer comp : this.view.game.comps) {
            comp.memory = new MemoryMap(this.worldGenOpts.size.w, this.worldGenOpts.size.h);
        }
        generator.generateWorld(this.view, this.worldGenOpts, (Integer i) -> this.setProgress(i.intValue()));
        this.setProgress(100);
    }

    /** {@inheritdoc} */
    @Override
    protected View getNextView() {
        return this.view;
    }
}
