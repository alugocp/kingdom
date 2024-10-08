package net.lugocorp.kingdom.game;
import com.badlogic.gdx.math.Vector3;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.lugocorp.kingdom.engine.Modellable;
import net.lugocorp.kingdom.events.Event;
import net.lugocorp.kingdom.events.EventTarget;
import net.lugocorp.kingdom.game.Inventory.InventoryType;
import net.lugocorp.kingdom.math.Coords;
import net.lugocorp.kingdom.math.Hexagons;
import net.lugocorp.kingdom.menu.HeaderNode;
import net.lugocorp.kingdom.menu.ListNode;
import net.lugocorp.kingdom.menu.MenuNode;
import net.lugocorp.kingdom.menu.MenuSubject;
import net.lugocorp.kingdom.menu.TextNode;
import net.lugocorp.kingdom.views.GameView;

/**
 * A single controllable entity (or NPC) that the player can interact with
 * in-game
 */
public class Unit extends Modellable implements EventTarget, MenuSubject {
    public final String name;
    public Optional<Ability> active1 = Optional.empty();
    public Optional<Ability> active2 = Optional.empty();
    public List<Ability> passives = new ArrayList<>();
    public Inventory equipped = new Inventory(InventoryType.EQUIP, 2);
    public Inventory haul = new Inventory(InventoryType.HAUL, 4);

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
    public MenuNode getMenuContent(GameView view, int x, int y) {
        ListNode node = new ListNode().add(new HeaderNode(view.game.graphics, this.name));
        this.active1.ifPresent((Ability a) -> node.add(a.getMenuContent(view, x, y)));
        this.active2.ifPresent((Ability a) -> node.add(a.getMenuContent(view, x, y)));
        for (Ability a : this.passives) {
            node.add(a.getMenuContent(view, x, y));
        }
        node.add(new TextNode(view.game.graphics, "Equipped Items"));
        node.add(this.equipped.getMenuContent(view, x, y));
        node.add(new TextNode(view.game.graphics, "Hauled Items"));
        node.add(this.haul.getMenuContent(view, x, y));
        return node;
    }
}
