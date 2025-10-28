package net.lugocorp.kingdom.ui.overlay;
import net.lugocorp.kingdom.color.Colors;
import net.lugocorp.kingdom.engine.assets.FontParam;
import net.lugocorp.kingdom.math.Coords;
import net.lugocorp.kingdom.math.Point;
import net.lugocorp.kingdom.ui.views.GameView;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector3;

/**
 * Represents a text label rising into the HUD
 */
public class ResourceOverlay extends Overlay {
    private final String label;
    private final int color;
    private final int dest;
    private int x;
    private int y;

    public ResourceOverlay(GameView view, Point p, float coeff, int color, int amount) {
        super(p, new Vector3());
        this.label = amount >= 0 ? String.format("+%d", amount) : Integer.toString(amount);
        this.dest = (int) (Coords.SIZE.x * coeff);
        this.color = color;

        // Set the origin point
        final float[] origin = this.getPosition(view);
        this.x = (int) origin[0];
        this.y = (int) origin[1];
    }

    /** {@inheritdoc} */
    @Override
    public boolean isDone() {
        return this.y >= Coords.SIZE.y;
    }

    /** {@inheritdoc} */
    @Override
    public void update(int dt) {
        if (this.x == this.dest) {
            this.y += 24;
        } else {
            final int diff = dest - this.x;
            final int dx = Math.min(Math.abs(diff), 18) * (diff < 0 ? -1 : 1);
            this.x += dx;
            this.y += 12;
        }
    }

    /** {@inheritdoc} */
    @Override
    public void render(GameView view) {
        final BitmapFont font = view.av.fonts.getFont(new FontParam().setSize(24).setColor(Colors.fromHex(this.color)));
        // TODO add an outline to the text
        view.av.sprites.begin();
        font.draw(view.av.sprites, this.label, this.x, this.y);
        view.av.sprites.end();
    }
}
