package net.lugocorp.kingdom.game;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import java.util.Optional;
import net.lugocorp.kingdom.assets.AssetsLoader;
import net.lugocorp.kingdom.engine.Modellable;
import net.lugocorp.kingdom.events.Event;
import net.lugocorp.kingdom.events.EventTarget;
import net.lugocorp.kingdom.math.Coords;

/**
 * Some structure that can be built on top of a Tile to modify its properties
 */
public class Building extends Modellable implements EventTarget {
    public final String name;

    public Building(String name) {
        this.name = name;
    }

    /** {@inheritdoc} */
    @Override
    public void handleEvent(Game g, Event e) {
        g.buildingHandlers.handle(this.name, e);
    }

    /** {@inheritdoc} */
    public void setModelInstance(AssetsLoader assets, String name, int x, int y, int z) {
        ModelInstance model = assets.createModelInstance(name);
        model.transform.setTranslation(Coords.grid.vector(x, z));
        model.transform.translate(Coords.raw.vector(0, assets.getModelHeight(name) / 2f, 0));
        this.model = Optional.of(model);

        // TODO only do this when a Unit co-habits the Building's Tile
        BlendingAttribute attr = new BlendingAttribute(0.5f);
        for (Material material : model.materials) {
            material.set(attr);
            // material.remove(BlendingAttribute.ID);
        }
    }
}
