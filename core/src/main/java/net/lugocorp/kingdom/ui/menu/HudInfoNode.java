package net.lugocorp.kingdom.ui.menu;
import net.lugocorp.kingdom.engine.AudioVideo;
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
    private final TextNode artifacts;

    public HudInfoNode(AudioVideo av) {
        this.gold = new TextNode(av, "");
        this.unitPoints = new TextNode(av, "");
        this.auctionPoints = new TextNode(av, "");
        this.auctionChips = new TextNode(av, "");
        this.artifacts = new TextNode(av, "");
        this.add(this.gold).add(this.unitPoints).add(this.auctionPoints).add(this.auctionChips).add(this.artifacts);
    }

    /**
     * Updates the information displayed in this MenuNode
     */
    public void updateInfo(Game game) {
        this.gold.setText(String.format("%s Gold", this.prettyInt(game.human.gold)));
        this.unitPoints.setText(String.format("%d / %d Unit Points", game.human.unitPoints, NewUnit.MAX_UNIT_POINTS));
        this.auctionPoints.setText(
                String.format("%d / %d Auction Points", game.auctionPoints, ArtifactAuction.MAX_AUCTION_POINTS));
        this.auctionChips.setText(this.plural("Auction Chip", game.human.auctionChips));
        this.artifacts.setText(this.plural("Artifact", game.human.artifacts.size()));
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

    /**
     * Makes the label singular if the value is 1 and plural otherwise
     */
    private String plural(String label, int value) {
        if (value == 1) {
            return String.format("1 %s", label);
        }
        return String.format("%s %ss", value, label);
    }
}
