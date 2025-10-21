package net.lugocorp.kingdom.game.mechanics;
import net.lugocorp.kingdom.builtin.Events;
import net.lugocorp.kingdom.game.Game;
import net.lugocorp.kingdom.game.glyph.Glyph;
import net.lugocorp.kingdom.game.model.Building;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.game.player.CompPlayer;
import net.lugocorp.kingdom.game.player.Player;
import net.lugocorp.kingdom.menu.Menu;
import net.lugocorp.kingdom.menu.structure.ListNode;
import net.lugocorp.kingdom.menu.text.ButtonNode;
import net.lugocorp.kingdom.menu.text.TextNode;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.math.Coords;
import net.lugocorp.kingdom.utils.math.Point;
import java.util.Optional;

/**
 * This class handles human and AI Players taking turns during the Game
 */
public class TurnStructure {
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
     * Returns the current turn number
     */
    public int getTurn() {
        return this.turn;
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
        this.turnPlayer.getFate().handleEvent(view, new Events.EndOfTurnEvent()).execute();
        view.game.actions.endOfTurn(this.turnPlayer);
        if (this.turnPlayer.isHumanPlayer()) {
            this.turnPlayer = view.game.comps.get(0);
            view.hud.bot.turnButton.update(false);
        } else {
            int index = view.game.comps.indexOf(this.turnPlayer);
            if (index == view.game.comps.size() - 1) {
                this.turnPlayer = view.game.human;
                this.turn++;
                view.game.future.checkFutureTicks(view);
                this.startNewTurnGroup(view);
                view.hud.bot.turnButton.update(true);
                view.hud.top.update(view.game);
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
        // Iterate day/night logic
        DayNight dayNight = view.game.mechanics.dayNight;
        boolean isDayBeforeTick = dayNight.isDay();
        dayNight.tick(view);
        boolean isDayAfterTick = dayNight.isDay();
        view.av.getToonShader().setNighttime(dayNight.isNight());

        // Recalculate favorite Players for Patrons
        view.game.mechanics.patronage.recalculateFavor(view);

        // Update Unit vision at dawn and dusk
        if (isDayBeforeTick != isDayAfterTick) {
            for (Player p : view.game.getAllPlayers()) {
                // TODO can we optimize this?
                for (Unit u : p.units) {
                    u.vision.set(view, p, u, u.getPoint());
                }
                for (Building b : p.buildings) {
                    b.vision.set(view, p, b, b.getPoint());
                }
            }
        }
    }

    /**
     * Perform these actions when the Game starts
     */
    private void initializeGame(GameView view) {
        for (Player p : view.game.getAllPlayers()) {
            p.getFate().handleEvent(view, new Events.GameStartEvent(p)).execute();
        }
    }

    /**
     * Runs the per-turn logic and then allows the turn Player to act
     */
    public void kickOffTurn(GameView view) {
        if (this.turn == 1 && this.turnPlayer.isHumanPlayer()) {
            this.initializeGame(view);
        }

        // Run per-turn calculations for the turn Player
        this.canPlayerAct = false;
        // TODO make this entire function more readable
        // TODO run this logic in another thread for optimization
        for (Unit u : this.turnPlayer.units) {
            u.sleep.wakeUpCheck(view);
        }
        // TODO rename newUnits to recruitUnits so the syntax highlighter doesn't get
        // confused
        view.game.mechanics.newUnits.giveUnitPointsYield(view, this.turnPlayer);
        if (this.turnPlayer.isHumanPlayer()) {
            view.hud.bot.minimap.refresh(view.game.world);
            view.hud.logger.log("It is your turn again");

            // Check human Player win/lose state
            if (view.game.hasHumanPlayerLost()) {
                view.hud.popups.add(new Menu(Mechanics.MENU_MARGIN, view.hud.top.getHeight(),
                        Coords.SIZE.x - (Mechanics.MENU_MARGIN * 2), false,
                        new ListNode().add(new TextNode(view.av, "You have lost"))
                                .add(new ButtonNode(view.av, "Okay", () -> view.close()))));
            }
            if (view.game.hasHumanPlayerWon()) {
                view.hud.popups.add(new Menu(Mechanics.MENU_MARGIN, view.hud.top.getHeight(),
                        Coords.SIZE.x - (Mechanics.MENU_MARGIN * 2), false,
                        new ListNode().add(new TextNode(view.av, "You win!"))
                                .add(new ButtonNode(view.av, "Okay", () -> view.close()))));
            }

            // Choose a new Unit at the maximum unit points
            for (int a = 0; a < Math.floor(this.turnPlayer.getUnitPoints() / NewUnit.MAX_UNIT_POINTS); a++) {
                view.hud.popups.add(view.game.mechanics.newUnits.getNewUnitMenu(view));
            }
            // Start a new ArtifactAuction at the maximum auction points
            if (view.game.mechanics.auction.readyForNewAuction()) {
                view.game.mechanics.auction.openNewAuction(view.game,
                        () -> view.hud.popups.add(view.game.mechanics.auction.getAuctionBuyInMenu(view)));
            }
            // Show the aftermath of any active ArtifactAuction
            if (view.game.mechanics.auction.getAuction().map((Auction a) -> a.hasBeenDecided(view.game))
                    .orElse(false)) {
                if (view.game.mechanics.auction.getAuction().get().notEveryoneHasSeenResults(view.game)) {
                    view.hud.popups.add(view.game.mechanics.auction.getFollowUpMenu(view, true));
                    view.game.mechanics.auction.getAuction().get().hasSeenResults();
                } else {
                    view.game.mechanics.auction.closeAuction();
                }
            }
        } else {
            CompPlayer comp = (CompPlayer) this.turnPlayer;
            comp.stats.unitPoints.add(comp.getUnitPoints());

            // Handle AI player ArtifactAuction logic
            if (view.game.mechanics.auction.getAuction().map((Auction a) -> a.hasBeenDecided(view.game))
                    .orElse(false)) {
                view.game.mechanics.auction.getAuction().get().hasSeenResults();
            } else if (view.game.mechanics.auction.getAuction().isPresent()) {
                // The CompPlayer decides whether or not it will enter the Auction
                comp.wishlist.artifacts.decideOnAuctionEntry();
            }

            // Handle CompPlayer Unit recruitment logic
            for (int a = 0; a < Math.floor(comp.getUnitPoints() / NewUnit.MAX_UNIT_POINTS); a++) {
                Optional<Glyph> glyph = comp.wishlist.glyphs.getDesiredOptions().getMostWanted();
                if (glyph.isPresent()) {
                    Optional<Point> spawnPoint = comp.wishlist.units.getSpawnPoint(comp, glyph.get());
                    if (spawnPoint.isPresent()) {
                        comp.wishlist.units.setOptions(view.game.mechanics.newUnits.getRecruitmentOptions(view,
                                glyph.get(), spawnPoint.get(), 1));
                        comp.wishlist.units.getDesiredOptions().getMostWanted()
                                .ifPresent((Unit u) -> view.game.mechanics.newUnits.choose(view, comp, u));
                    }
                }
            }
        }

        // Allow the turn Player to act
        this.canPlayerAct = true;
        if (!this.turnPlayer.isHumanPlayer()) {
            CompPlayer player = (CompPlayer) this.turnPlayer;
            player.makeDecisions(view);
            view.hud.bot.tileMenu.refresh();
            this.iterateTurnPlayer(view);
            player.stats.commit();
        }
    }
}
