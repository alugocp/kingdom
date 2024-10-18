package net.lugocorp.kingdom.ui.game;
import net.lugocorp.kingdom.engine.GameGraphics;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles UI for log messages
 */
public class Logger {
    private static final int MAX_TIMER = 4000;
    private static final int FADE_OUT = 1000;
    private static final int MAX_ROWS = 8;
    private static final int MARGIN = 10;
    private final List<LogMessage> messages = new ArrayList<>();
    private final GlyphLayout layout = new GlyphLayout();
    private final GameGraphics graphics;
    private int timer = 0;

    public Logger(GameGraphics graphics) {
        this.graphics = graphics;
    }

    /**
     * Adds a new log message to the queue
     */
    public void log(String message) {
        layout.setText(this.graphics.fonts.basic, message);
        messages.add(0, new LogMessage(message, layout.width, layout.height));
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
        float y = this.messages.get(rows - 1).h + Logger.MARGIN;
        this.graphics.sprites.begin();
        for (int a = rows - 1; a >= 0; a--) {
            LogMessage lm = this.messages.get(a);
            float alpha = (a == 0 && this.timer > Logger.MAX_TIMER - Logger.FADE_OUT)
                    ? (float) (Logger.MAX_TIMER - this.timer) / Logger.FADE_OUT
                    : 1f;
            this.graphics.fonts.basic.setColor(Color.RED.r, Color.RED.g, Color.RED.b, alpha);
            this.graphics.fonts.basic.draw(this.graphics.sprites, lm.message, (Gdx.graphics.getWidth() - lm.w) / 2, y);
            y += lm.h + Logger.MARGIN;
        }
        this.graphics.fonts.basic.setColor(Color.WHITE);
        this.graphics.sprites.end();
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
