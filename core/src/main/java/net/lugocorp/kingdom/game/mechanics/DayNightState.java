package net.lugocorp.kingdom.game.mechanics;
import net.lugocorp.kingdom.utils.logic.Colors;
import com.badlogic.gdx.graphics.Color;

/**
 * This enum represents the day/night state dichotomy
 */
public enum DayNightState {
    DAY(Colors.fromHex(0xC9FFFF)), NIGHT(Colors.fromHex(0xFFFF88));

    public final Color color;

    private DayNightState(Color color) {
        this.color = color;
    }
}
