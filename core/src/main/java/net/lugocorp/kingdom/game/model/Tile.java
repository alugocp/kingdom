package net.lugocorp.kingdom.game.model;
import net.lugocorp.kingdom.builtin.Events;
import net.lugocorp.kingdom.color.Colors;
import net.lugocorp.kingdom.engine.render.DynamicModellable;
import net.lugocorp.kingdom.engine.userdata.TileUserData;
import net.lugocorp.kingdom.game.layers.Governable;
import net.lugocorp.kingdom.game.layers.Spawnable;
import net.lugocorp.kingdom.game.player.Player;
import net.lugocorp.kingdom.gameplay.events.Event;
import net.lugocorp.kingdom.gameplay.events.EventReceiver;
import net.lugocorp.kingdom.math.Coords;
import net.lugocorp.kingdom.math.Point;
import net.lugocorp.kingdom.menu.MenuNode;
import net.lugocorp.kingdom.menu.MenuSubject;
import net.lugocorp.kingdom.menu.structure.ListNode;
import net.lugocorp.kingdom.menu.structure.TabsNode;
import net.lugocorp.kingdom.menu.text.HeaderNode;
import net.lugocorp.kingdom.menu.text.PlayerBadgeNode;
import net.lugocorp.kingdom.menu.text.TextNode;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.SideEffect;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.esotericsoftware.kryo.serializers.FieldSerializer;
import java.util.Optional;

/**
 * Represents a single hexagon in the game world and its corresponding
 * properties
 */
public class Tile extends DynamicModellable implements EventReceiver, MenuSubject, Spawnable, Governable {
    private final TileUserData userData = new TileUserData();
    @FieldSerializer.Optional("placeholderBuildingModel")
    private Optional<ModelInstance> placeholderBuildingModel = Optional.empty();
    private Color minimapColor = Color.BLACK;
    private Tower domainCenter = null;
    private boolean obstacle = false;
    private boolean wave = false;
    public final String name;
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
     * Returns the focal point for this Tile's domain
     */
    public Tower getDomainCenter() {
        return this.domainCenter;
    }

    /**
     * Sets the focal point for this Tile's domain
     */
    public void setDomainCenter(Tower tower) {
        this.domainCenter = tower;
    }

    /**
     * Returns the Minimap Color for this Tile
     */
    public Color getMinimapColor() {
        return this.userData.hasBeenSeen
                ? this.getLeader().map((Player l) -> l.getColor())
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
        return this.userData.vision > 0;
    }

    /**
     * Returns true if this Tile has ever been visible
     */
    public boolean hasBeenSeen() {
        return this.userData.hasBeenSeen;
    }

    /**
     * Adds a vision point (fog of war system)
     */
    public void incrementVision() {
        this.userData.hasBeenSeen = true;
        this.userData.vision++;
        this.placeholderBuildingModel = Optional.empty();
    }

    /**
     * Removes a vision point (fog of war system)
     */
    public void decrementVision() {
        this.userData.vision--;
        if (this.userData.vision == 0) {
            this.placeholderBuildingModel = this.building.flatMap((Building b) -> b.getModelInstance());
        }
    }

    /**
     * Returns the placeholder Building ModelInstance for this Tile (if there is
     * one)
     */
    public Optional<ModelInstance> getPlaceholderBuildingModel() {
        return this.placeholderBuildingModel;
    }

    /**
     * Sets the option render state
     */
    public void setOption(boolean option) {
        this.userData.option = option;
    }

    /**
     * Sets the hovered render state
     */
    public void changeHovered(boolean add) {
        this.userData.hovered += add ? 1 : -1;
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
     * Causes this Tile to recalculate its border color
     */
    public void recolorBorder() {
        this.userData.borderColor = this.getLeader().map((Player l) -> l.getColor()).orElse(Color.WHITE);
    }

    /**
     * Sets this Tile's border integer for its Domain
     */
    public void setBorder(int border) {
        this.userData.borders = border;
    }

    /**
     * Sets the movement path visible on this Tile
     */
    public void setMovePath(int path, int label) {
        this.userData.movePath = path;
        this.userData.pathLabel = label;
    }

    /** {@inheritdoc} */
    @Override
    public String toString() {
        return String.format("%s %s%s%s", this.name, this.getPoint(),
                this.building.map((Building b) -> String.format(" %s", b.name)).orElse(""),
                this.unit.map((Unit u) -> String.format(" %s", u.name)).orElse(""));
    }

    /** {@inheritdoc} */
    @Override
    public Optional<Player> getLeader() {
        return this.getDomainCenter().getLeader();
    }

    /** {@inheritdoc} */
    @Override
    public SideEffect handleEventWithoutSignalBooster(GameView view, Event e) {
        return view.game.events.tile.handle(view, this, e);
    }

    /** {@inheritdoc} */
    @Override
    public String getStratifier() {
        return this.name;
    }

    /** {@inheritdoc} */
    @Override
    public void spawn(GameView view) {
        view.game.world.setTile(this, this.x, this.y);
        this.handleEvent(view, new Events.SpawnEvent<Tile>(this)).execute();
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

        // Instantiate the Tile MenuNodes
        final ListNode tileNodes = new ListNode().add(new HeaderNode(view.av, this.name));
        if (this.getLeader().isPresent()) {
            tileNodes.add(new PlayerBadgeNode(view.av, this.getLeader().get()));
        }
        tileNodes.add(new TextNode(view.av, this.desc));

        // Determine whether we add the MenuNodes to the ListNode directly or to a
        // TabsNode
        if (this.unit.isPresent() || this.building.isPresent()) {
            final TabsNode tabs = new TabsNode(view.av);
            this.unit.ifPresent((Unit u) -> tabs.add("Unit", Optional.of(u.name), u.getMenuContent(view, p)));
            this.building.ifPresent(
                    (Building b) -> tabs.add(b.getMenuTabLabel(), Optional.of(b.name), b.getMenuContent(view, p)));
            tabs.add("Tile", Optional.of(this.name), tileNodes);
            return tabs;
        }
        return tileNodes;
    }
}
