package net.lugocorp.kingdom.game;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import java.util.Optional;
import net.lugocorp.kingdom.assets.AssetsLoader;
import net.lugocorp.kingdom.engine.Modellable;
import net.lugocorp.kingdom.math.Coords;

public class Building extends Modellable {

    /** {@inheritdoc} */
    public void setModelInstance(AssetsLoader assets, String name, int x, int y, int z) {
        ModelInstance model = assets.createModelInstance(name);
        model.transform.setTranslation(Coords.grid.vector(x, y, z));
        model.transform.translate(Coords.raw.vector(0, assets.getModelHeight(name) / 2f, 0));
        this.model = Optional.of(model);

        // TODO move this to some other place
        BlendingAttribute attr = new BlendingAttribute(0.5f);
        for (Material material : model.materials) {
            material.set(attr);
            // material.remove(BlendingAttribute.ID);
        }
    }
}
