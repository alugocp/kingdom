package net.lugocorp.kingdom.ui.hud;
import net.lugocorp.kingdom.color.ColorScheme;
import net.lugocorp.kingdom.engine.AudioVideo;
import net.lugocorp.kingdom.engine.fonts.FontParam;
import net.lugocorp.kingdom.math.Coords;
import net.lugocorp.kingdom.math.Rect;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

/**
 * This class displays debug information in the HUD
 */
public class DebugHud {

    /**
     * Returns true if this UI element should be displayed
     */
    public boolean isActive() {
        return false;
    }

    /**
     * Returns the current framerate of the game
     */
    private int getFramerate() {
        return Gdx.graphics.getFramesPerSecond();
    }

    /**
     * Returns how many megabytes are being used by the game
     */
    private int getMemoryUsage() {
        return ((int) Runtime.getRuntime().totalMemory()) / 1000000;
    }

    /**
     * Draws this UI element
     */
    public void draw(AudioVideo av, int y) {
        final Rect bg = Coords.screen.flip(0, y, 100, 55);

        // Draw black background
        av.shapes.begin(ShapeType.Filled);
        av.shapes.setColor(ColorScheme.MENU.color);
        av.shapes.rect(bg.x, bg.y, bg.w, bg.h);
        av.shapes.end();

        // Draw the text
        final BitmapFont font = av.fonts.getFont(new FontParam().setColor(ColorScheme.TEXT.color));
        av.shapes.setColor(ColorScheme.MENU.color);
        av.sprites.begin();
        font.draw(av.sprites, String.format("%d FPS", this.getFramerate()), 5, bg.y + bg.h);
        font.draw(av.sprites, String.format("%d MB", this.getMemoryUsage()), 5, bg.y + bg.h - 20);
        av.sprites.end();
    }
}
