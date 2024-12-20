package net.lugocorp.kingdom.ui.game;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.math.Coords;
import net.lugocorp.kingdom.utils.math.Rect;
import com.badlogic.gdx.graphics.Color;
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
    public void log(String message) {
        layout.setText(this.view.graphics.fonts.basic, message);
        messages.add(0, new LogMessage(message, layout.width, layout.height));
        if (this.messages.size() > Logger.MAX_ROWS) {
            messages.remove(messages.size() - 1);
        }
        this.timer = 0;
    }

    /**
     * Draws all the log messages the player can see
     */
    public void render() {
        if (this.messages.size() == 0) {
            return;
        }
        this.timer = Math.min(Logger.MAX_TIMER, this.timer + 50);
        int rows = Math.min(this.messages.size(), Logger.MAX_ROWS);
        float y = this.view.hud.getHeight();
        float[] alphas = new float[rows];
        Rect[] rects = new Rect[rows];

        // Draw the background boxes
        this.view.graphics.shapes.begin(ShapeType.Filled);
        for (int a = rows - 1; a >= 0; a--) {
            LogMessage lm = this.messages.get(a);
            rects[a] = Coords.screen.flip((int) ((Coords.SIZE.x - lm.w) / 2), (int) y, (int) lm.w,
                    (int) (lm.h + Logger.MARGIN));
            alphas[a] = (a == 0 && this.timer > Logger.MAX_TIMER - Logger.FADE_OUT)
                    ? (float) (Logger.MAX_TIMER - this.timer) / Logger.FADE_OUT
                    : 1f;
            this.view.graphics.shapes.setColor(Color.BLACK.r, Color.BLACK.g, Color.BLACK.b, alphas[a]);
            this.view.graphics.shapes.rect(rects[a].x - Logger.MARGIN, rects[a].y, rects[a].w + (Logger.MARGIN * 2),
                    rects[a].h);
            y += lm.h + Logger.MARGIN;
        }
        this.view.graphics.shapes.end();

        // Draw the text
        this.view.graphics.sprites.begin();
        for (int a = 0; a < rows; a++) {
            LogMessage lm = this.messages.get(a);
            this.view.graphics.fonts.basic.setColor(Color.RED.r, Color.RED.g, Color.RED.b, alphas[a]);
            this.view.graphics.fonts.basic.draw(this.view.graphics.sprites, lm.message, rects[a].x,
                    rects[a].y + lm.h + Logger.MARGIN);
        }
        this.view.graphics.fonts.basic.setColor(Color.WHITE);
        this.view.graphics.sprites.end();

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
