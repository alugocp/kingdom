package net.lugocorp.kingdom.ui;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import net.lugocorp.kingdom.engine.GameGraphics;
import net.lugocorp.kingdom.game.Player;
import net.lugocorp.kingdom.math.Coords;
import net.lugocorp.kingdom.math.Rect;

/**
 * This class handles rendering the Player's HUD UI
 */
public class Hud {
    public static final int HEIGHT = 35;

    /**
     * Draws the HUD UI
     */
    public static void render(GameGraphics graphics, Player player) {
        BitmapFont font = graphics.fonts.basic;

        // Background
        Rect bg = Coords.screen.flip(0, 0, Gdx.graphics.getWidth(), Hud.HEIGHT);
        graphics.shapes.begin(ShapeType.Filled);
        graphics.shapes.setColor(Color.BLACK);
        graphics.shapes.rect(bg.x, bg.y, bg.w, bg.h);
        graphics.shapes.end();

        // Draw Player stats
        graphics.sprites.begin();
        font.draw(graphics.sprites, String.format("Gold: %d", player.gold), 15, Gdx.graphics.getHeight() - 5);
        font.draw(graphics.sprites, String.format("Unit Points: %d", player.unitPoints), 215,
                Gdx.graphics.getHeight() - 5);
        graphics.sprites.end();
    }
}
