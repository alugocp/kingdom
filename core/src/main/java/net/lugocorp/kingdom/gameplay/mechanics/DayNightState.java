package net.lugocorp.kingdom.gameplay.mechanics;
import net.lugocorp.kingdom.color.Colors;
import com.badlogic.gdx.graphics.Color;

/**
 * This enum represents the day/night state dichotomy
 */
public enum DayNightState {
    DAY(Colors.fromHex(0xC9FFFF)), NIGHT(Colors.fromHex(0x000088));

    public final Color color;

    private DayNightState(Color color) {
        this.color = color;
    }
}
