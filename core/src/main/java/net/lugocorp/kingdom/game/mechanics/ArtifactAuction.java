package net.lugocorp.kingdom.game.mechanics;
import net.lugocorp.kingdom.game.Game;
import net.lugocorp.kingdom.game.model.Artifact;
import net.lugocorp.kingdom.game.model.Building;
import net.lugocorp.kingdom.game.model.Player;
import net.lugocorp.kingdom.game.model.Tile;
import net.lugocorp.kingdom.ui.Hud;
import net.lugocorp.kingdom.ui.menu.ArtifactNode;
import net.lugocorp.kingdom.ui.menu.ButtonNode;
import net.lugocorp.kingdom.ui.menu.HeaderNode;
import net.lugocorp.kingdom.ui.menu.ListNode;
import net.lugocorp.kingdom.ui.menu.Menu;
import net.lugocorp.kingdom.ui.menu.RowNode;
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
import java.util.Random;
import java.util.Set;

/**
 * This class manages the logic for artifact auctions
 */
public class ArtifactAuction {
    public static final int MAX_AUCTION_POINTS = 250;
    public static final int AUCTION_STATE_INACTIVE = 0;
    public static final int AUCTION_STATE_ACTIVE = 1;
    public static final int AUCTION_STATE_DONE = 2;
    private final Random random = new Random();
    private Optional<Auction> auction = Optional.empty();
    private List<Artifact> artifacts = new ArrayList<>();

    /**
     * Unlocks a couple initial Artifacts for the Auction system
     */
    public void init(Game g) {
        // TODO repeat this 3 times or so
        this.artifacts.add(g.content.artifacts.retrieve());
    }

    /**
     * Calculates how much gold a Player must pay to participate in an auction
     */
    public int getBuyInCost(int gold) {
        return (int) Math.floor(gold * 0.2);
    }

    /**
     * Retrieves the currently open Auction (if any)
     */
    public Optional<Auction> getAuction() {
        return this.auction;
    }

    /**
     * Closes the currently open Auction
     */
    public void closeAuction() {
        this.auction = Optional.empty();
    }

    /**
     * Opens a new Auction
     */
    public void openNewAuction() {
        if (this.artifacts.size() > 0) {
            this.auction = Optional.of(new Auction());
        }
    }

    /**
     * Returns every Point where the human Player owns a vault-like structure
     */
    private Set<Point> getAuctionVaults(GameView view) {
        // TODO for the love of god please optimize this
        Set<Point> vaults = new HashSet<>();
        for (int x = 0; x < view.game.world.getWidth(); x++) {
            for (int y = 0; y < view.game.world.getHeight(); y++) {
                Tile t = view.game.world.getTile(x, y).get();
                if (t.leader.map((Player p) -> p == view.game.human).orElse(false)
                        && t.building.flatMap((Building b) -> b.items).isPresent()) {
                    vaults.add(new Point(x, y));
                }
            }
        }
        return vaults;
    }

    /**
     * Instantiates the Menu that allows a human Player to participate in an auction
     */
    public Menu getAuctionBuyInMenu(GameView view) {
        int price = this.getBuyInCost(view.game.human.gold);
        ListNode node = new ListNode()
                .add(new ButtonNode(view.game.graphics, "x", () -> view.popups.setDisplay(false)));
        if (this.artifacts.size() > 0) {
            node.add(new HeaderNode(view.game.graphics, "Artifact Auction"))
                    .add(new TextNode(view.game.graphics,
                            String.format("Pay %d gold to participate in the auction?", price)))
                    .add(new RowNode().add(new ButtonNode(view.game.graphics, "Yes", () -> {
                        String error = "You have no vaults with items to bargain with";
                        Set<Point> vaults = this.getAuctionVaults(view);
                        if (vaults.size() == 0) {
                            this.auction.get().doNotAddBidder();
                            view.logger.log(error);
                            view.popups.complete();
                            return;
                        }
                        view.popups.setDisplay(false);
                        view.logger.log("Please select a vault with items to bid");
                        view.selectTiles(vaults, error, (Point p) -> {
                            this.auction.get().addBidder(view.game.human, p);
                            view.game.human.gold -= price;
                            view.popups.complete();
                        });
                    })).add(new ButtonNode(view.game.graphics, "No", () -> {
                        this.auction.get().doNotAddBidder();
                        view.popups.complete();
                    })));
        } else {
            node.add(new TextNode(view.game.graphics, "There are no artifacts left to auction"))
                    .add(new ButtonNode(view.game.graphics, "Okay", () -> {
                        view.popups.complete();
                    }));
        }
        return new Menu(Hud.BUTTON_WIDTH, Hud.HEIGHT, Gdx.graphics.getWidth() - (Hud.BUTTON_WIDTH * 2), false, node);
    }

