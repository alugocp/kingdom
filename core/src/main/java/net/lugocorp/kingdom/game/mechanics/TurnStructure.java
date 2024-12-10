package net.lugocorp.kingdom.game.mechanics;
import net.lugocorp.kingdom.game.Game;
import net.lugocorp.kingdom.game.core.Events.TickEvent;
import net.lugocorp.kingdom.game.events.EventReceiver;
import net.lugocorp.kingdom.game.mechanics.ArtifactAuction.Auction;
import net.lugocorp.kingdom.game.model.Player;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.ui.game.Hud;
import net.lugocorp.kingdom.ui.menu.ButtonNode;
import net.lugocorp.kingdom.ui.menu.ListNode;
import net.lugocorp.kingdom.ui.menu.Menu;
import net.lugocorp.kingdom.ui.menu.TextNode;
import net.lugocorp.kingdom.ui.views.GameView;
import com.badlogic.gdx.Gdx;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class handles human and AI Players taking turns during the Game
 */
public class TurnStructure {
    // TODO optimize the futures field with a different data structure
    private final List<FutureTick> futures = new ArrayList<>();
    private final Set<Unit> unitsThatHaveActed = new HashSet<>();
    private final Game game;
    private boolean canPlayerAct = false;
    private Player turnPlayer;
    private int turn = 1;

    public TurnStructure(Game game) {
        this.game = game;
        this.turnPlayer = game.human;
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
     * Sets up a TickEvent that will trigger on the given EventReceiver in the given
     * number of turns
     */
    public void addFutureTick(EventReceiver receiver, int turns, boolean repeat) {
        if (turns < 1) {
            throw new RuntimeException("You cannot set up a tick for the current or any past turns");
        }
        this.futures.add(new TurnStructure.FutureTick(receiver, this.turn + turns, new TickEvent(turns, repeat)));
    }

    /**
     * Removes all upcoming FutureTicks associated with the given EventReceiver
     */
    public void removeFutureTicks(EventReceiver receiver) {
        int a = 0;
        while (a < this.futures.size()) {
            FutureTick ft = this.futures.get(a);
            if (ft.receiver == receiver) {
                this.futures.remove(a);
            } else {
                a++;
            }
        }
    }

    /**
     * Triggers a TickEvent for every FutureTick that has reached its time
     */
    private void checkFutureTicks(GameView view) {
        int a = 0;
        while (a < this.futures.size()) {
            FutureTick ft = this.futures.get(a);
            if (ft.turn == this.turn) {
                this.futures.remove(a);
                ft.receiver.handleEvent(view, ft.event);
                if (ft.event.repeat) {
                    this.addFutureTick(ft.receiver, ft.event.turns, true);
                }
            } else {
                a++;
            }
        }
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
            this.turnPlayer = this.game.comps.get(0);
            this.startNewTurnGroup();
        } else {
            int index = this.game.comps.indexOf(this.turnPlayer);
            if (index == this.game.comps.size() - 1) {
                this.turnPlayer = this.game.human;
                this.turn++;
                this.checkFutureTicks(view);
            } else {
                this.turnPlayer = this.game.comps.get(index + 1);
            }
        }
        this.kickOffTurn(view);
    }

    /**
     * This function handles logic whenever every player has had a turn
     */
    private void startNewTurnGroup() {
        this.game.mechanics.dayNight.tick();
        this.game.graphics.getToonShader().setNighttime(this.game.mechanics.dayNight.isNight());
    }

    /**
     * Runs the per-turn logic and then allows the turn Player to act
     */
    public void kickOffTurn(GameView view) {
        // Run per-turn calculations for the turn Player
        this.canPlayerAct = false;
        // TODO run this logic in another thread for optimization
        this.unitsThatHaveActed.clear();
        this.turnPlayer.unitPoints += this.game.mechanics.newUnits.getUnitPointsYield(this.turnPlayer.bareTiles,
                this.turnPlayer.tiles);
        if (this.turnPlayer.isHumanPlayer()) {
            // Check human Player win/lose state
            if (this.game.hasHumanPlayerLost()) {
                view.popups.add(new Menu(Hud.BUTTON_WIDTH, Hud.HEIGHT, Gdx.graphics.getWidth() - (Hud.BUTTON_WIDTH * 2),
                        false, new ListNode().add(new TextNode(this.game.graphics, "You have lost"))
                                .add(new ButtonNode(this.game.graphics, "Okay", () -> {
                                    // TODO return to some main menu
                                }))));
            }
            if (this.game.hasHumanPlayerWon()) {
                view.popups.add(new Menu(Hud.BUTTON_WIDTH, Hud.HEIGHT, Gdx.graphics.getWidth() - (Hud.BUTTON_WIDTH * 2),
                        false, new ListNode().add(new TextNode(this.game.graphics, "You win!"))
                                .add(new ButtonNode(this.game.graphics, "Okay", () -> {
                                    // TODO return to some main menu
                                }))));
            }

            // Choose a new Unit at the maximum unit points
            for (int a = 0; a < Math.floor(this.turnPlayer.unitPoints / NewUnit.MAX_UNIT_POINTS); a++) {
                view.popups.add(this.game.mechanics.newUnits.getNewUnitMenu(view));
            }
            // Start a new ArtifactAuction at the maximum auction points
            if (this.game.auctionPoints >= ArtifactAuction.MAX_AUCTION_POINTS
                    && !this.game.mechanics.auction.getAuction().isPresent()) {
                this.game.auctionPoints -= ArtifactAuction.MAX_AUCTION_POINTS;
                this.game.mechanics.auction.openNewAuction();
                view.popups.add(this.game.mechanics.auction.getAuctionBuyInMenu(view));
            }
            // Show the aftermath of any active ArtifactAuction
            if (this.game.mechanics.auction.getAuction().map((Auction a) -> a.hasBeenDecided(this.game))
                    .orElse(false)) {
                if (this.game.mechanics.auction.getAuction().get().notEveryoneHasSeenResults(this.game)) {
                    view.popups.add(this.game.mechanics.auction.getFollowUpMenu(view));
                    this.game.mechanics.auction.getAuction().get().hasSeenResults();
                } else {
                    this.game.mechanics.auction.closeAuction();
                }
            }
        } else {
            // Handle AI player ArtifactAuction logic
            if (this.game.mechanics.auction.getAuction().map((Auction a) -> a.hasBeenDecided(this.game))
                    .orElse(false)) {
                this.game.mechanics.auction.getAuction().get().hasSeenResults();
            } else if (this.game.mechanics.auction.getAuction().isPresent()) {
                this.game.mechanics.auction.getAuction().get().doNotAddBidder();
            }
        }

        // Allow the turn Player to act
        this.canPlayerAct = true;
        if (!this.turnPlayer.isHumanPlayer()) {
            // TODO implement AI here on another thread
            // Call menu.refresh() afterwards and iterateTurnPlayer()
            this.iterateTurnPlayer(view);
        }
    }

    /**
     * Represents an Event trigger that should happen during some future turn
     */
    private static class FutureTick {
        private final EventReceiver receiver;
        private final TickEvent event;
        private final int turn;

        private FutureTick(EventReceiver receiver, int turn, TickEvent event) {
            this.receiver = receiver;
            this.event = event;
            this.turn = turn;
        }
    }
}
