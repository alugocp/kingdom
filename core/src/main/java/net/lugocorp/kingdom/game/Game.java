package net.lugocorp.kingdom.game;
import net.lugocorp.kingdom.engine.GameGraphics;
import net.lugocorp.kingdom.game.events.AllEventHandlers;
import net.lugocorp.kingdom.game.mechanics.ArtifactAuction;
import net.lugocorp.kingdom.game.mechanics.ArtifactAuction.Auction;
import net.lugocorp.kingdom.game.mechanics.Mechanics;
import net.lugocorp.kingdom.game.mechanics.NewUnit;
import net.lugocorp.kingdom.game.model.Building;
import net.lugocorp.kingdom.game.model.Generator;
import net.lugocorp.kingdom.game.model.Player;
import net.lugocorp.kingdom.game.model.Tile;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.game.pools.Content;
import net.lugocorp.kingdom.game.world.World;
import net.lugocorp.kingdom.ui.Hud;
import net.lugocorp.kingdom.ui.menu.ButtonNode;
import net.lugocorp.kingdom.ui.menu.ListNode;
import net.lugocorp.kingdom.ui.menu.Menu;
import net.lugocorp.kingdom.ui.menu.TextNode;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.math.Point;
import com.badlogic.gdx.Gdx;
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
    private final Set<Unit> unitsThatHaveActed = new HashSet<>();
    private Player turnPlayer = new Player("you", true);
    private boolean canPlayerAct = false;
    public final List<Player> comps = new ArrayList<>();
    public final Mechanics mechanics = new Mechanics();
    public final Content content = new Content();
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
        this.playerBuildings.put(this.human, new ArrayList<Building>());
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
            // Check human Player win/lose state
            if (this.hasHumanPlayerLost()) {
                view.popups.add(new Menu(Hud.BUTTON_WIDTH, Hud.HEIGHT, Gdx.graphics.getWidth() - (Hud.BUTTON_WIDTH * 2),
                        false, new ListNode().add(new TextNode(view.game.graphics, "You have lost"))
                                .add(new ButtonNode(view.game.graphics, "Okay", () -> {
                                    // TODO return to some main menu
                                }))));
            }
            if (this.hasHumanPlayerWon()) {
                view.popups.add(new Menu(Hud.BUTTON_WIDTH, Hud.HEIGHT, Gdx.graphics.getWidth() - (Hud.BUTTON_WIDTH * 2),
                        false, new ListNode().add(new TextNode(view.game.graphics, "You win!"))
                                .add(new ButtonNode(view.game.graphics, "Okay", () -> {
                                    // TODO return to some main menu
                                }))));
            }

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
            // Show the aftermath of any active ArtifactAuction
            if (this.mechanics.auction.getAuction().map((Auction a) -> a.hasBeenDecided(this)).orElse(false)) {
                if (this.mechanics.auction.getAuction().get().notEveryoneHasSeenResults(this)) {
                    view.popups.add(this.mechanics.auction.getFollowUpMenu(view));
                    this.mechanics.auction.getAuction().get().hasSeenResults();
                } else {
                    this.mechanics.auction.closeAuction();
                }
            }
        } else {
            // Handle AI player ArtifactAuction logic
            if (this.mechanics.auction.getAuction().map((Auction a) -> a.hasBeenDecided(this)).orElse(false)) {
                this.mechanics.auction.getAuction().get().hasSeenResults();
            } else if (this.mechanics.auction.getAuction().isPresent()) {
                this.mechanics.auction.getAuction().get().doNotAddBidder();
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
        Player player = new Player(name, false);
        this.comps.add(player);
        this.playerBuildings.put(player, new ArrayList<Building>());
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
    private boolean hasHumanPlayerLost() {
        return this.playerBuildings.get(this.human).size() == 0;
    }

    /**
     * Returns true if the human Player is the only one that has buildings left
     */
    private boolean hasHumanPlayerWon() {
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
}
