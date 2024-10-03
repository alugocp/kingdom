package net.lugocorp.kingdom.game;
import com.badlogic.gdx.math.Vector3;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.lugocorp.kingdom.engine.GameGraphics;
import net.lugocorp.kingdom.engine.Modellable;
import net.lugocorp.kingdom.events.Event;
import net.lugocorp.kingdom.events.EventTarget;
import net.lugocorp.kingdom.math.Coords;
import net.lugocorp.kingdom.math.Hexagons;
import net.lugocorp.kingdom.menu.HeaderNode;
import net.lugocorp.kingdom.menu.ListNode;
import net.lugocorp.kingdom.menu.MenuNode;
import net.lugocorp.kingdom.menu.TextNode;

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
    public Vector3 getPositionVector() {
        return Coords.grid.vector(this.getX(), this.getY()).add(Coords.raw.vector(0, Hexagons.HEIGHT, 0));
    }

    /** {@inheritdoc} */
    @Override
    public MenuNode getMenuContent(GameGraphics graphics) {
        ListNode node = new ListNode().add(new HeaderNode(graphics, this.name));
        this.active1.ifPresent((Ability a) -> node.add(a.getMenuContent(graphics)));
        this.active2.ifPresent((Ability a) -> node.add(a.getMenuContent(graphics)));
        for (Ability a : this.passives) {
            node.add(a.getMenuContent(graphics));
        }
        node.add(new TextNode(graphics, "Equipped Items"));
        node.add(this.equipped.getMenuContent(graphics));
        node.add(new TextNode(graphics, "Hauled Items"));
        node.add(this.haul.getMenuContent(graphics));
        return node;
    }
}
