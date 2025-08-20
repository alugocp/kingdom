package net.lugocorp.kingdom.game.mechanics;
import net.lugocorp.kingdom.game.Game;
import net.lugocorp.kingdom.game.core.Events;
import net.lugocorp.kingdom.game.events.EventReceiver;
import net.lugocorp.kingdom.game.mechanics.ArtifactAuction.Auction;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.game.player.CompPlayer;
import net.lugocorp.kingdom.game.player.Player;
import net.lugocorp.kingdom.ui.menu.ButtonNode;
import net.lugocorp.kingdom.ui.menu.ListNode;
import net.lugocorp.kingdom.ui.menu.Menu;
import net.lugocorp.kingdom.ui.menu.TextNode;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.math.Coords;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * This class handles human and AI Players taking turns during the Game
 */
public class TurnStructure {
    // TODO optimize the futures field with a different data structure
    private final List<FutureTick> futures = new ArrayList<>();
    private final Set<Unit> unitsThatHaveActed = new HashSet<>();
    private boolean canPlayerAct = false;
    private Player turnPlayer;
    private int turn = 1;

    public TurnStructure(Game game) {
        this.turnPlayer = game.human;
    }

    /**
     * This should only be used in conjunction with Kryo rehydration
     */
    public TurnStructure() {
    }

    /**
     * Opens a TileMenu on the next Unit who must act this turn
     */
    public boolean goToNextUnit(GameView view) {
        Optional<Unit> next = this.getNextUnitToAct(view.game.human);
        if (next.isPresent()) {
            view.centerOnPoint(next.get().getPoint());
            view.selector.hover(next.get().getPoint());
            view.menu.open();
            return true;
        }
        return false;
    }

    /**
     * Marks a Unit as having acted this turn
     */
    public void unitHasActed(GameView view, Unit u) {
        this.unitsThatHaveActed.add(u);
        u.wakeUp();
        if (u.getLeader().isPresent() && u.getLeader().get() == view.game.human) {
            this.goToNextUnit(view);
        }
    }

    /**
     * Returns true if the given Unit has acted yet this turn
     */
    public boolean hasUnitActed(Unit u) {
        return this.unitsThatHaveActed.contains(u);
    }

    /**
     * Returns the next Unit that must act this turn
     */
    public Optional<Unit> getNextUnitToAct(Player player) {
        for (Unit u : player.units) {
            if (!this.hasUnitActed(u) && !u.isSleeping()) {
                return Optional.of(u);
            }
        }
        return Optional.empty();
    }

    /**
     * Sets up an Event that will trigger on the given EventReceiver in the given
     * number of turns
     */
    public void addFutureTick(String channel, EventReceiver receiver, int interval, boolean repeat) {
        if (interval < 1) {
            throw new RuntimeException("You cannot set up a tick for the current or any past turns");
        }
        this.futures.add(new FutureTick(receiver, channel, this.turn + interval, interval, repeat));
    }

    /**
     * Returns a list of future Events by their receiver and maybe a channel
     */
    private List<FutureTick> getFutureTicks(EventReceiver receiver, Optional<String> channel) {
        List<FutureTick> lst = new ArrayList<>();
        for (FutureTick ft : this.futures) {
            if (ft.receiver == receiver && channel.map((String ch) -> ch == ft.channel).orElse(true)) {
                lst.add(ft);
            }
        }
        return lst;
    }

    /**
     * Removes all upcoming FutureTicks associated with the given EventReceiver
     */
    public void removeFutureTicks(EventReceiver receiver) {
        for (FutureTick ft : this.getFutureTicks(receiver, Optional.empty())) {
            this.futures.remove(ft);
        }
    }

    /**
     * Removes all future Events registered for the given receiver on a single
     * channel
     */
    public void removeFutureEvents(EventReceiver receiver, String channel) {
        for (FutureTick ft : this.getFutureTicks(receiver, Optional.of(channel))) {
            this.futures.remove(ft);
        }
    }

    /**
     * Returns how many turns must pass before this Event triggers
     */
    public int getFutureEventRemainingTurns(EventReceiver receiver, String channel) {
        List<FutureTick> ticks = this.getFutureTicks(receiver, Optional.of(channel));
        if (ticks.size() == 0) {
            return -1;
        }
        return ticks.get(0).turn - this.turn;
    }

    /**
     * Removes a FutureTick from the queue and processes it
     */
    private void processFutureTick(GameView view, FutureTick ft) {
        this.futures.remove(ft);
        Events.RepeatedEvent e = new Events.RepeatedEvent(ft.channel, ft.interval, ft.repeat);
        ft.receiver.handleEvent(view, e);
        if (e.repeat) {
            this.addFutureTick(ft.channel, ft.receiver, e.interval, true);
        }
    }

    /**
     * Triggers an Event for every FutureTick that has reached its time
     */
    private void checkFutureTicks(GameView view) {
        int a = 0;
        while (a < this.futures.size()) {
            FutureTick ft = this.futures.get(a);
            if (ft.turn == this.turn) {
                this.processFutureTick(view, ft);
            } else {
                a++;
            }
        }
    }

    /**
     * Pulls out all FutureTicks as described and processes them right now
     */
    public void handleFutureTicksEarly(GameView view, EventReceiver receiver, String channel) {
        List<FutureTick> ticks = this.getFutureTicks(receiver, Optional.of(channel));
        for (FutureTick ft : ticks) {
            this.processFutureTick(view, ft);
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
            this.turnPlayer = view.game.comps.get(0);
        } else {
            int index = view.game.comps.indexOf(this.turnPlayer);
            if (index == view.game.comps.size() - 1) {
                this.turnPlayer = view.game.human;
                this.turn++;
                this.checkFutureTicks(view);
                this.startNewTurnGroup(view);
            } else {
                this.turnPlayer = view.game.comps.get(index + 1);
            }
        }
        this.kickOffTurn(view);
    }

