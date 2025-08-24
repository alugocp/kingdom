package net.lugocorp.kingdom.game.model;
import net.lugocorp.kingdom.engine.render.DynamicModellable;
import net.lugocorp.kingdom.engine.render.userdata.CoordUserData;
import net.lugocorp.kingdom.game.combat.BuildingCombat;
import net.lugocorp.kingdom.game.core.Events;
import net.lugocorp.kingdom.game.events.Event;
import net.lugocorp.kingdom.game.events.EventReceiver;
import net.lugocorp.kingdom.game.mechanics.Visibility;
import net.lugocorp.kingdom.game.model.fields.Inventory;
import net.lugocorp.kingdom.game.model.fields.Tags;
import net.lugocorp.kingdom.game.player.Player;
import net.lugocorp.kingdom.ui.menu.HeaderNode;
import net.lugocorp.kingdom.ui.menu.ListNode;
import net.lugocorp.kingdom.ui.menu.MenuNode;
import net.lugocorp.kingdom.ui.menu.MenuSubject;
import net.lugocorp.kingdom.ui.menu.TextNode;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.Colors;
import net.lugocorp.kingdom.utils.code.SideEffect;
import net.lugocorp.kingdom.utils.math.Coords;
import net.lugocorp.kingdom.utils.math.Hexagons;
import net.lugocorp.kingdom.utils.math.Point;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import java.util.Optional;

/**
 * Some structure that can be built on top of a Tile to modify its properties
 */
public class Building extends DynamicModellable implements EventReceiver, MenuSubject {
    private final CoordUserData userData = new CoordUserData();
    private Optional<Color> minimapColor = Optional.empty();
    private BuildingType type = BuildingType.PASSIVE;
    private boolean obstacle = false;
    protected final Visibility visibility = new Visibility();
    public final BuildingCombat combat;
    public final Tags tags = new Tags();
    public final String name;
    public Optional<Inventory> items = Optional.empty();
    public String desc = "";

    Building(String name, int x, int y) {
        super(x, y);
        this.name = name;
        this.combat = new BuildingCombat(this);
        this.userData.point.x = x;
        this.userData.point.y = y;
    }

    /**
     * This should only be used in conjunction with Kryo rehydration
     */
    public Building() {
        super(0, 0);
        this.name = null;
        this.combat = null;
    }

    /**
     * This method is fired when the underlying Tile's leader field changes
     */
    public void handleLeaderChange(GameView view, Optional<Player> p1, Optional<Player> p2) {
        p1.ifPresent((Player l) -> this.visibility.remove(l, view.game.world));
        p2.ifPresent((Player l) -> this.visibility.set(view, l, this, this.getPoint()));
    }

    /**
     * Sets whether or not this Building is an obstacle. Obstacles cannot be walked
     * on by default.
     */
    public void setObstacle(boolean obstacle) {
        this.obstacle = obstacle;
    }

    /**
     * Returns whether or not this Building is an obstacle. Obstacles cannot be
     * walked on by default.
     */
    public boolean getObstacle() {
        return this.obstacle;
    }

    /**
     * Returns true if this Building is active
     */
    public boolean isActive() {
        return this.type == BuildingType.ACTIVE;
    }

    /**
     * Sets whether or not this is an active Building
     */
    public void setActive() {
        this.type = BuildingType.ACTIVE;
    }

    /**
     * Returns the Minimap Color for this Building (if any exists)
     */
    public Optional<Color> getMinimapColor() {
        return this.minimapColor;
    }

    /**
     * Sets the Minimap Color for this Building
     */
    public void setMinimapColor(int hexcode) {
        this.minimapColor = Optional.of(Colors.fromHex(hexcode));
    }

    /**
     * Spawns this loaded object into the World
     */
    public void spawn(GameView view) {
        view.game.world.getTile(this.x, this.y).ifPresent((Tile t) -> {
            t.building = Optional.of(this);
            t.setGlyph(Optional.empty());
            if (t.unit.isPresent()) {
                this.setAlpha(0.5f);
            }
            view.game.buildingSpawned(this);
        });
        this.handleEvent(view, new Events.SpawnEvent<Building>(this));
        this.getMinimapColor().ifPresent((Color c) -> view.hud.minimap.refresh(view.game.world));
    }

    /** {@inheritdoc} */
    @Override
    protected void setupModelInstance(ModelInstance model) {
        model.userData = this.userData;
    }

    /** {@inheritdoc} */
    @Override
    public Vector3 getPositionVector() {
        return Coords.grid.vector(this.x, this.y).add(Coords.raw.vector(0, Hexagons.HEIGHT, 0));
    }

    /** {@inheritdoc} */
    @Override
    public SideEffect handleEventWithoutSignalBooster(GameView view, Event e) {
        return view.game.events.building.handle(view, this, e);
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
        this.getMinimapColor().ifPresent((Color c) -> view.hud.minimap.refresh(view.game.world));
        this.dispose();
    }

    /** {@inheritdoc} */
    @Override
    public MenuNode getMenuContent(GameView view, Optional<Point> p) {
        Optional<Player> leader = p.flatMap((Point p1) -> view.game.world.getTile(p1.x, p1.y))
                .flatMap((Tile t) -> t.leader);
        ListNode node = new ListNode().add(new HeaderNode(view.av, this.name)).add(new TextNode(view.av, this.desc));
        if (leader.isPresent()) {
            node.add(new TextNode(view.av, String.format("Alignment: %s", leader.get().name)));
        }
        node.add(new TextNode(view.av,
                String.format("Health: %d/%d", this.combat.health.get(), this.combat.health.getMax())));
        if (this.items.isPresent()) {
            if (leader.map((Player p1) -> p1.isHumanPlayer()).orElse(false)) {
                node.add(new TextNode(view.av, String.format("Gold: %d", this.items.get().getTotalGold())));
                node.add(this.items.get().getMenuContent(view, p));
            } else {
                node.add(new TextNode(view.av, String.format("Can store %d items", this.items.get().getMax())));
            }
        }
        return node;
    }

    /**
     * An enum that represents whether a Building is active or passive
     */
    private static enum BuildingType {
        ACTIVE, PASSIVE;
    }
}
