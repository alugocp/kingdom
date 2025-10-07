package net.lugocorp.kingdom.ui.overlay;
import net.lugocorp.kingdom.ui.views.GameView;
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
    public void add(Overlay o) {
        this.overlays.add(o);
    }

    /**
     * Renders all active OverlayLayer
     */
    public void render(int dt) {
        for (Overlay o : this.overlays) {
            o.update(dt);
            if (o.isDone()) {
                this.dropList.add(o);
                continue;
            }
            o.render(this.view);
        }
        this.overlays.removeAll(this.dropList);
        this.dropList.clear();
    }
}