    /**
     * This function handles logic whenever every player has had a turn
     */
    private void startNewTurnGroup(GameView view) {
        view.game.mechanics.dayNight.tick();
        view.av.getToonShader().setNighttime(view.game.mechanics.dayNight.isNight());
        view.game.mechanics.patronage.recalculateFavor(view);
    }

    /**
     * Runs the per-turn logic and then allows the turn Player to act
     */
    public void kickOffTurn(GameView view) {
        // Run per-turn calculations for the turn Player
        this.canPlayerAct = false;
        // TODO make this entire function more readable
        // TODO run this logic in another thread for optimization
        for (Unit u : this.turnPlayer.units) {
            u.wakeUpCheck();
        }
        this.unitsThatHaveActed.clear();
        // TODO rename newUnits to recruitUnits so the syntax highlighter doesn't get
        // confused
        this.turnPlayer.unitPoints += view.game.mechanics.newUnits.getUnitPointsYield(this.turnPlayer);
        if (this.turnPlayer.isHumanPlayer()) {
            view.hud.minimap.refresh(view.game.world);
            view.logger.log("It is your turn again");

            // Check human Player win/lose state
            if (view.game.hasHumanPlayerLost()) {
                view.popups.add(new Menu(Mechanics.MENU_MARGIN, view.hud.getHeight(),
                        Coords.SIZE.x - (Mechanics.MENU_MARGIN * 2), false,
                        new ListNode().add(new TextNode(view.av, "You have lost"))
                                .add(new ButtonNode(view.av, "Okay", () -> view.close()))));
            }
            if (view.game.hasHumanPlayerWon()) {
                view.popups.add(new Menu(Mechanics.MENU_MARGIN, view.hud.getHeight(),
                        Coords.SIZE.x - (Mechanics.MENU_MARGIN * 2), false,
                        new ListNode().add(new TextNode(view.av, "You win!"))
                                .add(new ButtonNode(view.av, "Okay", () -> view.close()))));
            }

            // Choose a new Unit at the maximum unit points
            for (int a = 0; a < Math.floor(this.turnPlayer.unitPoints / NewUnit.MAX_UNIT_POINTS); a++) {
                view.popups.add(view.game.mechanics.newUnits.getNewUnitMenu(view));
            }
            // Start a new ArtifactAuction at the maximum auction points
            if (view.game.auctionPoints >= ArtifactAuction.MAX_AUCTION_POINTS
                    && !view.game.mechanics.auction.getAuction().isPresent()) {
                view.game.auctionPoints -= ArtifactAuction.MAX_AUCTION_POINTS;
                view.game.mechanics.auction.openNewAuction();
                view.popups.add(view.game.mechanics.auction.getAuctionBuyInMenu(view));
            }
            // Show the aftermath of any active ArtifactAuction
            if (view.game.mechanics.auction.getAuction().map((Auction a) -> a.hasBeenDecided(view.game))
                    .orElse(false)) {
                if (view.game.mechanics.auction.getAuction().get().notEveryoneHasSeenResults(view.game)) {
                    view.popups.add(view.game.mechanics.auction.getFollowUpMenu(view, true));
                    view.game.mechanics.auction.getAuction().get().hasSeenResults();
                } else {
                    view.game.mechanics.auction.closeAuction();
                }
            }
        } else {
            CompPlayer comp = (CompPlayer) this.turnPlayer;
            comp.stats.income.record(comp.gold);

            // Handle AI player ArtifactAuction logic
            if (view.game.mechanics.auction.getAuction().map((Auction a) -> a.hasBeenDecided(view.game))
                    .orElse(false)) {
                view.game.mechanics.auction.getAuction().get().hasSeenResults();
            } else if (view.game.mechanics.auction.getAuction().isPresent()) {
                // The CompPlayer decides whether or not it will enter the Auction
                comp.wishlist.decideOnAuctionEntry(view, comp);
            }

            // Handle CompPlayer Unit recruitment logic
            for (int a = 0; a < Math.floor(this.turnPlayer.unitPoints / NewUnit.MAX_UNIT_POINTS); a++) {
                // TODO the CompPlayer decides which Unit to recruit (if it has the unit points)
            }
        }

        // Allow the turn Player to act
        this.canPlayerAct = true;
        if (!this.turnPlayer.isHumanPlayer()) {
            CompPlayer player = (CompPlayer) this.turnPlayer;
            player.makeDecisions(view);
            view.menu.refresh(true);
            this.iterateTurnPlayer(view);
        }
    }

    /**
     * Represents an Event trigger that should happen during some future turn
     */
    public static class FutureTick {
        // TODO this doesn't rehydrate quite right, units load hungry from save file
        private final EventReceiver receiver;
        private final String channel;
        private final boolean repeat;
        private final int interval;
        private final int turn;

        private FutureTick(EventReceiver receiver, String channel, int turn, int interval, boolean repeat) {
            this.receiver = receiver;
            this.interval = interval;
            this.channel = channel;
            this.repeat = repeat;
            this.turn = turn;
        }

        /**
         * This should only be used in conjunction with Kryo rehydration
         */
        public FutureTick() {
            this.receiver = null;
            this.interval = 0;
            this.channel = null;
            this.repeat = false;
            this.turn = 0;
        }
    }
}
