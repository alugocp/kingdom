package net.lugocorp.kingdom.engine;

/**
 * A number of options that allow for better accessibility
 */
public class Settings {
    private float soundVolume = 1.0f;
    private float musicVolume = 1.0f;
    private boolean autoComplete = false;
    private boolean reverse = false;

    /**
     * Gets the soundVolume field
     */
    public float getSoundVolume() {
        return this.soundVolume;
    }

    /**
     * Sets the soundVolume field
     */
    public void setSoundVolume(float soundVolume) {
        this.soundVolume = soundVolume;
    }

    /**
     * Gets the musicVolume field
     */
    public float getMusicVolume() {
        return this.musicVolume;
    }

    /**
     * Sets the musicVolume field
     */
    public void setMusicVolume(float musicVolume) {
        this.musicVolume = musicVolume;
    }

    /**
     * Returns true if the scroll direction should be reversed
     */
    public boolean getReversedScrollDirection() {
        return this.reverse;
    }

    /**
     * Sets whether or not we reverse the scroll direction
     */
    public void setReversedScrollDirection(boolean reverse) {
        this.reverse = reverse;
    }

    /**
     * Gets the autoComplete field
     */
    public boolean getAutoComplete() {
        return this.autoComplete;
    }

    /**
     * Sets the autoComplete field
     */
    public void setAutoComplete(boolean autoComplete) {
        this.autoComplete = autoComplete;
    }
}
