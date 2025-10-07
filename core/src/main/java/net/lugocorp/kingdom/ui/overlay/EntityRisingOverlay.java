package net.lugocorp.kingdom.ui.overlay;
import net.lugocorp.kingdom.game.layers.Entity;
import net.lugocorp.kingdom.ui.views.GameView;
import com.badlogic.gdx.math.Vector3;

/**
 * Represents a text label rising over the GameView associated with some Entity
 */
public class EntityRisingOverlay extends RisingOverlay {

    public EntityRisingOverlay(GameView view, Entity e, int color, String label) {
        super(e.getPoint(), new Vector3(0f, view.av.loaders.models.getModelHeight(e.getModelName()), 0f), color, label);
    }
}
