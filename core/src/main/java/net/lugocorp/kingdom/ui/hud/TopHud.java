package net.lugocorp.kingdom.ui.hud;
import net.lugocorp.kingdom.color.ColorScheme;
import net.lugocorp.kingdom.engine.AudioVideo;
import net.lugocorp.kingdom.engine.assets.FontParam;
import net.lugocorp.kingdom.game.Game;
import net.lugocorp.kingdom.game.mechanics.ArtifactAuction;
import net.lugocorp.kingdom.game.mechanics.Mechanics;
import net.lugocorp.kingdom.game.mechanics.NewUnit;
import net.lugocorp.kingdom.math.Coords;
import net.lugocorp.kingdom.menu.Menu;
import net.lugocorp.kingdom.menu.game.DayNightNode;
import net.lugocorp.kingdom.menu.game.ResourceBarsNode;
import net.lugocorp.kingdom.menu.structure.ListNode;
import net.lugocorp.kingdom.menu.structure.RowNode;
import net.lugocorp.kingdom.menu.structure.SpacerNode;
import net.lugocorp.kingdom.menu.text.ButtonNode;
import net.lugocorp.kingdom.menu.text.NakedButtonNode;
import net.lugocorp.kingdom.menu.text.TextNode;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.ui.views.SettingsView;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import java.util.Optional;

/**
 * Represents the top half of the HUD UI
 */
public class TopHud extends Menu {
    private final ResourceBarsNode unitPoints;
    private final ResourceBarsNode auctionPoints;
    private final DayNightNode dayNight;
    private final TextNode auctionChips;
    private final TextNode artifacts;
    private final TextNode gold;

    public TopHud(GameView view) {
        super(0, 0, Coords.SIZE.x, false, new ListNode());
        this.unitPoints = new ResourceBarsNode(view.av,
                new ResourceBarsNode.Bar("Unit Points", ColorScheme.GREEN.hex, 0, NewUnit.MAX_UNIT_POINTS));
        this.auctionPoints = new ResourceBarsNode(view.av, new ResourceBarsNode.Bar("Auction Points",
                ColorScheme.GOLD.hex, 0, ArtifactAuction.MAX_AUCTION_POINTS));
        this.auctionChips = new TextNode(view.av, "").center();
        this.artifacts = new TextNode(view.av, "").center();
        this.gold = new TextNode(view.av, "") {
            /** {@inheritdoc} */
            @Override
            protected BitmapFont getFont() {
                return view.av.fonts.getFont(new FontParam().setColor(ColorScheme.GOLD.color));
            }
        }.center();
        this.dayNight = new DayNightNode(view.av);

        // Populate the root MenuNode and repack
        ((ListNode) this.root)
                // Global details
                .add(new RowNode().add(this.auctionPoints).addExact(45, this.dayNight)
                        .addRatio(15,
                                new ButtonNode(view.av, "Fates",
                                        () -> view.hud.popups.addNextUnrequired(
                                                view.game.mechanics.fates.getViewFatesMenu(view, view.game.human))))
                        .addRatio(15,
                                new ButtonNode(view.av, "Artifacts",
                                        () -> view.hud.popups.addNextUnrequired(view.game.mechanics.auction
                                                .getArtifactsMenu(view, Optional.of(view.game.human)))))
                        .addRatio(15,
                                new ButtonNode(view.av, "Settings",
                                        () -> view.hud.popups.addNextUnrequired(this.getSettingsMenu(view)))))
                // Human player details
                .add(new RowNode().add(this.unitPoints).add(this.gold).add(this.artifacts).add(this.auctionChips));
        this.pack();
    }

    /**
     * Updates the Hud info
     */
    public void update(Game g) {
        this.gold.setText(String.format("%s Gold", this.prettyInt(g.human.gold)));
        this.unitPoints.setValue(0, g.human.getUnitPoints());
        this.auctionPoints.setValue(0, g.mechanics.auction.getPoints());
        this.auctionChips.setText(this.plural("Auction Chip", g.human.auctionChips));
        this.artifacts.setText(this.plural("Artifact", g.human.artifacts.size()));
        this.dayNight.set(g.mechanics.dayNight);
    }

    /**
     * Cuts the length of a displayed number
     */
    private String prettyInt(int value) {
        if (value > 999999) {
            return "999K+";
        }
        return value > 999 ? String.format("%dK", (int) Math.floor((float) value / 1000)) : String.format("%d", value);
    }

    /**
     * Makes the label singular if the value is 1 and plural otherwise
     */
    private String plural(String label, int value) {
        return value == 1 ? String.format("1 %s", label) : String.format("%s %ss", value, label);
    }

    /**
     * Returns a Menu that allows the player to adjust settings
     */
    private Menu getSettingsMenu(GameView view) {
        return new Menu(Mechanics.MENU_MARGIN, view.hud.top.getHeight(), Coords.SIZE.x - (Mechanics.MENU_MARGIN * 2),
                false,
                SettingsView
                        .addSettingsMenuNodes(view.av,
                                new ListNode().add(
                                        new NakedButtonNode(view.av, "x", () -> view.hud.popups.setDisplay(false))))
                        .add(new SpacerNode()).add(new ButtonNode(view.av, "Exit Game", () -> view.close()))
        /*
         * .add(new ButtonNode(view.av, "Save game", () -> { try {
         * view.getSerial().saveGame(view.game);
         * view.hud.logger.log("Game has been saved"); } catch (Exception e) {
         * view.hud.logger.error("Could not save game"); e.printStackTrace(); } }))
         */
        );
    }

    /** {@inheritdoc} */
    @Override
    public void draw(AudioVideo av) {
        super.draw(av);

        // Draw a white bar at the bottom of the HUD
        av.shapes.begin(ShapeType.Line);
        av.shapes.setColor(ColorScheme.OUTLINE.color);
        av.shapes.rect(0, Coords.SIZE.y - this.getHeight(), Coords.SIZE.x, 1);
        av.shapes.end();
    }
}
