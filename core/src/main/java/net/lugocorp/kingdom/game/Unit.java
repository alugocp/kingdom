package net.lugocorp.kingdom.game;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import java.util.Optional;
import net.lugocorp.kingdom.assets.AssetsLoader;
import net.lugocorp.kingdom.engine.Modellable;
import net.lugocorp.kingdom.events.EventReceiver;
import net.lugocorp.kingdom.math.Coords;

public class Unit extends Modellable {
    public final EventReceiver events = new EventReceiver();

    /** {@inheritdoc} */
    public void setModelInstance(AssetsLoader assets, String name, int x, int y, int z) {
        ModelInstance model = assets.createModelInstance(name);
        model.transform.setTranslation(Coords.grid.vector(x, y, z));
        model.transform.translate(Coords.raw.vector(0, assets.getModelHeight(name) / 2f, 0));
        this.model = Optional.of(model);
    }
}
