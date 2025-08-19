package net.lugocorp.kingdom.game.mechanics;
import net.lugocorp.kingdom.game.Game;
import net.lugocorp.kingdom.game.model.Artifact;
import net.lugocorp.kingdom.game.model.Building;
import net.lugocorp.kingdom.game.model.Tile;
import net.lugocorp.kingdom.game.model.fields.Inventory;
import net.lugocorp.kingdom.game.player.CompPlayer;
import net.lugocorp.kingdom.game.player.Player;
import net.lugocorp.kingdom.game.world.World;
import net.lugocorp.kingdom.ui.menu.ArtifactNode;
import net.lugocorp.kingdom.ui.menu.ButtonNode;
import net.lugocorp.kingdom.ui.menu.HeaderNode;
import net.lugocorp.kingdom.ui.menu.ListNode;
import net.lugocorp.kingdom.ui.menu.Menu;
import net.lugocorp.kingdom.ui.menu.RowNode;
import net.lugocorp.kingdom.ui.menu.TextNode;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.math.Coords;
import net.lugocorp.kingdom.utils.math.Point;
import com.esotericsoftware.kryo.serializers.FieldSerializer;
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
    @FieldSerializer.Optional("random")
    private final Random random = new Random();
    private Optional<Auction> auction = Optional.empty();
    private List<Artifact> artifacts = new ArrayList<>();

    /**
     * Generates all registered Artifacts at the start of the Game
     */
    public void init(Game g) {
        Set<String> stratifiers = g.events.artifact.getStratifiers();
        for (String name : stratifiers) {
            this.artifacts.add(g.generator.artifact(name));
        }
    }

    /**
     * Returns all available Artifacts
     */
    public List<Artifact> getArtifacts() {
        return this.artifacts;
    }

    /**
     * Removes an Artifact from the List
     */
    public void removeArtifact(Artifact artifact) {
        this.artifacts.remove(artifact);
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
     * Instantiates the Menu that allows a human Player to participate in an auction
     */
    public Menu getAuctionBuyInMenu(GameView view) {
        int price = this.getBuyInCost(view.game.human.gold);
        ListNode node = new ListNode().add(new ButtonNode(view.av, "x", () -> view.popups.setDisplay(false)));
        if (this.artifacts.size() > 0) {
            node.add(new HeaderNode(view.av, "Artifact Auction"))
                    .add(new TextNode(view.av, String.format("Pay %d gold to participate in the auction?", price)))
                    .add(new RowNode().add(new ButtonNode(view.av, "Yes", () -> {
                        String error = "You have no vaults with items to bargain with";
                        Set<Point> vaults = view.game.getVaultBuildings(view.game.human);
                        if (vaults.size() == 0) {
                            this.auction.get().doNotAddBidder();
                            view.logger.log(error);
                            view.popups.complete();
                            return;
                        }
                        view.popups.setDisplay(false);
                        view.logger.log("Please select a vault with items to bid");
                        view.selector.select(vaults, error, (Point p) -> {
                            this.auction.get().addBidder(view.game.human, p);
                            view.game.human.gold -= price;
                            view.popups.complete();
                        });
                    })).add(new ButtonNode(view.av, "No", () -> {
                        this.auction.get().doNotAddBidder();
                        view.popups.complete();
                    })));
        } else {
            node.add(new TextNode(view.av, "There are no artifacts left to auction"))
                    .add(new ButtonNode(view.av, "Okay", () -> {
                        view.popups.complete();
                    }));
        }
        return new Menu(Mechanics.MENU_MARGIN, view.hud.getHeight(), Coords.SIZE.x - (Mechanics.MENU_MARGIN * 2), false,
                node);
    }

    /**
     * Instantiates the Menu that will appear after an Auction has concluded
     */
    public Menu getFollowUpMenu(GameView view, boolean firstIteration) {
        // Determine who won the Auction if this is the first iteration of the window
        if (firstIteration) {
            Optional<Player> winner = this.auction.get().getWinner(view.game.world, this.random);
            boolean humanPlayerWon = winner.map((Player p) -> p.isHumanPlayer()).orElse(false);
            if (humanPlayerWon) {
                view.game.human.auctionChips++;
            } else {
                winner.ifPresent((Player p) -> ((CompPlayer) p).wishlist.doAfterAuction(view, (CompPlayer) p));
                return new Menu(Mechanics.MENU_MARGIN, view.hud.getHeight(),
                        Coords.SIZE.x - (Mechanics.MENU_MARGIN * 2), false,
                        new ListNode().add(new ButtonNode(view.av, "x", () -> view.popups.setDisplay(false)))
                                .add(new TextNode(view.av,
                                        winner.isPresent()
                                                ? "You did not win the auction"
                                                : "Nobody bid in this auction"))
                                .add(new ButtonNode(view.av, "Okay", () -> view.popups.complete())));
            }
        }

        // Populate the Menu with ArtifactNodes if the human Player did win the Auction
        // (or if they have leftover auction chips)
        int a = 0;
        final int columns = 3;
        final int width = Coords.SIZE.x - (Mechanics.MENU_MARGIN * 2);
        ListNode node = new ListNode().add(new ButtonNode(view.av, "x", () -> view.popups.setDisplay(false)))
                .add(new ButtonNode(view.av, "Buy an artifact next time", () -> view.popups.complete()));
        while (a < artifacts.size()) {
            RowNode row1 = new RowNode().setColumns(columns);
            RowNode row2 = new RowNode().setColumns(columns);
            for (int b = 0; b < columns && a < this.artifacts.size();) {
                final Artifact artifact = this.artifacts.get(a);
                if (!artifact.shouldDisplay()) {
                    continue;
                }
                row1.add(new ArtifactNode(view.av, artifact));
                row2.add(new ButtonNode(view.av, "Choose", () -> {
                    if (view.game.human.auctionChips >= artifact.chips) {
                        view.game.human.auctionChips -= artifact.chips;
                        artifact.claim(view, view.game.human);
                        this.artifacts.remove(artifact);
                        view.popups.complete();
                        if (view.game.human.auctionChips > 0) {
                            view.popups.add(this.getFollowUpMenu(view, false));
                        }
                    } else {
                        view.logger.log("You need more auction chips");
                    }
                }));
                a++;
                b++;
            }
            node.add(row1);
            node.add(row2);
        }
        return new Menu(Mechanics.MENU_MARGIN, view.hud.getHeight(), width, false, node);
    }

    /**
     * Returns a Menu to show the human Player which Artifacts they've purchased
     */
    public Menu getOwnedArtifactsMenu(GameView view) {
        int a = 0;
        List<Artifact> artifacts = view.game.human.artifacts;
        final int columns = 3;
        final int width = Coords.SIZE.x - (Mechanics.MENU_MARGIN * 2);
        ListNode node = new ListNode().add(new ButtonNode(view.av, "x", () -> view.popups.complete()));
        if (artifacts.size() == 0) {
            node.add(new TextNode(view.av, "You do not have any artifacts"));
        } else {
            while (a < artifacts.size()) {
                RowNode row = new RowNode().setColumns(columns);
                for (int b = 0; b < columns && a < artifacts.size();) {
                    final Artifact artifact = artifacts.get(a);
                    row.add(new ArtifactNode(view.av, artifact));
                    a++;
                    b++;
                }
                node.add(row);
            }
        }
        return new Menu(Mechanics.MENU_MARGIN, view.hud.getHeight(), width, false, node);
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
        private Optional<Player> getWinner(World world, Random random) {
            if (this.bids.size() == 0) {
                return Optional.empty();
            }
            Set<Player> winners = new HashSet<>();
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
            Player[] w = new Player[winners.size()];
            winners.toArray(w);
            return Optional.of(w[random.nextInt(w.length)]);
        }
    }
}