    /**
     * Instantiates the Menu that will appear after an Auction has concluded
     */
    public Menu getFollowUpMenu(GameView view) {
        // Inform the human Player if they did not win the Auction
        Optional<Player> winner = this.auction.get().getWinner(this.random);
        if (winner.map((Player p) -> !p.isHumanPlayer()).orElse(true)) {
            return new Menu(Hud.BUTTON_WIDTH, Hud.HEIGHT, Gdx.graphics.getWidth() - (Hud.BUTTON_WIDTH * 2), false,
                    new ListNode().add(new ButtonNode(view.game.graphics, "x", () -> view.popups.setDisplay(false)))
                            .add(new TextNode(view.game.graphics,
                                    winner.isPresent() ? "You did not win the auction" : "Nobody bid in this auction"))
                            .add(new ButtonNode(view.game.graphics, "Okay", () -> view.popups.complete())));
        }

        // Populate the Menu with ArtifactNodes if the human Player did win the Auction
        int a = 0;
        int width = Gdx.graphics.getWidth() - (Hud.BUTTON_WIDTH * 2);
        int columns = (int) Math.floor(width / ArtifactNode.WIDTH);
        ListNode node = new ListNode()
                .add(new ButtonNode(view.game.graphics, "x", () -> view.popups.setDisplay(false)));
        while (a < artifacts.size()) {
            RowNode row1 = new RowNode();
            RowNode row2 = new RowNode();
            for (int b = 0; b < columns && a < this.artifacts.size();) {
                final Artifact artifact = this.artifacts.get(a);
                if (!artifact.shouldDisplay()) {
                    continue;
                }
                row1.add(new ArtifactNode(view.game.graphics, artifact));
                row2.add(new ButtonNode(view.game.graphics, "Choose", () -> {
                    artifact.claim(view, view.game.human);
                    this.artifacts.remove(artifact);
                    view.popups.complete();
                }));
                a++;
                b++;
            }
            node.add(row1);
            node.add(row2);
        }
        return new Menu(Hud.BUTTON_WIDTH, Hud.HEIGHT, width, false, node);
    }

    /**
     * Nested class that contains a single artifact auction's data
     */
    public static class Auction {
        private final Map<Player, Point> bids = new HashMap<>();
        public int aftermaths = 0;
        public int decisions = 0;

        /**
         * Returns true if every Player has made a decision on this Auction
         */
        public boolean hasBeenDecided(Game g) {
            // The +1 is for the human Player
            return this.decisions == g.comps.size() + 1;
        }

        /**
         * Returns true if there are still Players who have not seen the results of this
         * Auction
         */
        public boolean notEveryoneHasSeenResults(Game g) {
            // The +1 is for the human Player
            return this.aftermaths < g.comps.size() + 1;
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
         * Retrieves the winner of this Auction
         */
        private Optional<Player> getWinner(Random random) {
            if (this.bids.size() == 0) {
                return Optional.empty();
            }
            Set<Player> winners = new HashSet<>();
            int maxBid = 0;
            for (Player bidder : this.bids.keySet()) {
                int bid = 0; // getBidValue(this.bids.get(bidder));
                if (bid > maxBid) {
                    maxBid = bid;
                    winners.clear();
                    winners.add(bidder);
                } else if (bid == maxBid) {
                    winners.add(bidder);
                }
            }
            Player[] w = new Player[winners.size()];
            winners.toArray(w);
            return Optional.of(w[random.nextInt(w.length)]);
        }
    }
}
