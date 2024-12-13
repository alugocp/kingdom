package net.lugocorp.kingdom.ui.menu;
import net.lugocorp.kingdom.engine.Graphics;
import net.lugocorp.kingdom.game.Game;
import net.lugocorp.kingdom.game.mechanics.ArtifactAuction;
import net.lugocorp.kingdom.game.mechanics.NewUnit;

/**
 * Used to display resources on the in-game HUD
 */
public class HudInfoNode extends RowNode {
    private final TextNode gold;
    private final TextNode unitPoints;
    private final TextNode auctionPoints;
    private final TextNode auctionChips;

    public HudInfoNode(Graphics graphics) {
        this.gold = new TextNode(graphics, "");
        this.unitPoints = new TextNode(graphics, "");
        this.auctionPoints = new TextNode(graphics, "");
        this.auctionChips = new TextNode(graphics, "");
        this.add(this.gold).add(this.unitPoints).add(this.auctionPoints).add(this.auctionChips);
    }

    /**
     * Updates the information displayed in this MenuNode
     */
    public void updateInfo(Game game) {
        this.gold.setText(String.format("%s Gold", this.prettyInt(game.human.gold)));
        this.unitPoints.setText(String.format("%d / %d Unit Points", game.human.unitPoints, NewUnit.MAX_UNIT_POINTS));
        this.auctionPoints.setText(
                String.format("%d / %d Auction Points", game.auctionPoints, ArtifactAuction.MAX_AUCTION_POINTS));
        this.auctionChips.setText(String.format("%d Auction Chips", game.human.auctionChips));
    }

    /**
     * Cuts the length of a displayed number
     */
    private String prettyInt(int value) {
        if (value > 999999) {
            return "999K+";
        }
        if (value > 999) {
            return String.format("%dK", (int) Math.floor((float) value / 1000));
        }
        return String.format("%d", value);
    }
}
