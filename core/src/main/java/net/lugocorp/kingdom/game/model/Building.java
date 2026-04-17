package net.lugocorp.kingdom.game.model;
import net.lugocorp.kingdom.builtin.Events;
import net.lugocorp.kingdom.color.ColorScheme;
import net.lugocorp.kingdom.color.Colors;
import net.lugocorp.kingdom.engine.userdata.CoordUserData;
import net.lugocorp.kingdom.game.layers.Entity;
import net.lugocorp.kingdom.game.layers.Spawnable;
import net.lugocorp.kingdom.game.player.Player;
import net.lugocorp.kingdom.game.properties.BuildingType;
import net.lugocorp.kingdom.game.properties.EntityType;
import net.lugocorp.kingdom.game.properties.Inventory;
import net.lugocorp.kingdom.gameplay.events.Event;
import net.lugocorp.kingdom.math.Coords;
import net.lugocorp.kingdom.math.Hexagons;
import net.lugocorp.kingdom.math.Point;
import net.lugocorp.kingdom.menu.MenuNode;
import net.lugocorp.kingdom.menu.MenuSubject;
import net.lugocorp.kingdom.menu.game.ResourceBarsNode;
import net.lugocorp.kingdom.menu.structure.ListNode;
import net.lugocorp.kingdom.menu.structure.RowNode;
import net.lugocorp.kingdom.menu.text.BadgeNode;
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
import java.util.function.Supplier;

/**
 * Some structure that can be built on top of a Tile to modify its properties
 */
public class Building extends Entity implements MenuSubject, Spawnable {
    @FieldSerializer.Optional("getTile")
    private final Supplier<Tile> getTile;
    private final CoordUserData userData;
    private Optional<Color> minimapColor = Optional.empty();
    private BuildingType type = BuildingType.PASSIVE;
    private boolean obstacle = false;
    public Optional<Inventory> items = Optional.empty();

    Building(String name, int x, int y, Supplier<Tile> getTile) {
        super(name, x, y);
        this.getTile = getTile;
        this.userData = new CoordUserData(() -> !this.getTile.get().isVisible());
        this.userData.point.x = x;
        this.userData.point.y = y;
    }

    /**
     * This should only be used in conjunction with Kryo rehydration
     */
    public Building() {
        super(null, 0, 0);
        this.userData = null;
        this.getTile = null;
    }

    /** {@inheritdoc} */
    @Override
    public EntityType getEntityType() {
        return EntityType.BUILDING;
    }

    /** {@inheritdoc} */
    @Override
    public Optional<Player> getLeader() {
        return this.getTile.get().leader;
    }

    /**
     * Returns a tab label for this Building in the Tile Menu
     */
    public String getMenuTabLabel() {
        return "Building";
    }

    /**
     * This method is fired when the underlying Tile's leader field changes
     */
    public void handleLeaderChange(GameView view, Optional<Player> p1, Optional<Player> p2) {
        p1.ifPresent((Player l) -> this.vision.remove(l, view.game.world));
        p2.ifPresent((Player l) -> this.vision.set(view, l, this, this.getPoint()));
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

    /** {@inheritdoc} */
    @Override
    public void spawn(GameView view) {
        view.game.world.getTile(this.x, this.y).ifPresent((Tile t) -> {
            t.building = Optional.of(this);
            t.setGlyph(Optional.empty());
            if (t.unit.isPresent()) {
                this.setAlpha(0.5f);
            }
            t.leader.ifPresent((Player p) -> {
                this.vision.set(view, p, this, this.getPoint());
                p.buildings.add(this);
            });
        });
        this.handleEvent(view, new Events.SpawnEvent<Building>(this)).execute();
        this.getMinimapColor().ifPresent((Color c) -> view.hud.bot.minimap.refresh(view.game.world));
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
    public void deactivate(GameView view) {
        super.deactivate(view);
        this.getLeader().ifPresent((Player l) -> {
            this.vision.remove(l, view.game.world);
            l.buildings.remove(this);
        });
        view.game.world.getTile(this.getPoint()).get().building = Optional.empty();
        this.getMinimapColor().ifPresent((Color c) -> view.hud.bot.minimap.refresh(view.game.world));
        this.dispose();
    }

    /** {@inheritdoc} */
    @Override
    public MenuNode getMenuContent(GameView view, Optional<Point> p) {
        final Optional<Player> leader = p.flatMap((Point p1) -> view.game.world.getTile(p1.x, p1.y))
                .flatMap((Tile t) -> t.leader);
        final RowNode node = new RowNode().setColumns(2);
        final ListNode col1 = new ListNode().add(new HeaderNode(view.av, this.name));
        final RowNode row = new RowNode().addRatio(30, new BadgeNode(view.av, ColorScheme.BLUE.hex,
                ColorScheme.WHITE.hex, this.isActive() ? "Active" : "Passive"));
        if (leader.isPresent()) {
            row.add(new PlayerBadgeNode(view.av, leader.get()));
        }
        col1.add(row).add(new TextNode(view.av, this.desc)).add(new ResourceBarsNode(view.av,
                new ResourceBarsNode.Bar("Health", 0x3d9e33, this.combat.health.get(), this.combat.health.getMax())));
        node.add(col1);
        if (this.items.isPresent()) {
            final ListNode col2 = new ListNode();
            if (leader.map((Player p1) -> p1.isHumanPlayer()).orElse(false)) {
                col2.add(this.items.get().getMenuContent(view, p)).add(new BadgeNode(view.av, 0xb5b31f,
                        ColorScheme.WHITE.hex, String.format("%d gold", this.items.get().getTotalGold())));
            } else {
                col2.add(new TextNode(view.av, String.format("Can store %d items", this.items.get().getMax())));
            }
            node.add(col2);
        }
        return node;
    }
}
