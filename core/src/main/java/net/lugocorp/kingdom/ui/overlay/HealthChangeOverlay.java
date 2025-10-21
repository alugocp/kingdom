package net.lugocorp.kingdom.ui.overlay;
import net.lugocorp.kingdom.color.ColorScheme;
import net.lugocorp.kingdom.game.layers.Entity;
import net.lugocorp.kingdom.ui.views.GameView;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector3;

/**
 * Represents a health bar changing over some Entity
 */
public class HealthChangeOverlay extends Overlay {
    private static final float DURATION = 1500f;
    private static final int LOG_BASE = 6;
    private static final int HEIGHT = 10;
    private final int start;
    private final int end;
    private final int max;
    private float progress = 0f;

    public HealthChangeOverlay(GameView view, Entity e, int max, int start, int end) {
        super(e.getPoint(), new Vector3(0f, view.av.loaders.models.getModelHeight(e.getModelName()), 0f));
        this.start = start;
        this.end = end;
        this.max = max;
    }

    /** {@inheritdoc} */
    @Override
    public boolean isDone() {
        return this.progress == 1f;
    }

    /** {@inheritdoc} */
    @Override
    public void update(int dt) {
        this.progress = Math.min(1f, this.progress + (dt / HealthChangeOverlay.DURATION));
    }

    /** {@inheritdoc} */
    @Override
    public void render(GameView view) {
        final float[] pos = this.getPosition(view);
        final int value = (int) Math.min(this.max, Math.max(0, ((this.end - this.start) * this.progress) + this.start));
        final int w = (int) ((Math.log(this.max) / Math.log(HealthChangeOverlay.LOG_BASE)) * 50);
        final int x = (int) (pos[0] - (w / 2));

        // Draw the bar
        view.av.shapes.begin(ShapeType.Filled);
        view.av.shapes.setColor(ColorScheme.GUTTER.color);
        view.av.shapes.rect(x, pos[1], w, HealthChangeOverlay.HEIGHT);
        view.av.shapes.setColor(ColorScheme.SPECIAL_BUTTON.color);
        view.av.shapes.rect(x, pos[1], w * value / this.max, HealthChangeOverlay.HEIGHT);
        view.av.shapes.end();

        // Draw the outline
        view.av.shapes.begin(ShapeType.Line);
        view.av.shapes.setColor(ColorScheme.TEXT.color);
        view.av.shapes.rect(x, pos[1], w, HealthChangeOverlay.HEIGHT);
        view.av.shapes.end();
    }
}
