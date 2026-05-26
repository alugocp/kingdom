package net.lugocorp.kingdom.ai.wishlist;
import net.lugocorp.kingdom.ai.action.Goal;
import net.lugocorp.kingdom.game.Game;
import net.lugocorp.kingdom.game.model.Artifact;
import net.lugocorp.kingdom.game.player.CompPlayer;
import net.lugocorp.kingdom.gameplay.mechanics.ArtifactAuction;
import net.lugocorp.kingdom.gameplay.mechanics.Auction;
import net.lugocorp.kingdom.math.Point;
import net.lugocorp.kingdom.ui.views.GameView;
import java.util.Optional;
import java.util.Set;

/**
 * Handles the logic to determine when a CompPlayer enters an auction and what
 * Artifacts they go for.
 */
public class ArtifactWishlist extends Wishlist<Artifact> {
    private final CompPlayer player;
    private final Game game;
    private Optional<Artifact> wanted = Optional.empty();

    public ArtifactWishlist(Game game, CompPlayer player) {
        super(player.getActor());
        this.player = player;
        this.game = game;
    }

    // This constructor is only used for Kryo
    public ArtifactWishlist() {
        super(null);
        this.player = null;
        this.game = null;
    }

    /**
     * Handles whether or not the CompPlayer will enter into an Auction
     */
    public void decideOnAuctionEntry() {
        this.setOptions(this.game.mechanics.auction.getArtifacts());
        this.wanted = this.getDesiredOptions().getMostWanted();

        // Check which Unit we should bid with
        if (this.wanted.isPresent()) {
            for (Point p : this.game.mechanics.auction.getBidderUnits(player)) {
                if (this.shouldBidWithVault(this.wanted.get(), p)) {
                    this.game.mechanics.auction.getAuction().ifPresent((Auction a) -> a.addBidder(player, p));
                    return;
                }
            }
        }

        // Don't add a bid if we can't find a good enough vault
        this.game.mechanics.auction.getAuction().ifPresent((Auction a) -> a.doNotAddBidder());
    }

    /**
     * Handles logic for the CompPlayer to select an Artifact
     */
    public void doAfterAuction(GameView view) {
        // TODO make this more consistent with the way HumanPlayers do it (move to the
        // Auction code) and then replace the GameView field in this class with just
        // Game
        this.player.auctionChips++;
        while (this.wanted.map((Artifact a) -> this.player.auctionChips >= a.chips).orElse(false)) {
            final Artifact a = this.wanted.get();
            this.player.auctionChips -= a.chips;
            a.claim(view, this.player);
        }
    }

    /**
     * Determines whether or not this CompPlayer should bid in an Auction using a
     * vault at the given Point
     */
    private boolean shouldBidWithVault(Artifact artifact, Point p) {
        final ArtifactAuction auction = this.game.mechanics.auction;

        // Decide if the buy-in price is too high
        final int cost = auction.getBuyInCost(player.gold);
        final int turnsToRecover = cost / Math.max(this.player.stats.income.getAverage(), 1);
        final int desire = this.getDesireForOption(artifact);
        final int chipsMissing = artifact.chips - this.player.auctionChips;
        if (cost * chipsMissing > turnsToRecover * desire) {
            return false;
        }

        // Decide if the vault at this Point is a good bet
        final int value = auction.getBidValueAtPoint(this.game.world, p);
        return value > cost;
    }

    /** {@inheritdoc} */
    @Override
    protected int getScoreForGoal(Artifact option, Goal g) {
        final Set<String> channels = this.game.events.artifact.getChannels(option.getStratifier());
        int score = 0;
        for (String c : channels) {
            score += g.likesEventChannel(c) ? 1 : 0;
        }
        return score;
    }
}
