package net.lugocorp.kingdom.game.model;
import net.lugocorp.kingdom.core.Events.CanUnitMoveEvent;
import net.lugocorp.kingdom.core.Events.UnitMoveDistanceEvent;
import net.lugocorp.kingdom.engine.Modellable;
import net.lugocorp.kingdom.events.Event;
import net.lugocorp.kingdom.events.EventTarget;
import net.lugocorp.kingdom.game.Game;
import net.lugocorp.kingdom.game.model.Inventory.InventoryType;
import net.lugocorp.kingdom.math.Coords;
import net.lugocorp.kingdom.math.Hexagons;
import net.lugocorp.kingdom.math.Point;
import net.lugocorp.kingdom.ui.menu.ButtonNode;
import net.lugocorp.kingdom.ui.menu.HeaderNode;
import net.lugocorp.kingdom.ui.menu.ListNode;
import net.lugocorp.kingdom.ui.menu.MenuNode;
import net.lugocorp.kingdom.ui.menu.MenuSubject;
import net.lugocorp.kingdom.ui.menu.TextNode;
import net.lugocorp.kingdom.ui.views.GameView;
import com.badlogic.gdx.math.Vector3;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * A single controllable entity (or NPC) that the player can interact with
 * in-game
 */
public class Unit extends Modellable implements EventTarget, MenuSubject {
    public final String name;
    public Optional<Player> leader = Optional.empty();
    public Optional<Ability> active1 = Optional.empty();
    public Optional<Ability> active2 = Optional.empty();
    public List<Ability> passives = new ArrayList<>();
    public Inventory equipped = new Inventory(InventoryType.EQUIP, 2);
    public Inventory haul = new Inventory(InventoryType.HAUL, 4);

    Unit(String name, int x, int y) {
        super(x, y);
        this.name = name;
    }

    /**
     * Returns the maximum distance that this Unit can move in a turn
     */
    private int getMaxMoveDistance(Game g) {
        UnitMoveDistanceEvent event = new UnitMoveDistanceEvent(this);
        this.handleEvent(g, event);
        return event.distance;
    }

    /**
     * Returns the list of Points that this Unit can move to
     */
    private Set<Point> getMoveTargets(Game g) {
        int max = this.getMaxMoveDistance(g);
        if (max == 0) {
            return new HashSet<Point>();
        }
        Point origin = new Point(this.x, this.y);
        Set<Point> next = new HashSet<>();
        Set<Point> targets = new HashSet<>();
        Set<Point> visited = new HashSet<>();
        Set<Point> adj = Hexagons.getNeighbors(origin, 1);
        visited.add(origin);
        for (int a = 0; a < max; a++) {
            for (Point p : adj) {
                // Optimization: skip already visited Points
                if (visited.contains(p)) {
                    continue;
                }
                visited.add(p);

                // Units cannot walk on Tiles that don't exist or already have a Unit
                Optional<Tile> t = g.world.getTile(p);
                if (!t.isPresent()) {
                    continue;
                }
                Tile tile = t.get();
                if (tile.unit.isPresent()) {
                    continue;
                }

                // Use event handler to check if this Unit can move here
                CanUnitMoveEvent event = new CanUnitMoveEvent(this, tile);
                this.handleEvent(g, event);
                if (!event.possible) {
                    continue;
                }
                targets.add(p);
                if (a < max - 1) {
                    next.addAll(Hexagons.getNeighbors(p, 1));
                }
            }
            visited.addAll(adj);
            adj.clear();
            adj.addAll(next);
            next.clear();
        }
        return targets;
    }

    /**
     * Moves this Unit to another Tile in the grid
     */
    private void move(Game g, Point p) {
        Tile origin = g.world.getTile(this.x, this.y).get();
        Tile destin = g.world.getTile(p).get();
        origin.unit = Optional.empty();
        destin.unit = Optional.of(this);
        this.x = p.x;
        this.y = p.y;
        this.resetModelPosition();
        this.leader.ifPresent((Player p1) -> g.setLeader(destin, p1));
    }

    /** {@inheritdoc} */
    @Override
    public void handleEvent(Game g, Event e) {
        g.events.unit.handle(g, this.name, e);
    }

    /** {@inheritdoc} */
    public Vector3 getPositionVector() {
        return Coords.grid.vector(this.x, this.y).add(Coords.raw.vector(0, Hexagons.HEIGHT, 0));
    }

    /** {@inheritdoc} */
    @Override
    public MenuNode getMenuContent(GameView view, int x, int y) {
        ListNode node = new ListNode().add(new HeaderNode(view.game.graphics, this.name));
        if (this.leader.isPresent()) {
            node.add(new TextNode(view.game.graphics, String.format("Alignment: %s", this.leader.get().name)));
        }
        if (this.leader.map((Player p) -> p.isHumanPlayer()).orElse(false)) {
            ButtonNode move = new ButtonNode(view.game.graphics, "Move",
                    () -> view.selectTiles(this.getMoveTargets(view.game), "This unit cannot move", (Point p) -> {
                        this.move(view.game, p);
                        view.game.unitHasActed(this);
                    }));
            if (view.game.hasUnitActed(this)) {
                move.disable();
            }
            node.add(move);
        }
        this.active1.ifPresent((Ability a) -> node.add(a.getMenuContent(view, x, y)));
        this.active2.ifPresent((Ability a) -> node.add(a.getMenuContent(view, x, y)));
        for (Ability a : this.passives) {
            node.add(a.getMenuContent(view, x, y));
        }
        if (this.leader.map((Player p) -> p.isHumanPlayer()).orElse(false)) {
            node.add(new TextNode(view.game.graphics, "Equipped Items"));
            node.add(this.equipped.getMenuContent(view, x, y));
            node.add(new TextNode(view.game.graphics, "Hauled Items"));
            node.add(this.haul.getMenuContent(view, x, y));
        } else {
            node.add(new TextNode(view.game.graphics, String.format("Can equip %d items", this.equipped.getMax())));
            node.add(new TextNode(view.game.graphics, String.format("Can haul %d items", this.haul.getMax())));
        }
        return node;
    }
}
