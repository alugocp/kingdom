package net.lugocorp.kingdom.game.mechanics;
import net.lugocorp.kingdom.ui.views.GameView;
import com.badlogic.gdx.graphics.Color;

/**
 * This class implements a day/night cycle
 */
public class DayNight {
    private static final int DURATION = 8;
    private DayNightState state = DayNightState.DAY;
    private int countdown = DayNight.DURATION;

    /**
     * Ticks the countdown until the DayNight cycle changes state
     */
    void tick(GameView view) {
        if (--this.countdown == 0) {
            this.countdown = DayNight.DURATION;
            this.state = this.isDay() ? DayNightState.NIGHT : DayNightState.DAY;
            view.logger.log(this.isDay() ? "The sun has risen once more" : "Night has fallen");
            // TODO trigger an event here
        }
    }

    /**
     * Returns the appropriate sky color for this time of day
     */
    public Color getSkyboxColor() {
        return this.state.color;
    }

    /**
     * Returns true if it is day
     */
    public boolean isDay() {
        return this.state == DayNightState.DAY;
    }

    /**
     * Returns true if it is night
     */
    public boolean isNight() {
        return this.state == DayNightState.NIGHT;
    }
}
