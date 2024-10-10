package net.lugocorp.kingdom.game;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.lugocorp.kingdom.engine.GameGraphics;
import net.lugocorp.kingdom.events.EventHandlerBundle;
import net.lugocorp.kingdom.world.World;

/**
 * Stores all the data for a single ongoing game
 */
public class Game {
    private final Set<Unit> unitsThatHaveActed = new HashSet<>();
    private Player turnPlayer = new Player("you", true);
    public final List<Player> comps = new ArrayList<>();
    public final EventHandlerBundle events;
    public final GameGraphics graphics;
    public final Generator generator;
    public final Player human;
    public final World world;

    public Game(GameGraphics graphics, EventHandlerBundle events, World world) {
        this.generator = new Generator(this);
        this.human = this.turnPlayer;
        this.graphics = graphics;
        this.events = events;
        this.world = world;
    }

    /**
     * Returns the turn Player
     */
    public Player getTurnPlayer() {
        return this.turnPlayer;
    }

    /**
     * Sets up the next turn Player
     */
    public void iterateTurnPlayer() {
        this.unitsThatHaveActed.clear();
        if (this.turnPlayer == this.human) {
            this.turnPlayer = this.comps.get(0);
        } else {
            int index = this.comps.indexOf(this.turnPlayer);
            if (index == this.comps.size() - 1) {
                this.turnPlayer = this.human;
            } else {
                this.turnPlayer = this.comps.get(index + 1);
            }
        }
    }

    /**
     * Registers a new AI Player
     */
    public void addComputerPlayer(String name) {
        this.comps.add(new Player(name, false));
    }

    /**
     * Marks a Unit as having acted this turn
     */
    public void unitHasActed(Unit u) {
        this.unitsThatHaveActed.add(u);
    }

    /**
     * Returns true if the given Unit has acted yet this turn
     */
    public boolean hasUnitActed(Unit u) {
        return this.unitsThatHaveActed.contains(u);
    }

    /**
     * Sets the leader Player for a given Tile
     */
    public void setLeader(Tile t, Player p) {
        if (!t.leader.isPresent() || t.leader.get() != p) {
            if (!t.building.isPresent()) {
                p.bareTiles++;
            }
            p.tiles++;
        }
        t.leader = Optional.of(p);
    }

    /**
     * Sets the leader Player for a given Unit
     */
    public void setLeader(Unit u, Player p) {
        u.leader = Optional.of(p);
        this.setLeader(this.world.getTile(u.getX(), u.getY()).get(), p);
    }
}
