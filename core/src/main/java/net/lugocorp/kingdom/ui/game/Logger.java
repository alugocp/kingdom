package net.lugocorp.kingdom.ui.game;
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
    private static final int MAX_TIMER = 8000;
    private static final int FADE_OUT = 1000;
    private static final int MAX_ROWS = 8;
    private static final int MARGIN = 10;
    private final List<LogMessage> messages = new ArrayList<>();
    private final GlyphLayout layout = new GlyphLayout();
    private final GameView view;
    private int timer = 0;

    public Logger(GameView view) {
        this.view = view;
    }

    /**
     * Adds a new log message to the queue
     */
    public void log(String message, boolean dispel) {
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
        layout.setText(this.view.av.fonts.getFont(0xffffff), message);
        messages.add(0, new LogMessage(message, layout.width, layout.height));
        if (this.messages.size() > Logger.MAX_ROWS) {
            messages.remove(messages.size() - 1);
        }
        this.timer = 0;
    }

    /**
     * Calls into log() with dispel = false
     */
    public void log(String message) {
        this.log(message, false);
    }

    /**
     * Draws all the log messages the player can see
     */
    public void render() {
        if (this.messages.size() == 0) {
            return;
        }
        this.timer = Math.min(Logger.MAX_TIMER, this.timer + 50);
        int rows = this.messages.size();
        float y = this.view.hud.getHeight();
        float[] alphas = new float[rows];
        Rect[] rects = new Rect[rows];

        // Draw the background boxes
        this.view.av.shapes.begin(ShapeType.Filled);
        for (int a = rows - 1; a >= 0; a--) {
            LogMessage lm = this.messages.get(a);
            rects[a] = Coords.screen.flip((int) ((Coords.SIZE.x - lm.w) / 2), (int) y, (int) lm.w,
                    (int) (lm.h + Logger.MARGIN));
            alphas[a] = (a == 0 && this.timer > Logger.MAX_TIMER - Logger.FADE_OUT)
                    ? (float) (Logger.MAX_TIMER - this.timer) / Logger.FADE_OUT
                    : 1f;
            this.view.av.shapes.setColor(Color.BLACK.r, Color.BLACK.g, Color.BLACK.b, alphas[a]);
            this.view.av.shapes.rect(rects[a].x - Logger.MARGIN, rects[a].y, rects[a].w + (Logger.MARGIN * 2),
                    rects[a].h);
            y += lm.h + Logger.MARGIN;
        }
        this.view.av.shapes.end();

        // Draw the text
        this.view.av.sprites.begin();
        BitmapFont font = this.view.av.fonts.getFont(18, 0xff0000);
        for (int a = 0; a < rows; a++) {
            LogMessage lm = this.messages.get(a);
            font.setColor(Color.RED.r, Color.RED.g, Color.RED.b, alphas[a]);
            font.draw(this.view.av.sprites, lm.message, rects[a].x, rects[a].y + lm.h + Logger.MARGIN);
        }
        font.setColor(Color.RED);
        this.view.av.sprites.end();

        // Remove a message when the timer runs out
        if (this.timer == Logger.MAX_TIMER) {
            this.messages.remove(0);
            this.timer = 0;
        }
    }

    /**
     * An internal representation of a single message in the log
     */
    private static class LogMessage {
        private final String message;
        private final float w;
        private final float h;

        private LogMessage(String message, float w, float h) {
            this.message = message;
            this.w = w;
            this.h = h;
        }
    }
}
