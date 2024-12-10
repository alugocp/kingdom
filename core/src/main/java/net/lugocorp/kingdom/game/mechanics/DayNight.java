package net.lugocorp.kingdom.game.mechanics;
import com.badlogic.gdx.graphics.Color;

/**
 * This class implements a day/night cycle
 */
public class DayNight {
    private static final int DURATION = 8;
    private DayNight.State state = DayNight.State.DAY;
    private int countdown = DayNight.DURATION;

    /**
     * Ticks the countdown until the DayNight cycle changes state
     */
    void tick() {
        if (--this.countdown == 0) {
            this.countdown = DayNight.DURATION;
            this.state = this.isDay() ? DayNight.State.NIGHT : DayNight.State.DAY;
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
        return this.state == DayNight.State.DAY;
    }

    /**
     * Returns true if it is night
     */
    public boolean isNight() {
        return this.state == DayNight.State.NIGHT;
    }

    /**
     * This nested class represents the day/night state dichotomy
     */
    private static enum State {
        DAY(new Color(0.8f, 1.0f, 1.0f, 1f)), NIGHT(new Color(0.1f, 0.1f, 0.5f, 1f));

        private final Color color;

        private State(Color color) {
            this.color = color;
        }
    };
}
