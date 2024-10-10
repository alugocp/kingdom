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
                    // TODO remove this second call and instead kick off AI player logic
                    this.view.game.iterateTurnPlayer();
                    this.view.refreshMenu(true);
                })));
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
        font.draw(this.view.game.graphics.sprites, String.format("Gold: %d", p.gold), 15, Gdx.graphics.getHeight() - 5);
        font.draw(this.view.game.graphics.sprites, String.format("Unit Points: %d", p.unitPoints), 215,
                Gdx.graphics.getHeight() - 5);
        this.view.game.graphics.sprites.end();

        // Draw the "End Turn" button
        if (this.view.game.getTurnPlayer().isHumanPlayer()) {
            this.turnMenu.draw(this.view.game.graphics);
        }
    }
}
