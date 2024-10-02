package net.lugocorp.kingdom.game;
import com.badlogic.gdx.math.Vector3;
import java.util.Optional;
import net.lugocorp.kingdom.engine.Graphics;
import net.lugocorp.kingdom.engine.Modellable;
import net.lugocorp.kingdom.events.Event;
import net.lugocorp.kingdom.events.EventTarget;
import net.lugocorp.kingdom.math.Coords;
import net.lugocorp.kingdom.ui.ListNode;
import net.lugocorp.kingdom.ui.MenuNode;
import net.lugocorp.kingdom.ui.MenuSubject;
import net.lugocorp.kingdom.ui.TextNode;

/**
 * Represents a single hexagon in the game world and its corresponding
 * properties
 */
public class Tile extends Modellable implements EventTarget, MenuSubject {
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
    public Vector3 getPositionVector() {
        return Coords.grid.vector(this.getX(), this.getY());
    }

    /** {@inheritdoc} */
    public MenuNode getMenuContent(Graphics graphics) {
        return new ListNode().add(new TextNode(graphics, this.name));
    }
}
