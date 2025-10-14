package net.lugocorp.kingdom.game;
import net.lugocorp.kingdom.builtin.Events;
import net.lugocorp.kingdom.engine.AudioVideo;
import net.lugocorp.kingdom.engine.render.Modellable;
import net.lugocorp.kingdom.game.actions.ActionManager;
import net.lugocorp.kingdom.game.events.AllEventHandlers;
import net.lugocorp.kingdom.game.future.FutureEventManager;
import net.lugocorp.kingdom.game.glyph.Glyph;
import net.lugocorp.kingdom.game.mechanics.Mechanics;
import net.lugocorp.kingdom.game.model.Building;
import net.lugocorp.kingdom.game.model.Generator;
import net.lugocorp.kingdom.game.model.Tile;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.game.player.CompPlayer;
import net.lugocorp.kingdom.game.player.HumanPlayer;
import net.lugocorp.kingdom.game.player.Player;
import net.lugocorp.kingdom.game.player.PlayerColors;
import net.lugocorp.kingdom.game.world.World;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.code.Lambda;
import net.lugocorp.kingdom.utils.math.Point;
import com.esotericsoftware.kryo.serializers.FieldSerializer;
import java.time.OffsetTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Stores all the data for a single ongoing game
 */
public class Game {
    public final FutureEventManager future = new FutureEventManager(this);
    public final ActionManager actions = new ActionManager();
    public final PlayerColors colorPool = new PlayerColors();
    public final List<CompPlayer> comps = new ArrayList<>();
    public final Set<Unit> units = new HashSet<>();
    public final World world = new World();
    public final OffsetTime startTime;
    public final Mechanics mechanics;
    public final HumanPlayer human;
    @FieldSerializer.Optional("events")
    public AllEventHandlers events;
    @FieldSerializer.Optional("generator")
    public Generator generator;

    public Game(AllEventHandlers events, OffsetTime startTime) {
        this.events = events;
        this.startTime = startTime;
        this.human = new HumanPlayer(this.colorPool.getFromPool());
        this.mechanics = new Mechanics(this);
    }

    /**
     * This should only be used in conjunction with Kryo rehydration
     */
    public Game() {
        this.startTime = null;
        this.mechanics = null;
        this.human = null;
    }

    /**
     * Call this function on a Game that has been saved to a file and must now be
     * reloaded
     */
    public void rehydrateFromKryo(AudioVideo av, AllEventHandlers events, Generator generator) {
        this.events = events;
        this.generator = generator;
        for (Modellable m : this.world.getModellables(true)) {
            m.rehydrateFromKryo(av);
        }
    }

    /**
     * This function returns the initial Unit for the given Player. Note that it
     * does not spawn the Unit, but it does remove them from all relevant
     * GlyphPools.
     */
    public Unit getInitialUnit(GameView view, Player p, int x, int y) {
        final Events.GetInitialGlyphEvent e = new Events.GetInitialGlyphEvent();
        p.getFate().handleEvent(view, e).execute();
        final Glyph g = e.glyph.orElse(Lambda.random(Glyph.class));
        final String name = this.mechanics.pools.random(g, 1)[0];
        final Unit u = this.generator.unit(name, x, y);
        this.mechanics.pools.remove(u);
        this.setLeader(view, u, p);
        return u;
    }

    /**
     * Returns all Players in this Game
     */
    public Set<Player> getAllPlayers() {
        final Set<Player> players = new HashSet<>();
        players.addAll(this.comps);
        players.add(this.human);
        return players;
    }

    /**
     * Calls into the other setLeader()
     */
    public void setLeader(GameView view, Tile t, Player p) {
        this.setLeader(view, t, Optional.of(p));
    }

