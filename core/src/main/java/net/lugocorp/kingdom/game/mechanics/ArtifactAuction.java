package net.lugocorp.kingdom.game.mechanics;
import net.lugocorp.kingdom.game.model.Artifact;
import net.lugocorp.kingdom.game.model.Player;
import net.lugocorp.kingdom.utils.math.Point;
import net.lugocorp.kingdom.ui.Hud;
import net.lugocorp.kingdom.ui.menu.ArtifactNode;
import net.lugocorp.kingdom.ui.menu.ButtonNode;
import net.lugocorp.kingdom.ui.menu.HeaderNode;
import net.lugocorp.kingdom.ui.menu.ListNode;
import net.lugocorp.kingdom.ui.menu.Menu;
import net.lugocorp.kingdom.ui.menu.RowNode;
import net.lugocorp.kingdom.ui.menu.TextNode;
import net.lugocorp.kingdom.ui.views.GameView;
import com.badlogic.gdx.Gdx;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * This class manages the logic for artifact auctions
 */
public class ArtifactAuction {
    private final Random random = new Random();
    private Auction auction = new Auction();

    /**
     * Calculates how much gold a Player must pay to participate in an auction
     */
    public int getBuyInCost(int gold) {
        return (int) Math.floor(gold * 0.2);
    }

    /**
     * Instantiates the Menu that allows a human Player to participate in an auction
     */
    public Menu getAuctionBuyInMenu(GameView view) {
        int price = this.getBuyInCost(view.game.human.gold);
        ListNode node = new ListNode().add(new ButtonNode(view.game.graphics, "x", () -> view.popups.setDisplay(false)))
                .add(new HeaderNode(view.game.graphics, "Artifact Auction"))
                .add(new TextNode(view.game.graphics,
                        String.format("Pay %d gold to participate in the auction?", price)))
                .add(new RowNode().add(new ButtonNode(view.game.graphics, "Yes", () -> {
                    // TODO find all vaults under this player's control
                    String error = "You have no vaults with items to bargain with";
                    Set<Point> vaults = new HashSet<>();
                    if (vaults.size() == 0) {
                        view.logger.log(error);
                        view.popups.complete();
                        return;
                    }
                    view.popups.setDisplay(false);
                    view.selectTiles(vaults, error, (Point p) -> {
                        this.auction.addBidder(view.game.human, p);
                        view.game.human.gold -= price;
                        view.popups.complete();
                    });
                })).add(new ButtonNode(view.game.graphics, "No", () -> view.popups.complete())));
        return new Menu(Hud.BUTTON_WIDTH, Hud.HEIGHT, Gdx.graphics.getWidth() - (Hud.BUTTON_WIDTH * 2), false, node);
    }

    /**
     * Instantiates the Menu that will appear after an Auction has concluded
     */
    public Menu getFollowUpMenu(GameView view) {
        // Inform the human Player if they did not win the Auction
        if (!this.auction.getWinner(this.random).isHumanPlayer()) {
            return new Menu(Hud.BUTTON_WIDTH, Hud.HEIGHT, Gdx.graphics.getWidth() - (Hud.BUTTON_WIDTH * 2), false,
                    new ListNode().add(new ButtonNode(view.game.graphics, "x", () -> view.popups.setDisplay(false)))
                            .add(new TextNode(view.game.graphics, "You did not win the auction"))
                            .add(new ButtonNode(view.game.graphics, "Okay", () -> view.popups.complete())));
        }

        // Populate the Menu with ArtifactNodes if the human Player did win the Auction
        int a = 0;
        int width = Gdx.graphics.getWidth() - (Hud.BUTTON_WIDTH * 2);
        int columns = (int) Math.floor(width / ArtifactNode.WIDTH);
        ListNode node = new ListNode()
                .add(new ButtonNode(view.game.graphics, "x", () -> view.popups.setDisplay(false)));
        // TODO populate this list of Artifacts
        List<Artifact> artifacts = new ArrayList<>();
        while (a < artifacts.size()) {
            RowNode row1 = new RowNode();
            RowNode row2 = new RowNode();
            for (int b = 0; b < columns && a < artifacts.size(); b++) {
                final Artifact artifact = artifacts.get(a);
                row1.add(new ArtifactNode(view.game.graphics, artifact));
                row2.add(new ButtonNode(view.game.graphics, "Choose", () -> {
                    artifact.claim(view.game.human);
                    view.popups.complete();
                }));
                a++;
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

        /**
         * Adds a new bid to this Auction
         */
        public void addBidder(Player bidder, Point p) {
            this.bids.put(bidder, p);
        }

        /**
         * Returns true if this Auction has any bids
         */
        private boolean hasBids() {
            return this.bids.size() > 0;
        }

        /**
         * Retrieves the winner of this Auction
         */
        private Player getWinner(Random random) {
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
            return w[random.nextInt(w.length)];
        }
    }
}
