package net.lugocorp.kingdom.game;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import java.util.Optional;
import net.lugocorp.kingdom.assets.AssetsLoader;
import net.lugocorp.kingdom.engine.Modellable;
import net.lugocorp.kingdom.math.Coords;
import net.lugocorp.kingdom.math.Hexagons;

public class Tile extends Modellable {
    public Optional<Building> building = Optional.empty();
    public Optional<Unit> unit = Optional.empty();

    /** {@inheritdoc} */
    public void setModelInstance(AssetsLoader assets, String name, int x, int y, int z) {
        ModelInstance model = assets.createModelInstance(name);
        model.transform.setTranslation(Coords.grid.vector(x, y, z));
        this.model = Optional.of(model);
    }
}
