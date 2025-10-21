package net.lugocorp.kingdom.ui.hud;
import net.lugocorp.kingdom.ui.ColorScheme;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.math.Coords;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

/**
 * Handles UI for log messages
 */
public class Logger {
    private static final int FONT_SIZE = 22;
    private static final int MAX_TIMER = 3500;
    private static final int FADE_OUT = 500;
    private static final int MAX_ROWS = 6;
    private static final int MARGIN = 5;
    private final GlyphLayout layout = new GlyphLayout();
    private final LogMessage[] messages = new LogMessage[Logger.MAX_ROWS];
    private final GameView view;
    private int timer = 0;
    private int n = 0;

    public Logger(GameView view) {
        this.view = view;
    }

    /**
     * Calls into log() with dispel = false and color = ColorScheme.TEXT.color
     */
    public void log(String message) {
        this.log(message, ColorScheme.TEXT.color);
    }

    /**
     * Calls into log() with the error sound effect
     */
    public void error(String message) {
        this.view.av.loaders.sounds.play("sfx/error");
        this.log(message, ColorScheme.ERROR.color);
    }

    /**
     * Adds a new log message to the queue
     */
    private void log(String message, Color color) {
        // Add this message to the log
        if (this.n < Logger.MAX_ROWS) {
            this.n++;
        }
        for (int a = Math.min(this.n, Logger.MAX_ROWS - 1); a > 0; a--) {
            this.messages[a] = this.messages[a - 1];
        }
        layout.setText(this.view.av.fonts.getFont(Logger.FONT_SIZE, ColorScheme.TEXT.color), message);
        this.messages[0] = new LogMessage(message, color, layout.width, layout.height);
    }

    /**
     * Draws all the log messages the player can see
     */
    public void render(int dt) {
        if (this.n == 0) {
            return;
        }
        this.timer = (int) Math.min(Logger.MAX_TIMER, this.timer + dt);

        // Draw the text
        float y = Coords.SIZE.y - this.view.hud.top.getHeight() - Logger.MARGIN;
        this.view.av.sprites.begin();
        final BitmapFont font = this.view.av.fonts.getFont(Logger.FONT_SIZE, ColorScheme.TEXT.color);
        for (int a = 0; a < this.n; a++) {
            final LogMessage lm = this.messages[a];
            final int x = (Coords.SIZE.x - (int) lm.w) / 2;
            final float fade = (this.timer - (Logger.MAX_TIMER - Logger.FADE_OUT)) / (float) Logger.FADE_OUT;
            final float alpha = 1f - (0.05f * a);
            font.setColor(lm.color.r, lm.color.g, lm.color.b,
                    (a == this.n - 1 && fade > 0f) ? (1f - fade) * alpha : alpha);
            font.draw(this.view.av.sprites, lm.message, x, y);
            y -= lm.h + Logger.MARGIN;
        }
        font.setColor(ColorScheme.TEXT.color);
        this.view.av.sprites.end();

        // Remove a message when the timer runs out
        if (this.timer == Logger.MAX_TIMER) {
            this.timer = 0;
            this.n--;
        }
    }
}
