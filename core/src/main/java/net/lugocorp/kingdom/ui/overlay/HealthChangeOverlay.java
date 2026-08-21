package net.lugocorp.kingdom.ui.overlay;
import net.lugocorp.kingdom.color.ColorScheme;
import net.lugocorp.kingdom.engine.fonts.FontParam;
import net.lugocorp.kingdom.engine.render.Drawable;
import net.lugocorp.kingdom.game.layers.Entity;
import net.lugocorp.kingdom.ui.views.GameView;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector3;

/**
 * Represents a health bar changing over some Entity
 */
public class HealthChangeOverlay extends Overlay {
    private static final float DURATION = 1500f;
    private final Drawable icon;
    private final int start;
    private final int end;
    private float progress = 0f;

    public HealthChangeOverlay(GameView view, Entity e, int start, int end) {
        super(e.getPoint(), new Vector3(0f, view.av.loaders.models.getModelHeight(e.getModelName()), 0f));
        this.icon = new Drawable(view.av.loaders.sprites, "heart-icon");
        this.start = start;
        this.end = end;
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
        final int value = start + (int) ((end - start) * this.progress);
        final BitmapFont font = view.av.fonts.getFont(
                new FontParam().setSize(30).setColor(ColorScheme.RED.color).setBorder(ColorScheme.BLACK.color));

        // Draw the icon and text
        view.av.sprites.begin();
        this.icon.render(view.av.sprites, (int) (pos[0] - 15f), (int) (pos[1] - 20f));
        font.draw(view.av.sprites, String.format("%d", value), (int) (pos[0] + 15f), (int) pos[1]);
        view.av.sprites.end();
    }
}
