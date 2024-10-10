package net.lugocorp.kingdom.game;
import net.lugocorp.kingdom.engine.GameGraphics;
import net.lugocorp.kingdom.events.EventHandlerBundle;
import net.lugocorp.kingdom.game.mechanics.NewUnit;
import net.lugocorp.kingdom.game.model.Generator;
import net.lugocorp.kingdom.game.model.Player;
import net.lugocorp.kingdom.game.model.Tile;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.world.World;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Stores all the data for a single ongoing game
 */
public class Game {
    private final Set<Unit> unitsThatHaveActed = new HashSet<>();
    private Player turnPlayer = new Player("you", true);
    private boolean canPlayerAct = false;
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
     * Returns true when the turn Player is the human
     */
    public boolean canHumanPlayerAct() {
        return this.turnPlayer.isHumanPlayer() && this.canPlayerAct;
    }

    /**
     * Sets up the next turn Player
     */
    public void iterateTurnPlayer(GameView view) {
        if (this.turnPlayer.isHumanPlayer()) {
            this.turnPlayer = this.comps.get(0);
        } else {
            int index = this.comps.indexOf(this.turnPlayer);
            if (index == this.comps.size() - 1) {
                this.turnPlayer = this.human;
            } else {
                this.turnPlayer = this.comps.get(index + 1);
            }
        }
        this.kickOffTurn(view);
    }

    /**
     * Runs the per-turn logic and then allows the turn Player to act
     */
    public void kickOffTurn(GameView view) {
        // Run per-turn calculations for the turn Player
        this.canPlayerAct = false;
        // TODO run this logic in another thread for optimization
        this.unitsThatHaveActed.clear();
        this.turnPlayer.unitPoints += NewUnit.getUnitPointsYield(this.turnPlayer.bareTiles, this.turnPlayer.tiles);
        if (this.turnPlayer.isHumanPlayer() && this.turnPlayer.unitPoints >= NewUnit.MAX_UNIT_POINTS) {
            view.addPopup(NewUnit.getNewUnitMenu(view));
        }

        // Allow the turn Player to act
        this.canPlayerAct = true;
        if (!this.turnPlayer.isHumanPlayer()) {
            // TODO implement AI here on another thread
            // Call refreshMenu() afterwards and iterateTurnPlayer()
            this.iterateTurnPlayer(view);
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
