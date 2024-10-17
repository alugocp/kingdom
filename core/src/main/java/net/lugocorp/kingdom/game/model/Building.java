package net.lugocorp.kingdom.game.model;
import net.lugocorp.kingdom.engine.Modellable;
import net.lugocorp.kingdom.game.Game;
import net.lugocorp.kingdom.game.events.Event;
import net.lugocorp.kingdom.game.events.EventReceiver;
import net.lugocorp.kingdom.ui.menu.HeaderNode;
import net.lugocorp.kingdom.ui.menu.ListNode;
import net.lugocorp.kingdom.ui.menu.MenuNode;
import net.lugocorp.kingdom.ui.menu.MenuSubject;
import net.lugocorp.kingdom.ui.menu.TextNode;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.math.Coords;
import net.lugocorp.kingdom.utils.math.Hexagons;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.math.Vector3;
import java.util.Optional;

/**
 * Some structure that can be built on top of a Tile to modify its properties
 */
public class Building extends Modellable implements EventReceiver, MenuSubject {
    private Optional<Ability> ability = Optional.empty();
    public final String name;
    public Optional<Inventory> items = Optional.empty();

    Building(String name, int x, int y) {
        super(x, y);
        this.name = name;
    }

    /**
     * Can make this Building's model partially transparent or fully opaque
     */
    public void setTransparency(boolean transparent) {
        this.model.ifPresent(
                (ModelInstance model) -> model.materials.first().set(new BlendingAttribute(transparent ? 0.5f : 1f)));
    }

    /** {@inheritdoc} */
    @Override
    public void handleEventWithoutSignalBooster(GameView view, Event e) {
        view.game.events.building.handle(view, this, e);
    }

    /** {@inheritdoc} */
    @Override
    public String getStratifier() {
        return this.name;
    }

    /** {@inheritdoc} */
    @Override
    public void spawn(Game g) {
        g.world.getTile(this.x, this.y).ifPresent((Tile t) -> {
            t.building = Optional.of(this);
            if (t.unit.isPresent()) {
                this.setTransparency(true);
            }
        });
    }

    /** {@inheritdoc} */
    public Vector3 getPositionVector() {
        return Coords.grid.vector(this.x, this.y).add(Coords.raw.vector(0, Hexagons.HEIGHT, 0));
    }

    /** {@inheritdoc} */
    @Override
    public MenuNode getMenuContent(GameView view, int x, int y) {
        ListNode node = new ListNode().add(new HeaderNode(view.game.graphics, this.name));
        this.ability.ifPresent((Ability a) -> node.add(a.getMenuContent(view, x, y)));
        if (this.items.isPresent()) {
            node.add(new TextNode(view.game.graphics, String.format("Gold: %d", this.items.get().getTotalGold())));
            node.add(this.items.get().getMenuContent(view, x, y));
        }
        return node;
    }
}
