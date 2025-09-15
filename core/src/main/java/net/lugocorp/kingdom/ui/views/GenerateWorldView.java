package net.lugocorp.kingdom.ui.views;
import net.lugocorp.kingdom.game.world.WorldGenOptions;
import net.lugocorp.kingdom.game.world.WorldGenerator;
import net.lugocorp.kingdom.ui.View;

/**
 * View for when we're generating a new World
 */
public class GenerateWorldView extends ThreadedTaskView {
    private final WorldGenOptions worldGenOpts;
    private final GameView view;
    private int progress = 0;

    GenerateWorldView(GameView view, WorldGenOptions worldGenOpts) {
        super(view.av);
        this.worldGenOpts = worldGenOpts;
        this.view = view;
    }

    /**
     * Internal syntactic sugar
     */
    private void setProgress(int progress) {
        this.progress = progress;
    }

    /** {@inheritdoc} */
    @Override
    protected void performTask() {
        WorldGenerator generator = new WorldGenerator();
        this.view.game.world.init(this.worldGenOpts.size);
        generator.generateWorld(this.view, this.worldGenOpts, (Integer i) -> this.setProgress(i.intValue()));
        this.progress = 100;
    }

    /** {@inheritdoc} */
    @Override
    protected String getLoadingText() {
        return String.format("Generating world %d%%", this.progress);
    }

    /** {@inheritdoc} */
    @Override
    protected View getNextView() {
        return this.view;
    }
}
