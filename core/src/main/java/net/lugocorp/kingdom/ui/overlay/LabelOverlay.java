package net.lugocorp.kingdom.ui.overlay;
import net.lugocorp.kingdom.color.ColorScheme;
import net.lugocorp.kingdom.color.Colors;
import net.lugocorp.kingdom.engine.fonts.FontParam;
import net.lugocorp.kingdom.math.Point;
import net.lugocorp.kingdom.ui.views.GameView;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector3;

/**
 * Represents a text label on the GameView
 */
public class LabelOverlay extends Overlay {
    private final String label;
    private final int color;
    private boolean dispelled = false;

    public LabelOverlay(Point p, Vector3 offset, int color, String label) {
        super(p, offset);
        this.color = color;
        this.label = label;
    }

    /**
     * Causes this Overlay to disappear from the UI
     */
    public void dispel() {
        this.dispelled = true;
    }

    /** {@inheritdoc} */
    @Override
    public boolean isDone() {
        return this.dispelled;
    }

    /** {@inheritdoc} */
    @Override
    public void update(int dt) {
        // No-op
    }

    /** {@inheritdoc} */
    @Override
    public void render(GameView view) {
        final BitmapFont font = view.av.fonts.getFont(
                new FontParam().setSize(24).setColor(Colors.fromHex(this.color)).setBorder(ColorScheme.BLACK.color));
        final float[] pos = this.getPosition(view);
        final Color c = font.getColor();
        view.av.sprites.begin();
        font.setColor(c);
        font.draw(view.av.sprites, this.label, pos[0], pos[1] + (int) font.getLineHeight());
        view.av.sprites.end();
    }
}