    /**
     * Sets the leader Player for a given Tile
     */
    public void setLeader(GameView view, Tile t, Optional<Player> op) {
        if (op.equals(t.leader)) {
            return;
        }
        t.leader.ifPresent((Player p) -> {
            t.building.ifPresent((Building b) -> p.buildings.remove(b));
            p.tiles--;
        });
        op.ifPresent((Player p) -> {
            t.building.ifPresent((Building b) -> p.buildings.add(b));
            p.tiles++;
        });
        t.building.ifPresent((Building b) -> b.handleLeaderChange(view, t.leader, op));
        t.leader = op;
        t.calculateBorders(this.world, true);
    }

    /**
     * Call this when a Building is spawned into the World. It may need to be
     * tracked as part of a Player's Buildings.
     */
    public void buildingSpawned(Building b) {
        Tile t = this.world.getTile(b.getPoint()).get();
        t.leader.ifPresent((Player p) -> p.buildings.add(b));
    }

    /**
     * Removes a Building from the World and from cached Game data
     */
    public void removeBuilding(Building b) {
        Tile t = this.world.getTile(b.getPoint()).get();
        t.building = Optional.empty();
        t.leader.ifPresent((Player p) -> p.buildings.remove(b));
    }

    /**
     * Calls into the other setLeader()
     */
    public void setLeader(GameView view, Unit u, Player p) {
        this.setLeader(view, u, Optional.of(p));
    }

    /**
     * Sets the leader Player for a given Unit
     */
    public void setLeader(GameView view, Unit u, Optional<Player> op) {
        u.getLeader().ifPresent((Player p) -> p.units.remove(u));
        op.ifPresent((Player p) -> p.units.add(u));
        u.getLeader().ifPresent((Player l) -> u.vision.remove(l, this.world));
        u.leadership.setLeader(op);
        u.getLeader().ifPresent((Player l) -> u.vision.set(view, l, u, u.getPoint()));
        this.setLeader(view, this.world.getTile(u.getPoint()).get(), op);
    }

    /**
     * Returns true if the human Player has no buildings left
     */
    public boolean hasHumanPlayerLost() {
        // TODO optimize this
        return Lambda.filter((Building b) -> b.isActive(), this.human.buildings).size() == 0;
    }

    /**
     * Returns true if the human Player is the only one that has buildings left
     */
    public boolean hasHumanPlayerWon() {
        // TODO optimize this too
        for (Player p : this.comps) {
            boolean remaining = Lambda.filter((Building b) -> b.isActive(), p.buildings).size() > 0;
            if (remaining) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns all Buildings under a Player's control that can store Items
     */
    public Set<Point> getVaultBuildings(Player player) {
        // TODO should probably optimize this
        Set<Point> vaults = new HashSet<>();
        for (Building b : player.buildings) {
            if (b.items.isPresent()) {
                vaults.add(b.getPoint());
            }
        }
        return vaults;
    }

    /**
     * Returns the Points where the Player can recruit a new Unit. The Tiles at
     * these Points all have the following properties: 1) Is under the control of
     * the Player in question, 2) Has a glyph associated with it, 3) Is not occupied
     * by another Unit, 4) The associated glyph has units in its pool, 5) The Tile
     * is not an obstacle, 6) If there is a building on the Tile then it is not an
     * obstacle either
     */
    public Set<Point> getRecruitmentTiles(Player player) {
        // TODO optimize this PLEASE
        Set<Point> points = new HashSet<>();
        for (int a = 0; a < this.world.getWidth(); a++) {
            for (int b = 0; b < this.world.getHeight(); b++) {
                if (this.world.getTile(a, b)
                        .map((Tile t) -> t.leader.isPresent() && t.leader.get() == player && !t.unit.isPresent()
                                && t.getGlyph().isPresent() && this.mechanics.pools.remaining(t.getGlyph().get()) > 0
                                && !t.getObstacle()
                                && !t.building.map((Building bldg) -> bldg.getObstacle()).orElse(false))
                        .orElse(false)) {
                    points.add(new Point(a, b));
                }
            }
        }
        return points;
    }
}
