package net.lugocorp.kingdom.game;
import com.badlogic.gdx.math.Vector3;
import java.util.Optional;
import net.lugocorp.kingdom.engine.Modellable;
import net.lugocorp.kingdom.events.Event;
import net.lugocorp.kingdom.events.EventTarget;
import net.lugocorp.kingdom.game.Inventory.InventoryType;
import net.lugocorp.kingdom.math.Coords;
import net.lugocorp.kingdom.menu.ListNode;
import net.lugocorp.kingdom.menu.MenuNode;
import net.lugocorp.kingdom.menu.MenuSubject;
import net.lugocorp.kingdom.menu.TextNode;
import net.lugocorp.kingdom.views.GameView;

/**
 * Represents a single hexagon in the game world and its corresponding
 * properties
 */
public class Tile extends Modellable implements EventTarget, MenuSubject {
    private Optional<Ability> ability = Optional.empty();
    public final String name;
    public final Inventory items = new Inventory(InventoryType.FREE, 10);
    public Optional<Building> building = Optional.empty();
    public Optional<Unit> unit = Optional.empty();

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
    public Vector3 getPositionVector() {
        return Coords.grid.vector(this.x, this.y);
    }

    /** {@inheritdoc} */
    @Override
    public MenuNode getMenuContent(GameView view, int x, int y) {
        ListNode node = new ListNode().add(new TextNode(view.game.graphics, this.name));
        this.ability.ifPresent((Ability a) -> node.add(new TextNode(view.game.graphics, a.desc)));
        node.add(this.items.getMenuContent(view, x, y));
        this.building.ifPresent((Building b) -> node.add(b.getMenuContent(view, x, y)));
        this.unit.ifPresent((Unit u) -> node.add(u.getMenuContent(view, x, y)));
        return node;
    }
}
