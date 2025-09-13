package net.lugocorp.kingdom.ui.game;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.CameraMath;
import net.lugocorp.kingdom.utils.math.Point;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import java.util.ArrayList;
import java.util.List;

/**
 * A collection of text and icons drawn on top of the GameView
 */
public class OverlayLayer {
    private final List<Overlay> overlays = new ArrayList<>();
    private final List<Overlay> dropList = new ArrayList<>();
    private final GameView view;

    public OverlayLayer(GameView view) {
        this.view = view;
    }

    /**
     * Adds a new Overlay to this instance
     */
    public void add(String label, int color, Point p) {
        final float[] origin = CameraMath.getScreenPointFromTile(this.view.getCamera(), p);
        this.overlays.add(new Overlay(label, color, origin));
    }

    /**
     * Renders all active OverlayLayer
     */
    public void render(int dt) {
        this.view.av.sprites.begin();
        for (Overlay o : this.overlays) {
            final float progress = o.update(dt);
            if (o.isDone()) {
                this.dropList.add(o);
                continue;
            }

            // Draw the Overlay
            final BitmapFont font = this.view.av.fonts.getFont(24, o.color);
            final Color c = font.getColor();
            font.setColor(c.r, c.g, c.b, o.getOpacity());
            font.draw(this.view.av.sprites, o.label, o.origin[0], o.origin[1] + (progress * 50f));
            font.setColor(c);
        }
        this.view.av.sprites.end();
        this.overlays.removeAll(this.dropList);
        this.dropList.clear();
    }
}
