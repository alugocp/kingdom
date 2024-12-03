package net.lugocorp.kingdom.game;
import net.lugocorp.kingdom.engine.GameGraphics;
import net.lugocorp.kingdom.game.events.AllEventHandlers;
import net.lugocorp.kingdom.game.mechanics.Mechanics;
import net.lugocorp.kingdom.game.model.Building;
import net.lugocorp.kingdom.game.model.Generator;
import net.lugocorp.kingdom.game.model.Glyph;
import net.lugocorp.kingdom.game.model.Player;
import net.lugocorp.kingdom.game.model.Tile;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.game.world.World;
import net.lugocorp.kingdom.utils.math.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Stores all the data for a single ongoing game
 */
public class Game {
    private final Map<Player, List<Building>> playerBuildings = new HashMap<>();
    public final List<Player> comps = new ArrayList<>();
    public final AllEventHandlers events;
    public final GameGraphics graphics;
    public final Mechanics mechanics;
    public final Player human;
    public final World world;
    public Generator generator;
    public int auctionPoints = 0;

    public Game(GameGraphics graphics, AllEventHandlers events, World world) {
        this.human = new Player("you", true);
        this.graphics = graphics;
        this.events = events;
        this.world = world;
        this.playerBuildings.put(this.human, new ArrayList<Building>());
        this.mechanics = new Mechanics(this);
    }

    /**
     * This function returns the initial Unit for the given Player. Note that it
     * does not spawn the Unit, but it does remove them from all relevant
     * GlyphPools.
     */
    public Unit getInitialUnit(Player p, int x, int y, Glyph g) {
        String name = this.mechanics.pools.random(g, 1)[0];
        Unit u = this.generator.unit(name, x, y);
        this.mechanics.pools.remove(u);
        this.setLeader(u, p);
        return u;
    }

    /**
     * Registers a new AI Player
     */
    public Player addComputerPlayer(String name) {
        Player player = new Player(name, false);
        this.comps.add(player);
        this.playerBuildings.put(player, new ArrayList<Building>());
        return player;
    }

    /**
     * Sets the leader Player for a given Tile
     */
    public void setLeader(Tile t, Player p) {
        if (!t.leader.isPresent() || t.leader.get() != p) {
            if (t.building.isPresent()) {
                if (t.leader.isPresent()) {
                    this.playerBuildings.get(t.leader.get()).remove(t.building.get());
                }
                this.playerBuildings.get(p).add(t.building.get());
            } else {
                p.bareTiles++;
            }
            p.tiles++;
        }
        t.leader = Optional.of(p);
    }

    /**
     * Call this when a Building is spawned into the World. It may need to be
     * tracked as part of a Player's Buildings.
     */
    public void buildingSpawned(Building b) {
        Tile t = this.world.getTile(b.getPoint()).get();
        t.leader.ifPresent((Player p) -> {
            this.playerBuildings.get(p).add(b);
            p.bareTiles--;
        });
    }

    /**
     * Removes a Building from the World and from cached Game data
     */
    public void removeBuilding(Building b) {
        Tile t = this.world.getTile(b.getPoint()).get();
        t.building = Optional.empty();
        t.leader.ifPresent((Player p) -> {
            this.playerBuildings.get(p).remove(b);
            p.bareTiles++;
        });
    }

    /**
     * Sets the leader Player for a given Unit
     */
    public void setLeader(Unit u, Player p) {
        u.leader = Optional.of(p);
        this.setLeader(this.world.getTile(u.getX(), u.getY()).get(), p);
    }

    /**
     * Returns true if the human Player has no buildings left
     */
    public boolean hasHumanPlayerLost() {
        return this.playerBuildings.get(this.human).size() == 0;
    }

    /**
     * Returns true if the human Player is the only one that has buildings left
     */
    public boolean hasHumanPlayerWon() {
        for (Player p : this.playerBuildings.keySet()) {
            if (p != this.human && this.playerBuildings.get(p).size() > 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns all Buildings under a Player's control that can store Items
     */
    public Set<Point> getVaultBuildings(Player player) {
        Set<Point> vaults = new HashSet<>();
        for (Building b : this.playerBuildings.get(player)) {
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
     * by another Unit, 4) The associated glyph has units in its pool
     */
    public Set<Point> getRecruitmentTiles(Player player) {
        // TODO optimize this PLEASE
        Set<Point> points = new HashSet<>();
        for (int a = 0; a < this.world.getWidth(); a++) {
            for (int b = 0; b < this.world.getHeight(); b++) {
                if (this.world.getTile(a, b)
                        .map((Tile t) -> t.leader.isPresent() && t.leader.get() == player && !t.unit.isPresent()
                                && t.glyph.isPresent() && this.mechanics.pools.remaining(t.glyph.get()) > 0)
                        .orElse(false)) {
                    points.add(new Point(a, b));
                }
            }
        }
        return points;
    }
}
