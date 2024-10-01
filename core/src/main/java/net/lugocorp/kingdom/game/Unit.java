package net.lugocorp.kingdom.game;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.lugocorp.kingdom.assets.AssetsLoader;
import net.lugocorp.kingdom.engine.Modellable;
import net.lugocorp.kingdom.events.Event;
import net.lugocorp.kingdom.events.EventTarget;
import net.lugocorp.kingdom.math.Coords;

/**
 * A single controllable entity (or NPC) that the player can interact with
 * in-game
 */
public class Unit extends Modellable implements EventTarget {
    public final String name;
    public Optional<Ability> active1 = Optional.empty();
    public Optional<Ability> active2 = Optional.empty();
    public List<Ability> passives = new ArrayList<>();
    public Inventory equipped = new Inventory(2);
    public Inventory haul = new Inventory(4);

    Unit(String name, int x, int y) {
        super(x, y);
        this.name = name;
    }

    /** {@inheritdoc} */
    @Override
    public void handleEvent(Game g, Event e) {
        g.events.unit.handle(g, this.name, e);
    }

    /** {@inheritdoc} */
    public void setModelInstance(AssetsLoader assets, String name) {
        ModelInstance model = assets.createModelInstance(name);
        model.transform.setTranslation(Coords.grid.vector(this.x, this.y));
        model.transform.translate(Coords.raw.vector(0, assets.getModelHeight(name) / 2f, 0));
        this.model = Optional.of(model);
    }
}
