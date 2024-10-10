package net.lugocorp.kingdom.ui;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import net.lugocorp.kingdom.game.Player;
import net.lugocorp.kingdom.math.Coords;
import net.lugocorp.kingdom.math.Rect;
import net.lugocorp.kingdom.ui.menu.ButtonNode;
import net.lugocorp.kingdom.ui.menu.ListNode;
import net.lugocorp.kingdom.ui.menu.Menu;
import net.lugocorp.kingdom.ui.views.GameView;

/**
 * This class handles rendering the Player's HUD UI
 */
public class Hud {
    public static final int HEIGHT = 35;
    private final GameView view;
    public final Menu turnMenu;

    public Hud(GameView view) {
        this.view = view;
        this.turnMenu = new Menu(Gdx.graphics.getWidth() - 150, Hud.HEIGHT, 150, false,
                new ListNode().add(new ButtonNode(this.view.game.graphics, "End Turn", () -> {
                    this.view.game.iterateTurnPlayer();
                    this.view.refreshMenu(true);
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
                Gdx.graphics.getHeight() - 5);
        font.draw(this.view.game.graphics.sprites,
                String.format("Unit Points: %d / %d", p.unitPoints, Player.MAX_UNIT_POINTS), 215,
                Gdx.graphics.getHeight() - 5);
        this.view.game.graphics.sprites.end();

        // Draw the "End Turn" button
        if (this.view.game.canHumanPlayerAct()) {
            this.turnMenu.draw(this.view.game.graphics);
        }
    }
}
