package net.lugocorp.kingdom.ai;
import net.lugocorp.kingdom.game.mechanics.ArtifactAuction;
import net.lugocorp.kingdom.game.model.Artifact;
import net.lugocorp.kingdom.game.model.Building;
import net.lugocorp.kingdom.game.model.Tile;
import net.lugocorp.kingdom.game.model.fields.Inventory;
import net.lugocorp.kingdom.game.player.CompPlayer;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.code.Lambda;
import net.lugocorp.kingdom.utils.math.Point;
import java.util.Optional;

/**
 * Handles the logic to determine when a CompPlayer enters an auction and what
 * Artifacts they go for.
 */
public class ArtifactWishlist {
    private Optional<Artifact> desired = Optional.empty();

    /**
     * Returns how much the CompPlayer actually wants the given Artifact
     */
    private int getArtifactDesire(CompPlayer player, Artifact a) {
        return player.getFate().evaluateArtifact(a);
    }

    /**
     * Determines whether or not this CompPlayer should bid in an Auction
     */
    private boolean shouldBidOnAuction(GameView view, CompPlayer player, Point p) {
        final ArtifactAuction auction = view.game.mechanics.auction;

        // Decide if the buy-in price is too high
        final int cost = auction.getBuyInCost(player.gold);
        final int turnsToRecover = cost / player.stats.income.getAverage();
        final int desire = this.desired.map((Artifact a) -> this.getArtifactDesire(player, a)).orElse(0);
        final int chipsMissing = this.desired.map((Artifact a) -> a.chips - player.auctionChips).orElse(0);
        if (cost * chipsMissing > turnsToRecover * desire) {
            return false;
        }

        // Decide if the vault at this Point is a good bet
        int value = view.game.world.getTile(p).flatMap((Tile t) -> t.building).flatMap((Building b) -> b.items)
                .map((Inventory i) -> i.getTotalGold()).orElse(0);
        return value > cost;
    }

    /**
     * Handles whether or not the CompPlayer will enter into an Auction
     */
    public void decideOnAuctionEntry(GameView view, CompPlayer player) {
        Artifact desired = Lambda
                .sort((Artifact a) -> this.getArtifactDesire(player, a), view.game.mechanics.auction.getArtifacts())
                .get(0);
        final int desire = this.getArtifactDesire(player, desired);
        this.desired = Optional.of(desired);
        for (Point p : view.game.getVaultBuildings(player)) {
            if (this.shouldBidOnAuction(view, player, p)) {
                view.game.mechanics.auction.getAuction()
                        .ifPresent((ArtifactAuction.Auction a) -> a.addBidder(player, p));
                return;
            }
        }
        view.game.mechanics.auction.getAuction().ifPresent((ArtifactAuction.Auction a) -> a.doNotAddBidder());
    }

    /**
     * Handles logic for the CompPlayer to select an Artifact
     */
    public void doAfterAuction(GameView view, CompPlayer player) {
        player.auctionChips++;
        while (this.desired.map((Artifact a) -> player.auctionChips >= a.chips).orElse(false)) {
            Artifact artifact = this.desired.get();
            player.auctionChips -= artifact.chips;
            artifact.claim(view, player);
            view.game.mechanics.auction.removeArtifact(artifact);
        }
    }
}
