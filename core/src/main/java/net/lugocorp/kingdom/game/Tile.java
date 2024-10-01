package net.lugocorp.kingdom.game;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import java.util.Optional;
import net.lugocorp.kingdom.assets.AssetsLoader;
import net.lugocorp.kingdom.engine.Modellable;
import net.lugocorp.kingdom.events.Event;
import net.lugocorp.kingdom.events.EventTarget;
import net.lugocorp.kingdom.math.Coords;

/**
 * Represents a single hexagon in the game world and its corresponding
 * properties
 */
public class Tile extends Modellable implements EventTarget {
    public Optional<Building> building = Optional.empty();
    public Optional<Unit> unit = Optional.empty();
    public final String name;

    Tile(String name, int x, int y) {
        super(x, y);
        this.name = name;
    }

    /** {@inheritdoc} */
    @Override
    public void handleEvent(Game g, Event e) {
        g.events.tile.handle(g, this.name, e);
    }

    /** {@inheritdoc} */
    public void setModelInstance(AssetsLoader assets, String name) {
        ModelInstance model = assets.createModelInstance(name);
        model.transform.setTranslation(Coords.grid.vector(this.x, this.y));
        this.model = Optional.of(model);
    }
}
