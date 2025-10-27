package net.lugocorp.kingdom.game.mechanics;
import net.lugocorp.kingdom.game.Game;
import net.lugocorp.kingdom.game.model.Building;
import net.lugocorp.kingdom.game.model.Tile;
import net.lugocorp.kingdom.game.player.Player;
import net.lugocorp.kingdom.game.properties.Inventory;
import net.lugocorp.kingdom.game.world.World;
import net.lugocorp.kingdom.math.Point;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

/**
 * This class models a single Artifact Auction's data
 */
public class Auction {
    private final Map<Player, Point> bids = new HashMap<>();
    private final int numberOfPlayers;
    public int aftermaths = 0;
    public int decisions = 0;

    public Auction(Game g) {
        this.numberOfPlayers = g.getAllPlayers().size();
    }

    /**
     * Returns true if every Player has made a decision on this Auction
     */
    public boolean hasBeenDecided(Game g) {
        return this.decisions == this.numberOfPlayers;
    }

    /**
     * Returns true if there are still Players who have not seen the results of this
     * Auction
     */
    public boolean notEveryoneHasSeenResults(Game g) {
        return this.aftermaths < this.numberOfPlayers;
    }

    /**
     * Called when a Player decides not to bid
     */
    public void doNotAddBidder() {
        this.decisions++;
    }

    /**
     * Adds a new bid to this Auction
     */
    public void addBidder(Player bidder, Point p) {
        this.bids.put(bidder, p);
        this.decisions++;
    }

    /**
     * Called when a Player has seen the Auction results
     */
    public void hasSeenResults() {
        this.aftermaths++;
    }

    /**
     * Returns the total worth of all the Items in the bidder's auctioned Building
     * (if any)
     */
    private int getBidValue(World world, Player bidder) {
        return world.getTile(this.bids.get(bidder)).flatMap((Tile t) -> t.building).flatMap((Building b) -> b.items)
                .map((Inventory i) -> i.getTotalGold()).orElse(0);
    }

    /**
     * Retrieves the winner of this Auction
     */
    Optional<Player> getWinner(World world, Random random) {
        if (this.bids.size() == 0) {
            return Optional.empty();
        }
        final Set<Player> winners = new HashSet<>();
        int maxBid = 0;
        for (Player bidder : this.bids.keySet()) {
            int bid = this.getBidValue(world, bidder);
            if (bid > maxBid) {
                maxBid = bid;
                winners.clear();
                winners.add(bidder);
            } else if (bid == maxBid) {
                winners.add(bidder);
            }
        }
        final Player[] w = new Player[winners.size()];
        winners.toArray(w);
        return Optional.of(w[random.nextInt(w.length)]);
    }
}
