package net.lugocorp.kingdom.game;
import com.badlogic.gdx.math.Vector3;
import java.util.Optional;
import net.lugocorp.kingdom.engine.GameGraphics;
import net.lugocorp.kingdom.engine.Modellable;
import net.lugocorp.kingdom.events.Event;
import net.lugocorp.kingdom.events.EventTarget;
import net.lugocorp.kingdom.math.Coords;
import net.lugocorp.kingdom.menu.ListNode;
import net.lugocorp.kingdom.menu.MenuNode;
import net.lugocorp.kingdom.menu.TextNode;

/**
 * Represents a single hexagon in the game world and its corresponding
 * properties
 */
public class Tile extends Modellable implements EventTarget {
    private Optional<Ability> ability = Optional.empty();
    public final String name;
    public final Inventory items = new Inventory(10);
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
        return Coords.grid.vector(this.getX(), this.getY());
    }

    /** {@inheritdoc} */
    @Override
    public MenuNode getMenuContent(GameGraphics graphics) {
        ListNode node = new ListNode().add(new TextNode(graphics, this.name));
        this.ability.ifPresent((Ability a) -> node.add(new TextNode(graphics, a.desc)));
        node.add(this.items.getMenuContent(graphics));
        this.building.ifPresent((Building b) -> node.add(b.getMenuContent(graphics)));
        this.unit.ifPresent((Unit u) -> node.add(u.getMenuContent(graphics)));
        return node;
    }
}
