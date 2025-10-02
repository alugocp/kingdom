package net.lugocorp.kingdom.ai.wishlist;
import net.lugocorp.kingdom.ai.action.Goal;
import net.lugocorp.kingdom.game.mechanics.ArtifactAuction;
import net.lugocorp.kingdom.game.model.Artifact;
import net.lugocorp.kingdom.game.model.Building;
import net.lugocorp.kingdom.game.model.Tile;
import net.lugocorp.kingdom.game.player.CompPlayer;
import net.lugocorp.kingdom.game.properties.Inventory;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.math.Point;
import java.util.Optional;
import java.util.Set;

/**
 * Handles the logic to determine when a CompPlayer enters an auction and what
 * Artifacts they go for.
 */
public class ArtifactWishlist extends Wishlist<Artifact> {
    private final CompPlayer player;
    private final GameView view;
    private Optional<Artifact> wanted = Optional.empty();

    public ArtifactWishlist(GameView view, CompPlayer player) {
        super(player.getActor());
        this.player = player;
        this.view = view;
    }

    /**
     * Handles whether or not the CompPlayer will enter into an Auction
     */
    public void decideOnAuctionEntry(GameView view, CompPlayer player) {
        this.setOptions(view.game.mechanics.auction.getArtifacts());
        this.wanted = this.getDesiredOptions().getMostWanted();

        // Check which vault Building we should bid with
        if (this.wanted.isPresent()) {
            for (Point p : view.game.getVaultBuildings(player)) {
                if (this.shouldBidWithVault(view, player, this.wanted.get(), p)) {
                    view.game.mechanics.auction.getAuction()
                            .ifPresent((ArtifactAuction.Auction a) -> a.addBidder(player, p));
                    return;
                }
            }
        }

        // Don't add a bid if we can't find a good enough vault
        view.game.mechanics.auction.getAuction().ifPresent((ArtifactAuction.Auction a) -> a.doNotAddBidder());
    }

    /**
     * Handles logic for the CompPlayer to select an Artifact
     */
    public void doAfterAuction(GameView view, CompPlayer player) {
        player.auctionChips++;
        while (this.wanted.map((Artifact a) -> player.auctionChips >= a.chips).orElse(false)) {
            final Artifact a = this.wanted.get();
            player.auctionChips -= a.chips;
            a.claim(view, player);
            view.game.mechanics.auction.removeArtifact(a);
        }
    }

    /**
     * Determines whether or not this CompPlayer should bid in an Auction using a
     * vault at the given Point
     */
    private boolean shouldBidWithVault(GameView view, CompPlayer player, Artifact artifact, Point p) {
        final ArtifactAuction auction = view.game.mechanics.auction;

        // Decide if the buy-in price is too high
        final int cost = auction.getBuyInCost(player.gold);
        final int turnsToRecover = cost / player.stats.income.getAverage();
        final int desire = this.getDesireForOption(artifact);
        final int chipsMissing = artifact.chips - player.auctionChips;
        if (cost * chipsMissing > turnsToRecover * desire) {
            return false;
        }

        // Decide if the vault at this Point is a good bet
        int value = view.game.world.getTile(p).flatMap((Tile t) -> t.building).flatMap((Building b) -> b.items)
                .map((Inventory i) -> i.getTotalGold()).orElse(0);
        return value > cost;
    }

    /** {@inheritdoc} */
    @Override
    protected int getScoreForGoal(Artifact option, Goal g) {
        final Set<String> channels = view.game.events.artifact.getChannels(option.getStratifier());
        int score = 0;
        for (String c : channels) {
            score += g.likesEventChannel(c) ? 1 : 0;
        }
        return score;
    }
}
