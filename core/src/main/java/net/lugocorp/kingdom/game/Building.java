package net.lugocorp.kingdom.game;
import com.badlogic.gdx.math.Vector3;
import java.util.Optional;
import net.lugocorp.kingdom.engine.Modellable;
import net.lugocorp.kingdom.events.Event;
import net.lugocorp.kingdom.events.EventTarget;
import net.lugocorp.kingdom.math.Coords;
import net.lugocorp.kingdom.math.Hexagons;
import net.lugocorp.kingdom.menu.HeaderNode;
import net.lugocorp.kingdom.menu.ListNode;
import net.lugocorp.kingdom.menu.MenuNode;
import net.lugocorp.kingdom.menu.MenuSubject;
import net.lugocorp.kingdom.views.GameView;

/**
 * Some structure that can be built on top of a Tile to modify its properties
 */
public class Building extends Modellable implements EventTarget, MenuSubject {
    private Optional<Ability> ability = Optional.empty();
    public final String name;
    public Optional<Inventory> items = Optional.empty();

    Building(String name, int x, int y) {
        super(x, y);
        this.name = name;
    }

    /** {@inheritdoc} */
    @Override
    public void handleEvent(Game g, Event e) {
        g.events.building.handle(g, this.name, e);
    }

    /** {@inheritdoc} */
    public Vector3 getPositionVector() {
        return Coords.grid.vector(this.getX(), this.getY()).add(Coords.raw.vector(0, Hexagons.HEIGHT, 0));
    }

    /** {@inheritdoc} */
    @Override
    public MenuNode getMenuContent(GameView view, int x, int y) {
        ListNode node = new ListNode().add(new HeaderNode(view.game.graphics, this.name));
        this.ability.ifPresent((Ability a) -> node.add(a.getMenuContent(view, x, y)));
        if (this.items.isPresent()) {
            node.add(this.items.get().getMenuContent(view, x, y));
        }
        return node;
    }
}
