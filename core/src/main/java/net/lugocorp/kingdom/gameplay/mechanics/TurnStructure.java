package net.lugocorp.kingdom.gameplay.mechanics;
import net.lugocorp.kingdom.builtin.Events;
import net.lugocorp.kingdom.game.glyph.Glyph;
import net.lugocorp.kingdom.game.model.Building;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.game.player.CompPlayer;
import net.lugocorp.kingdom.game.player.Player;
import net.lugocorp.kingdom.math.Coords;
import net.lugocorp.kingdom.math.Point;
import net.lugocorp.kingdom.menu.Menu;
import net.lugocorp.kingdom.menu.structure.ListNode;
import net.lugocorp.kingdom.menu.structure.SpacerNode;
import net.lugocorp.kingdom.menu.text.ButtonNode;
import net.lugocorp.kingdom.menu.text.HeaderNode;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.Log;
import java.util.Optional;
import java.util.List;

/**
 * This class handles human and AI Players taking turns during the Game
 */
public class TurnStructure {
    private final Turn turn = new Turn();

    /**
     * Returns the current Turn
     */
    public Turn getTurn() {
        return this.turn;
    }

    /**
     * Returns true when the turn Player is the human
     */
    public boolean canHumanPlayerAct() {
        return this.turn.getPlayer().isHumanPlayer() && this.turn.getState() == TurnState.ACTIVE;
    }

    /**
     * Iterates the Turn Player and kicks off a new Turn
     */
    public void nextTurn(GameView view) {
        final Player prev = this.turn.getPlayer();
        prev.getFate().handleEvent(view, new Events.EndOfTurnEvent()).execute();
        if (prev.isHumanPlayer()) {
            view.hud.bot.turnButton.update(false, true);
        }
        this.turn.next();
        view.game.actions.turnTransition(prev, this.turn.getPlayer());
        this.startOfTurn(view);
    }

    /**
     * Kicks off a new Turn
     */
    public void startOfTurn(GameView view) {
        if (this.turn.isFirstTurnPlayer()) {
            this.startOfTurnGroup(view);
        }
        view.hud.top.update(view.game);
        for (Unit u : this.turn.getPlayer().units) {
            u.sleep.wakeUpCheck(view);
        }
        view.game.mechanics.recruitUnits.giveUnitPointsYield(view, this.turn.getPlayer());
        if (this.turn.getPlayer().isHumanPlayer()) {
            view.hud.bot.minimap.refresh(view.game.world);
            view.hud.logger.log("It is your turn again");

            // Check human Player win/lose state
            if (view.game.hasHumanPlayerLost()) {
                view.hud.popups.add(new Menu(Mechanics.MENU_MARGIN, view.hud.top.getHeight(),
                        Coords.SIZE.x - (Mechanics.MENU_MARGIN * 2), false,
                        new ListNode().add(new HeaderNode(view.av, "You have lost").center()).add(new SpacerNode())
                                .add(new ButtonNode(view.av, "Back to main menu", () -> view.close()))));
            }
            if (view.game.hasHumanPlayerWon()) {
                view.hud.popups.add(new Menu(Mechanics.MENU_MARGIN, view.hud.top.getHeight(),
                        Coords.SIZE.x - (Mechanics.MENU_MARGIN * 2), false,
                        new ListNode().add(new HeaderNode(view.av, "You win!").center()).add(new SpacerNode())
                                .add(new ButtonNode(view.av, "Back to main menu", () -> view.close()))));
            }

            // Choose a new Unit at the maximum unit points
            if (this.turn.getPlayer().getUnitPoints() >= NewUnit.MAX_UNIT_POINTS) {
                view.hud.popups.add(view.game.mechanics.recruitUnits.getNewUnitMenu(view));
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
            final CompPlayer comp = (CompPlayer) this.turn.getPlayer();
            comp.stats.unitPoints.add(comp.getUnitPoints());
            view.hud.logger.log(String.format("%s's turn", comp.name));
            Log.log("%s's unit points: %d", comp.name, comp.getUnitPoints());

            // Handle AI player ArtifactAuction logic
            if (view.game.mechanics.auction.getAuction().map((Auction a) -> a.hasBeenDecided(view.game))
                    .orElse(false)) {
                view.game.mechanics.auction.getAuction().get().hasSeenResults();
            } else if (view.game.mechanics.auction.getAuction().isPresent()) {
                // The CompPlayer decides whether or not it will enter the Auction
                comp.wishlist.artifacts.decideOnAuctionEntry();
            }

            // Handle CompPlayer Unit recruitment logic
            if (comp.getUnitPoints() >= NewUnit.MAX_UNIT_POINTS) {
                Optional<Glyph> glyph = comp.wishlist.glyphs.getDesiredOptions().getMostWanted();
                Log.log("%s most wanted glyph: %s", comp.name, glyph);
                if (glyph.isPresent()) {
                    Optional<Point> spawnPoint = comp.wishlist.units.getSpawnPoint(view, comp, glyph.get());
                    if (spawnPoint.isPresent()) {
                        Log.log("Will spawn unit at %s", spawnPoint.get());
                        final List<Unit> options = view.game.mechanics.recruitUnits.getRecruitmentOptions(view, glyph.get(), spawnPoint.get(), 1);
                        if (options.size() > 0) {
                            comp.wishlist.units.setOptions(options);
                            final Optional<Unit> unit = comp.wishlist.units.getDesiredOptions().getMostWanted();
                            unit.ifPresent((Unit u) -> view.game.mechanics.recruitUnits.choose(view, comp, u));
                            Log.log("%s chose to recruit %s", comp.name, unit.get().name);
                        } else {
                            Log.log("%s glyph pool was empty", glyph.get());
                        }
                    } else {
                        Log.log("No valid spawn point found");
                    }
                }
            }
        }
    }

    /**
     * This function handles logic whenever every player has had a turn
     */
    private void startOfTurnGroup(GameView view) {
        // Very first turn setup
        if (this.turn.getCounter() == 1) {
            for (Player p : view.game.getAllPlayers()) {
                p.getFate().handleEvent(view, new Events.GameStartEvent(p)).execute();
            }
        }

        // Iterate day/night logic
        DayNight dayNight = view.game.mechanics.dayNight;
        boolean isDayBeforeTick = dayNight.isDay();
        dayNight.tick(view);
        boolean isDayAfterTick = dayNight.isDay();
        view.av.shaders.toon.setNighttime(dayNight.isNight());
        view.av.shaders.tile.setNighttime(dayNight.isNight());

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
     * Performs some action each frame to keep the TurnStructure flowing
     */
    public void processTurnByFrame(GameView view) {
        if (this.turn.getState() == TurnState.TRANSITION) {
            if (view.game.future.checkFutureTicks(view) && !view.animations.inProgress()) {
                this.turn.activate();
                view.hud.bot.tileMenu.refresh();
                if (this.turn.getPlayer().isHumanPlayer()) {
                    view.hud.bot.turnButton.update(true, false);
                }
            }
        } else if (!this.turn.getPlayer().isHumanPlayer()) {
            // TODO decision making should last multiple frames
            final CompPlayer player = (CompPlayer) this.turn.getPlayer();
            player.makeDecisions(view);
            view.hud.bot.tileMenu.refresh();
            this.nextTurn(view);
            player.stats.commit();
        }
    }
}
