package net.lugocorp.kingdom.ai;
import net.lugocorp.kingdom.game.mechanics.ArtifactAuction;
import net.lugocorp.kingdom.game.model.Artifact;
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
     * Handles whether or not the CompPlayer will enter into an Auction
     */
    public void decideOnAuctionEntry(GameView view, CompPlayer player) {
        Artifact desired = Lambda
                .sort((Artifact a) -> this.getArtifactDesire(player, a), view.game.mechanics.auction.getArtifacts())
                .get(0);
        final int desire = this.getArtifactDesire(player, desired);
        final int cost = view.game.mechanics.auction.getBuyInCost(player.gold);
        this.desired = Optional.of(desired);
        for (Point p : view.game.getVaultBuildings(player)) {
            // TODO implement me, I'm just random right now
            if (Math.random() < 0.3) {
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
