package net.lugocorp.kingdom.game;
import net.lugocorp.kingdom.engine.GameGraphics;
import net.lugocorp.kingdom.game.events.AllEventHandlers;
import net.lugocorp.kingdom.game.mechanics.ArtifactAuction;
import net.lugocorp.kingdom.game.mechanics.ArtifactAuction.Auction;
import net.lugocorp.kingdom.game.mechanics.Mechanics;
import net.lugocorp.kingdom.game.mechanics.NewUnit;
import net.lugocorp.kingdom.game.model.Generator;
import net.lugocorp.kingdom.game.model.Player;
import net.lugocorp.kingdom.game.model.Tile;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.game.world.World;
import net.lugocorp.kingdom.ui.views.GameView;
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
    public final Mechanics mechanics = new Mechanics();
    public final AllEventHandlers events;
    public final GameGraphics graphics;
    public final Player human;
    public final World world;
    public Generator generator;

    public Game(GameGraphics graphics, AllEventHandlers events, World world) {
        this.human = this.turnPlayer;
        this.generator = generator;
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
        this.turnPlayer.unitPoints += this.mechanics.newUnits.getUnitPointsYield(this.turnPlayer.bareTiles,
                this.turnPlayer.tiles);
        if (this.turnPlayer.isHumanPlayer()) {
            // Choose a new Unit at the maximum unit points
            if (this.turnPlayer.unitPoints >= NewUnit.MAX_UNIT_POINTS) {
                this.turnPlayer.unitPoints -= NewUnit.MAX_UNIT_POINTS;
                view.popups.add(this.mechanics.newUnits.getNewUnitMenu(view));
            }
            // Start a new ArtifactAuction at the maximum auction points
            if (this.turnPlayer.auctionPoints >= ArtifactAuction.MAX_AUCTION_POINTS
                    && !this.mechanics.auction.getAuction().isPresent()) {
                this.turnPlayer.auctionPoints -= ArtifactAuction.MAX_AUCTION_POINTS;
                this.mechanics.auction.openNewAuction();
                view.popups.add(this.mechanics.auction.getAuctionBuyInMenu(view));
            }
            // Show the aftermatch of any active ArtifactAuction
            if (this.mechanics.auction.getAuction().map((Auction a) -> a.hasBeenDecided(this)).orElse(false)) {
                if (this.mechanics.auction.getAuction().get().notEveryoneHasSeenResults()) {
                    this.mechanics.auction.getFollowUpMenu(view);
                    this.mechanics.auction.getAuction().get().hasSeenResults();
                } else {
                    this.mechanics.auction.closeAuction();
                }
            }
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
