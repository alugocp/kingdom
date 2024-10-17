package net.lugocorp.kingdom.game.model;
import net.lugocorp.kingdom.engine.Modellable;
import net.lugocorp.kingdom.game.Game;
import net.lugocorp.kingdom.game.events.Event;
import net.lugocorp.kingdom.game.events.EventReceiver;
import net.lugocorp.kingdom.game.model.Inventory.InventoryType;
import net.lugocorp.kingdom.ui.menu.ButtonNode;
import net.lugocorp.kingdom.ui.menu.ListNode;
import net.lugocorp.kingdom.ui.menu.MenuNode;
import net.lugocorp.kingdom.ui.menu.MenuSubject;
import net.lugocorp.kingdom.ui.menu.TextNode;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.math.Coords;
import net.lugocorp.kingdom.utils.math.Point;
import com.badlogic.gdx.math.Vector3;
import java.util.Optional;

/**
 * Represents a single hexagon in the game world and its corresponding
 * properties
 */
public class Tile extends Modellable implements EventReceiver, MenuSubject {
    private Optional<Ability> ability = Optional.empty();
    public final String name;
    public final Inventory items = new Inventory(InventoryType.FREE, 4);
    public Optional<Player> leader = Optional.empty();
    public Optional<Building> building = Optional.empty();
    public Optional<Unit> unit = Optional.empty();

    Tile(String name, int x, int y) {
        super(x, y);
        this.name = name;
    }

    /** {@inheritdoc} */
    @Override
    public void handleEventWithoutSignalBooster(GameView view, Event e) {
        view.game.events.tile.handle(view, this, e);
    }

    /** {@inheritdoc} */
    @Override
    public String getStratifier() {
        return this.name;
    }

    /** {@inheritdoc} */
    @Override
    public void spawn(Game g) {
        g.world.setTile(this, this.x, this.y);
    }

    /** {@inheritdoc} */
    @Override
    public Vector3 getPositionVector() {
        return Coords.grid.vector(this.x, this.y);
    }

    /** {@inheritdoc} */
    @Override
    public MenuNode getMenuContent(GameView view, Optional<Point> p) {
        if (!p.isPresent()) {
            throw new RuntimeException("Cannot display unspawned tiles");
        }
        ListNode node = new ListNode().add(new ButtonNode(view.game.graphics, "x", () -> {
            view.closeMenu();
        })).add(new TextNode(view.game.graphics, this.name));
        this.ability.ifPresent((Ability a) -> node.add(new TextNode(view.game.graphics, a.desc)));
        node.add(this.items.getMenuContent(view, p));
        this.building.ifPresent((Building b) -> node.add(b.getMenuContent(view, p)));
        this.unit.ifPresent((Unit u) -> node.add(u.getMenuContent(view, p)));
        return node;
    }
}
