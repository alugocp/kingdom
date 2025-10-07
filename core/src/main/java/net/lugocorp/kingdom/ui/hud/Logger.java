package net.lugocorp.kingdom.ui.hud;
import net.lugocorp.kingdom.ui.ColorScheme;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.math.Coords;
import net.lugocorp.kingdom.utils.math.Rect;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles UI for log messages
 */
public class Logger {
    private static final int FONT_SIZE = 22;
    private static final int MAX_TIMER = 4500;
    private static final int FADE_OUT = 500;
    private static final int MAX_ROWS = 8;
    private static final int MARGIN = 10;
    private final List<LogMessage> messages = new ArrayList<>();
    private final GlyphLayout layout = new GlyphLayout();
    private final GameView view;
    private int timer = 0;
    // TODO make logs pop up at the bottom and push the top messages away (or
    // something) and remove the dispel parameter

    public Logger(GameView view) {
        this.view = view;
    }

    /**
     * Adds a new log message to the queue
     */
    public void log(String message, boolean dispel, Color color) {
        // If we dispel then remove this message from the log
        if (dispel) {
            for (int a = this.messages.size() - 1; a >= 0; a--) {
                if (this.messages.get(a).message.equals(message)) {
                    this.messages.remove(a);
                    break;
                }
            }
        }

        // Add this message to the log
        layout.setText(this.view.av.fonts.getFont(Logger.FONT_SIZE, ColorScheme.TEXT.color), message);
        messages.add(0, new LogMessage(message, color, layout.width, layout.height));
        if (this.messages.size() > Logger.MAX_ROWS) {
            messages.remove(messages.size() - 1);
        }
        this.timer = 0;
    }

    /**
     * Calls into log() with color = ColorScheme.TEXT.color
     */
    public void log(String message, boolean dispel) {
        this.log(message, dispel, ColorScheme.TEXT.color);
    }

    /**
     * Calls into log() with dispel = false and color = ColorScheme.TEXT.color
     */
    public void log(String message) {
        this.log(message, false, ColorScheme.TEXT.color);
    }

    /**
     * Calls into log() with the error sound effect
     */
    public void error(String message, boolean dispel) {
        this.view.av.loaders.sounds.play("sfx/error");
        this.log(message, dispel, ColorScheme.ERROR.color);
    }

    /**
     * Calls into log() with the error sound effect and dispel = false
     */
    public void error(String message) {
        this.error(message, false);
    }

    /**
     * Draws all the log messages the player can see
     */
    public void render(int dt) {
        if (this.messages.size() == 0) {
            return;
        }
        final int rows = this.messages.size();
        final float[] alphas = new float[rows];
        final Rect[] rects = new Rect[rows];
        float y = this.view.hud.getHeight() + 1;

        // Update the timer
        this.timer = (int) Math.min(Logger.MAX_TIMER, this.timer + dt);

        // Draw the background boxes
        this.view.av.shapes.begin(ShapeType.Filled);
        for (int a = rows - 1; a >= 0; a--) {
            LogMessage lm = this.messages.get(a);
            rects[a] = Coords.screen.flip((int) ((Coords.SIZE.x - lm.w) / 2), (int) y, (int) lm.w,
                    (int) (lm.h + Logger.MARGIN));
            alphas[a] = (a == 0 && this.timer > Logger.MAX_TIMER - Logger.FADE_OUT)
                    ? (float) (Logger.MAX_TIMER - this.timer) / Logger.FADE_OUT
                    : 1f;
            this.view.av.shapes.setColor(ColorScheme.MENU.color.r, ColorScheme.MENU.color.g, ColorScheme.MENU.color.b,
                    alphas[a]);
            this.view.av.shapes.rect(rects[a].x - Logger.MARGIN, rects[a].y, rects[a].w + (Logger.MARGIN * 2),
                    rects[a].h);
            y += lm.h + Logger.MARGIN;
        }
        this.view.av.shapes.end();

        // Draw the text
        this.view.av.sprites.begin();
        final BitmapFont font = this.view.av.fonts.getFont(Logger.FONT_SIZE, ColorScheme.TEXT.color);
        for (int a = 0; a < rows; a++) {
            final LogMessage lm = this.messages.get(a);
            font.setColor(lm.color.r, lm.color.g, lm.color.b, alphas[a]);
            font.draw(this.view.av.sprites, lm.message, rects[a].x, rects[a].y + lm.h + Logger.MARGIN);
        }
        font.setColor(ColorScheme.TEXT.color);
        this.view.av.sprites.end();

        // Remove a message when the timer runs out
        if (this.timer == Logger.MAX_TIMER) {
            this.messages.remove(0);
            this.timer = 0;
        }
    }
}
