package net.lugocorp.kingdom.ui.game;
import net.lugocorp.kingdom.game.mechanics.ArtifactAuction;
import net.lugocorp.kingdom.game.mechanics.NewUnit;
import net.lugocorp.kingdom.game.model.Player;
import net.lugocorp.kingdom.ui.menu.ButtonNode;
import net.lugocorp.kingdom.ui.menu.ListNode;
import net.lugocorp.kingdom.ui.menu.Menu;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.math.Coords;
import net.lugocorp.kingdom.utils.math.Rect;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

/**
 * This class handles rendering the Player's HUD UI
 */
public class Hud {
    public static final int BUTTON_WIDTH = 150;
    public static final int HEIGHT = 35;
    private final GameView view;
    public final Menu turnMenu;

    public Hud(GameView view) {
        this.view = view;
        this.turnMenu = new Menu(Gdx.graphics.getWidth() - Hud.BUTTON_WIDTH, Hud.HEIGHT, Hud.BUTTON_WIDTH, false,
                new ListNode().add(new ButtonNode(this.view.game.graphics, "Complete Turn", () -> {
                    if (this.view.popups.get().isPresent()) {
                        this.view.popups.setDisplay(true);
                    } else {
                        this.view.logger.log("You have ended your turn");
                        this.view.game.mechanics.turns.iterateTurnPlayer(this.view);
                        this.view.menu.refresh(true);
                    }
                })));
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
     * Draws the HUD UI
     */
    public void render() {
        BitmapFont font = this.view.game.graphics.fonts.basic;
        Player p = this.view.game.human;

        // Background
        Rect bg = Coords.screen.flip(0, 0, Gdx.graphics.getWidth(), Hud.HEIGHT);
        this.view.game.graphics.shapes.begin(ShapeType.Filled);
        this.view.game.graphics.shapes.setColor(Color.BLACK);
        this.view.game.graphics.shapes.rect(bg.x, bg.y, bg.w, bg.h);
        this.view.game.graphics.shapes.end();

        // Draw Player stats
        this.view.game.graphics.sprites.begin();
        font.draw(this.view.game.graphics.sprites, String.format("Gold: %s", this.prettyInt(p.gold)), 15,
                Gdx.graphics.getHeight() - 8);
        font.draw(this.view.game.graphics.sprites,
                String.format("Unit Points: %d / %d", p.unitPoints, NewUnit.MAX_UNIT_POINTS), 215,
                Gdx.graphics.getHeight() - 8);
        font.draw(this.view.game.graphics.sprites,
                String.format("Auction Points: %d / %d", view.game.auctionPoints, ArtifactAuction.MAX_AUCTION_POINTS),
                415, Gdx.graphics.getHeight() - 8);
        font.draw(this.view.game.graphics.sprites, String.format("Auction Chips: %d", p.auctionChips), 615,
                Gdx.graphics.getHeight() - 8);
        this.view.game.graphics.sprites.end();

        // Draw the "Complete Turn" button
        if (this.view.game.mechanics.turns.canHumanPlayerAct()) {
            this.turnMenu.draw(this.view.game.graphics);
        }
    }
}
