package net.lugocorp.kingdom.game.model;
import net.lugocorp.kingdom.engine.Modellable;
import net.lugocorp.kingdom.game.combat.HitPoints;
import net.lugocorp.kingdom.game.core.Events;
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
import net.lugocorp.kingdom.utils.math.Point;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.math.Vector3;
import java.util.Optional;

/**
 * Some structure that can be built on top of a Tile to modify its properties
 */
public class Building extends Modellable implements EventReceiver, MenuSubject {
    public final Tags tags = new Tags();
    public final String name;
    public final HitPoints<Building> health;
    public Optional<Inventory> items = Optional.empty();
    public String desc = "";

    Building(String name, int x, int y) {
        super(x, y);
        this.name = name;
        this.health = new HitPoints<Building>(this);
    }

    /**
     * Can make this Building's model partially transparent or fully opaque
     */
    public void setTransparency(boolean transparent) {
        if (this.model.isPresent()) {
            for (Material m : this.model.get().materials) {
                m.set(new BlendingAttribute(transparent ? 0.5f : 1f));
            }
        }
    }

    /** {@inheritdoc} */
    @Override
    public void spawn(GameView view) {
        view.game.world.getTile(this.x, this.y).ifPresent((Tile t) -> {
            t.building = Optional.of(this);
            if (t.unit.isPresent()) {
                this.setTransparency(true);
            }
            view.game.buildingSpawned(this);
        });
        this.handleEvent(view, new Events.SpawnEvent<Building>(this));
    }

    /** {@inheritdoc} */
    @Override
    public Vector3 getPositionVector() {
        return Coords.grid.vector(this.x, this.y).add(Coords.raw.vector(0, Hexagons.HEIGHT, 0));
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
    public void deactivate(GameView view) {
        EventReceiver.super.deactivate(view);
        view.game.removeBuilding(this);
    }

    /** {@inheritdoc} */
    @Override
    public MenuNode getMenuContent(GameView view, Optional<Point> p) {
        Optional<Player> leader = p.flatMap((Point p1) -> view.game.world.getTile(p1.x, p1.y))
                .flatMap((Tile t) -> t.leader);
        ListNode node = new ListNode().add(new HeaderNode(view.game.graphics, this.name))
                .add(new TextNode(view.game.graphics, this.desc));
        if (leader.isPresent()) {
            node.add(new TextNode(view.game.graphics, String.format("Alignment: %s", leader.get().name)));
        }
        node.add(new TextNode(view.game.graphics,
                String.format("Health: %d/%d", this.health.get(), this.health.getMax())));
        if (this.items.isPresent()) {
            if (leader.map((Player p1) -> p1.isHumanPlayer()).orElse(false)) {
                node.add(new TextNode(view.game.graphics, String.format("Gold: %d", this.items.get().getTotalGold())));
                node.add(this.items.get().getMenuContent(view, p));
            } else {
                node.add(new TextNode(view.game.graphics,
                        String.format("Can store %d items", this.items.get().getMax())));
            }
        }
        return node;
    }
}
