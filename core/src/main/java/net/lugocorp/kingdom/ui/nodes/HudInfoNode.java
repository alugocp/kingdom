package net.lugocorp.kingdom.ui.nodes;
import net.lugocorp.kingdom.engine.AudioVideo;
import net.lugocorp.kingdom.game.Game;
import net.lugocorp.kingdom.game.mechanics.ArtifactAuction;
import net.lugocorp.kingdom.game.mechanics.NewUnit;
import net.lugocorp.kingdom.ui.ColorScheme;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

/**
 * Used to display resources on the in-game HUD
 */
public class HudInfoNode extends RowNode {
    private final ResourceBarsNode unitPoints;
    private final ResourceBarsNode auctionPoints;
    private final TextNode auctionChips;
    private final TextNode artifacts;
    private final TextNode gold;
    private final TextNode dayNight;

    public HudInfoNode(AudioVideo av) {
        this.unitPoints = new ResourceBarsNode(av,
                new ResourceBarsNode.Bar("Unit Points", ColorScheme.GREEN.hex, 0, NewUnit.MAX_UNIT_POINTS));
        this.auctionPoints = new ResourceBarsNode(av, new ResourceBarsNode.Bar("Auction Points", ColorScheme.GOLD.hex,
                0, ArtifactAuction.MAX_AUCTION_POINTS));
        this.auctionChips = new TextNode(av, "");
        this.artifacts = new TextNode(av, "");
        this.gold = new TextNode(av, "") {
            /** {@inheritdoc} */
            @Override
            protected BitmapFont getFont() {
                return this.av.fonts.getFont(ColorScheme.GOLD.color);
            }
        };
        this.dayNight = new TextNode(av, "");
        this.add(this.unitPoints).add(this.auctionPoints).add(this.auctionChips).add(this.artifacts).add(this.gold)
                .add(this.dayNight);
    }

    /**
     * Updates the information displayed in this MenuNode
     */
    public void updateInfo(Game game) {
        this.gold.setText(String.format("%s Gold", this.prettyInt(game.human.gold)));
        this.unitPoints.setValue(0, game.human.getUnitPoints());
        this.auctionPoints.setValue(0, game.mechanics.auction.getPoints());
        this.auctionChips.setText(this.plural("Auction Chip", game.human.auctionChips));
        this.artifacts.setText(this.plural("Artifact", game.human.artifacts.size()));
        this.dayNight.setText(game.mechanics.dayNight.isDay() ? "Daytime" : "Nighttime");
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
