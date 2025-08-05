package net.lugocorp.kingdom.game.model;
import net.lugocorp.kingdom.engine.render.DynamicModellable;
import net.lugocorp.kingdom.engine.render.userdata.TileUserData;
import net.lugocorp.kingdom.game.core.Events;
import net.lugocorp.kingdom.game.events.Event;
import net.lugocorp.kingdom.game.events.EventReceiver;
import net.lugocorp.kingdom.game.model.Inventory.InventoryType;
import net.lugocorp.kingdom.game.world.World;
import net.lugocorp.kingdom.ui.menu.ButtonNode;
import net.lugocorp.kingdom.ui.menu.ListNode;
import net.lugocorp.kingdom.ui.menu.MenuNode;
import net.lugocorp.kingdom.ui.menu.MenuSubject;
import net.lugocorp.kingdom.ui.menu.SpacerNode;
import net.lugocorp.kingdom.ui.menu.TextNode;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.Colors;
import net.lugocorp.kingdom.utils.math.Coords;
import net.lugocorp.kingdom.utils.math.Hexagons;
import net.lugocorp.kingdom.utils.math.Point;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import java.util.Optional;
import java.util.Set;

/**
 * Represents a single hexagon in the game world and its corresponding
 * properties
 */
public class Tile extends DynamicModellable implements EventReceiver, MenuSubject {
    private final TileUserData userData = new TileUserData();
    private Optional<GlyphCategory> glyph = Optional.of(GlyphCategory.COMBAT);
    private Color minimapColor = Color.BLACK;
    private boolean obstacle = false;
    private boolean wave = false;
    public final String name;
    public final Inventory items = new Inventory(InventoryType.FREE, 4);
    public Optional<Player> leader = Optional.empty();
    public Optional<Building> building = Optional.empty();
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
     * Returns this Tile's GlyphCategory
     */
    public Optional<GlyphCategory> getGlyph() {
        return this.building.isPresent() ? Optional.empty() : this.glyph;
    }

    /**
     * Sets this Tile's GlyphCategory
     */
    public void setGlyph(Optional<GlyphCategory> glyph) {
        this.glyph = glyph;
        this.userData.glyph = this.getGlyph();
    }

    /**
     * Returns the Minimap Color for this Tile
     */
    public Color getMinimapColor() {
        return this.userData.hasBeenSeen
                ? this.leader.map((Player l) -> l.color)
                        .orElse(this.building.flatMap((Building b) -> b.getMinimapColor()).orElse(this.minimapColor))
                : Color.BLACK;
    }

    /**
     * Sets the Minimap Color for this Tile
     */
    public void setMinimapColor(int hexcode) {
        this.minimapColor = Colors.fromHex(hexcode);
    }

    /**
     * Sets whether or not this Tile should apply a wave to its texture
     */
    public void setWave(boolean wave) {
        this.userData.wave = wave;
    }

    /**
     * Returns true if this Tile is currently visible
     */
    public boolean isVisible() {
        return this.userData.visibility > 0;
    }

    /**
     * Adds a visibility point (fog of war system)
     */
    public void incrementVisibility() {
        this.userData.hasBeenSeen = true;
        this.userData.visibility++;
    }

    /**
     * Removes a visibility point (fog of war system)
     */
    public void decrementVisibility() {
        this.userData.visibility--;
    }

    /**
     * Adds a level of selection
     */
    public void incrementSelection() {
        this.userData.selection++;
    }

    /**
     * Removes a level of selection
     */
    public void decrementSelection() {
        this.userData.selection--;
    }

    /**
     * Returns whether or not this Tile is an obstacle. Obstacles cannot be walked
     * on by default.
     */
    public boolean getObstacle() {
        return this.obstacle;
    }

    /**
     * Sets whether or not this Tile is an obstacle. Obstacles cannot be walked on
     * by default.
     */
    public void setObstacle(boolean obstacle) {
        this.obstacle = obstacle;
    }

    /**
     * Causes this Tile (and its neighbors) to recalculate its visible borders
     */
    public void calculateBorders(World world, boolean iterate) {
        Set<Point> neighbors = Hexagons.getNeighbors(this.getPoint(), 1);
        int borders = 0;
        for (Point p : neighbors) {
            Optional<Tile> t = world.getTile(p);
            if (this.leader.isPresent() && this.leader.get() != t.flatMap((Tile t1) -> t1.leader).orElse(null)) {
                if (this.y == p.y) {
                    borders += this.x < p.x ? TileUserData.BORDER_RIGHT : TileUserData.BORDER_LEFT;
                } else {
                    boolean right = (this.y % 2 == 0 && this.x == p.x) || (this.y % 2 == 1 && this.x == p.x - 1);
                    if (this.y < p.y) {
                        borders += right ? TileUserData.BORDER_BOT_RIGHT : TileUserData.BORDER_BOT_LEFT;
                    } else {
                        borders += right ? TileUserData.BORDER_TOP_RIGHT : TileUserData.BORDER_TOP_LEFT;
                    }
                }
            }
            if (iterate) {
                t.ifPresent((Tile t1) -> t1.calculateBorders(world, false));
            }
        }
        this.userData.borderColor = this.leader.map((Player l) -> l.color).orElse(Color.BLACK);
        this.userData.borders = borders;
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
    protected void setupModelInstance(ModelInstance model) {
        model.userData = this.userData;
    }

    /** {@inheritdoc} */
    @Override
    public MenuNode getMenuContent(GameView view, Optional<Point> p) {
        if (!p.isPresent()) {
            throw new RuntimeException("Cannot display unspawned tiles");
        }
        ListNode node = new ListNode();
        node.add(new ButtonNode(view.av, "x", () -> view.menu.close()));
        if (this.getGlyph().isPresent()) {
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
