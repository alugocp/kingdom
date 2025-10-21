package net.lugocorp.kingdom.ui.hud;
import com.badlogic.gdx.graphics.Color;

/**
 * This class models a single message in the Logger system
 */
class LogMessage {
    final String message;
    final Color color;
    final float w;
    final float h;

    LogMessage(String message, Color color, float w, float h) {
        this.message = message;
        this.color = color;
        this.w = w;
        this.h = h;
    }
}
