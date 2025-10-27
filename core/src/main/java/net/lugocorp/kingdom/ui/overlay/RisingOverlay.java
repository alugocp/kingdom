package net.lugocorp.kingdom.ui.overlay;
import net.lugocorp.kingdom.color.Colors;
import net.lugocorp.kingdom.math.Point;
import net.lugocorp.kingdom.ui.views.GameView;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector3;

/**
 * Represents a text label rising over the GameView
 */
public class RisingOverlay extends Overlay {
    private static final float DURATION = 2000f;
    private static final float FADE_OUT = 0.3f;
    private final String label;
    private final int color;
    private float progress = 0f;

    public RisingOverlay(Point p, Vector3 offset, int color, String label) {
        super(p, offset);
        this.color = color;
        this.label = label;
    }

    /**
     * Returns the opacity for this Overlay
     */
    private final float getOpacity() {
        final float thresh = 1f - RisingOverlay.FADE_OUT;
        return this.progress >= thresh ? 1f - ((this.progress - thresh) / RisingOverlay.FADE_OUT) : 1f;
    }

    /** {@inheritdoc} */
    @Override
    public boolean isDone() {
        return this.progress == 1f;
    }

    /** {@inheritdoc} */
    @Override
    public void update(int dt) {
        this.progress = Math.min(1f, this.progress + (dt / RisingOverlay.DURATION));
    }

    /** {@inheritdoc} */
    @Override
    public void render(GameView view) {
        final BitmapFont font = view.av.fonts.getFont(24, Colors.fromHex(this.color));
        final float[] pos = this.getPosition(view);
        final Color c = font.getColor();
        // TODO add an outline to the text
        view.av.sprites.begin();
        font.setColor(c.r, c.g, c.b, this.getOpacity());
        font.draw(view.av.sprites, this.label, pos[0], pos[1] + (int) font.getLineHeight() + (progress * 50f));
        font.setColor(c.r, c.g, c.b, 1f);
        view.av.sprites.end();
    }
}
