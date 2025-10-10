package net.lugocorp.kingdom.ui.overlay;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.logic.Colors;
import net.lugocorp.kingdom.utils.math.Coords;
import net.lugocorp.kingdom.utils.math.Point;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector3;

/**
 * Represents a text label rising into the HUD
 */
public class ResourceOverlay extends Overlay {
    private final String label;
    private final int color;
    private final int dest;
    private final Point p;

    public ResourceOverlay(GameView view, Point p, float coeff, int color, int amount) {
        super(p, new Vector3());
        this.label = amount >= 0 ? String.format("+%d", amount) : Integer.toString(amount);
        this.dest = (int) (Coords.SIZE.x * coeff);
        this.color = color;

        // Set the origin point
        final float[] origin = this.getPosition(view);
        this.p = new Point((int) origin[0], (int) origin[1]);
    }

    /** {@inheritdoc} */
    @Override
    public boolean isDone() {
        return this.p.y >= Coords.SIZE.y;
    }

    /** {@inheritdoc} */
    @Override
    public void update(int dt) {
        if (this.p.x == this.dest) {
            this.p.set(this.p.x, this.p.y + 12);
        } else {
            final int diff = dest - this.p.x;
            final int dx = Math.min(Math.abs(diff), 9) * (diff < 0 ? -1 : 1);
            this.p.set(this.p.x + dx, this.p.y + 6);
        }
    }

    /** {@inheritdoc} */
    @Override
    public void render(GameView view) {
        final BitmapFont font = view.av.fonts.getFont(24, Colors.fromHex(this.color));
        // TODO add an outline to the text
        view.av.sprites.begin();
        font.draw(view.av.sprites, this.label, this.p.x, this.p.y);
        view.av.sprites.end();
    }
}
