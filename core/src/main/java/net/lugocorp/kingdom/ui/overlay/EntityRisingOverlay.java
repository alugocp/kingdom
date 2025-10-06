package net.lugocorp.kingdom.ui.overlay;
import net.lugocorp.kingdom.game.layers.Entity;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.logic.Colors;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector3;

/**
 * Represents a text label rising over the GameView
 */
public class EntityRisingOverlay extends Overlay {
    private static final float DURATION = 2000f;
    private static final float FADE_OUT = 0.3f;
    private final String label;
    private final int color;
    private float progress = 0f;

    public EntityRisingOverlay(GameView view, Entity e, int color, String label) {
        super(e.getPoint(), new Vector3(0f, view.av.loaders.models.getModelHeight(e.getModelName()), 0f));
        this.color = color;
        this.label = label;
    }

    /**
     * Returns the opacity for this Overlay
     */
    private final float getOpacity() {
        final float thresh = 1f - EntityRisingOverlay.FADE_OUT;
        return this.progress >= thresh ? 1f - ((this.progress - thresh) / EntityRisingOverlay.FADE_OUT) : 1f;
    }

    /** {@inheritdoc} */
    @Override
    public boolean isDone() {
        return this.progress == 1f;
    }

    /** {@inheritdoc} */
    @Override
    public float update(int dt) {
        this.progress = Math.min(1f, this.progress + (dt / EntityRisingOverlay.DURATION));
        return this.progress;
    }

    /** {@inheritdoc} */
    @Override
    public void render(GameView view) {
        final BitmapFont font = view.av.fonts.getFont(24, Colors.fromHex(this.color));
        final float[] pos = this.getPosition(view);
        final Color c = font.getColor();
        view.av.sprites.begin();
        font.setColor(c.r, c.g, c.b, this.getOpacity());
        font.draw(view.av.sprites, this.label, pos[0], pos[1] + (int) font.getLineHeight() + (progress * 50f));
        font.setColor(c);
        view.av.sprites.end();
    }
}
