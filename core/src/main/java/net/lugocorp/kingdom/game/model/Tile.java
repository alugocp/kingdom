package net.lugocorp.kingdom.game.model;
import net.lugocorp.kingdom.engine.render.DynamicModellable;
import net.lugocorp.kingdom.engine.render.RenderableUserData;
import net.lugocorp.kingdom.game.core.Events;
import net.lugocorp.kingdom.game.events.Event;
import net.lugocorp.kingdom.game.events.EventReceiver;
import net.lugocorp.kingdom.game.model.Inventory.InventoryType;
import net.lugocorp.kingdom.ui.menu.ButtonNode;
import net.lugocorp.kingdom.ui.menu.ListNode;
import net.lugocorp.kingdom.ui.menu.MenuNode;
import net.lugocorp.kingdom.ui.menu.MenuSubject;
import net.lugocorp.kingdom.ui.menu.SpacerNode;
import net.lugocorp.kingdom.ui.menu.TextNode;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.math.Coords;
import net.lugocorp.kingdom.utils.math.Point;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import java.util.Optional;

/**
 * Represents a single hexagon in the game world and its corresponding
 * properties
 */
public class Tile extends DynamicModellable implements EventReceiver, MenuSubject {
    private boolean obstacle = false;
    public final String name;
    public final Inventory items = new Inventory(InventoryType.FREE, 4);
    public Optional<Player> leader = Optional.empty();
    public Optional<Building> building = Optional.empty();
    public Optional<GlyphCategory> glyph = Optional.of(GlyphCategory.STRATEGIC);
    public Optional<Unit> unit = Optional.empty();
    public String desc = "";

    Tile(String name, int x, int y) {
        super(x, y);
        this.name = name;
    }

    /**
     * This should only be used in conjunction with Kryo rehydration
     */
    public Tile() {
        super(0, 0);
        this.name = null;
    }

    /**
     * Sets whether or not this Tile is an obstacle. Obstacles cannot be walked on
     * by default.
     */
    public void setObstacle(boolean obstacle) {
        this.obstacle = obstacle;
    }

    /**
     * Returns whether or not this Tile is an obstacle. Obstacles cannot be walked
     * on by default.
     */
    public boolean getObstacle() {
        return this.obstacle;
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

    /**
     * Spawns this loaded object into the World
     */
    public void spawn(GameView view) {
        view.game.world.setTile(this, this.x, this.y);
        this.handleEvent(view, new Events.SpawnEvent<Tile>(this));
    }

    /** {@inheritdoc} */
    @Override
    public Vector3 getPositionVector() {
        return Coords.grid.vector(this.x, this.y);
    }

    /** {@inheritdoc} */
    @Override
    public Optional<ModelInstance> getModelInstance() {
        super.getModelInstance();
        if (this.model.isPresent()) {
            ModelInstance model = this.model.get();
            if (model.userData == null) {
                model.userData = new RenderableUserData();
            }
            RenderableUserData data = (RenderableUserData) model.userData;
            data.glyph = this.glyph;
        }
        return this.model;
    }

    /** {@inheritdoc} */
    @Override
    public MenuNode getMenuContent(GameView view, Optional<Point> p) {
        if (!p.isPresent()) {
            throw new RuntimeException("Cannot display unspawned tiles");
        }
        ListNode node = new ListNode();
        node.add(new ButtonNode(view.av, "x", () -> view.menu.close()));
        if (this.glyph.isPresent()) {
            node.add(new TextNode(view.av, String.format("%s glyphs", this.glyph.get())));
        } else {
            node.add(new TextNode(view.av, "No glyphs on this tile"));
        }
        node.add(new TextNode(view.av, this.name)).add(new TextNode(view.av, this.desc))
                .add(this.items.getMenuContent(view, p));
        this.unit.ifPresent((Unit u) -> node.add(new SpacerNode()).add(u.getMenuContent(view, p)));
        this.building.ifPresent((Building b) -> node.add(new SpacerNode()).add(b.getMenuContent(view, p)));
        return node;
    }
}
