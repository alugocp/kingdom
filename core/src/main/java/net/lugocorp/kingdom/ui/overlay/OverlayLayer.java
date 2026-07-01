package net.lugocorp.kingdom.ui.overlay;
import net.lugocorp.kingdom.game.layers.Entity;
import net.lugocorp.kingdom.game.model.Tile;
import net.lugocorp.kingdom.ui.views.GameView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A collection of text and icons drawn on top of the GameView
 */
public class OverlayLayer {
    // TODO keep the values of entityOverlays sorted by their keys' y-coordinate in
    // the world
    private final Map<Entity, EntityOverlay> entityOverlays = new HashMap<>();
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
        if (view.game.world.getTile(o.getOrigin()).map((Tile t) -> t.isVisible()).orElse(false)) {
            this.overlays.add(o);
        }
    }

    /**
     * Returns an EntityOverlay associated with he given Entity
     */
    public EntityOverlay entity(Entity e) {
        if (!this.entityOverlays.containsKey(e)) {
            this.entityOverlays.put(e, new EntityOverlay());
        }
        return this.entityOverlays.get(e);
    }

    /**
     * Renders all active OverlayLayer
     */
    public void render(int dt) {
        // Process the radical Overlays
        for (Overlay o : this.overlays) {
            o.update(dt);
            if (o.isDone()) {
                o.runCallback();
                this.dropList.add(o);
                continue;
            }
            o.render(this.view);
        }
        this.overlays.removeAll(this.dropList);
        this.dropList.clear();

        // Process the EntityOverlays
        for (EntityOverlay o : this.entityOverlays.values()) {
            o.update(dt);
            o.render(this.view);
        }
    }
}
