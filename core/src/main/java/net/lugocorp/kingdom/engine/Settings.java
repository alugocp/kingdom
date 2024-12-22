package net.lugocorp.kingdom.engine;

/**
 * A number of options that allow for better accessibility
 */
public class Settings {
    private float soundVolume = 1.0f;
    private float musicVolume = 1.0f;

    /**
     * Gets the soundVolume field
     */
    public float getSoundVolume() {
        return this.soundVolume;
    }

    /**
     * Gets the musicVolume field
     */
    public float getMusicVolume() {
        return this.musicVolume;
    }

    /**
     * Sets the soundVolume field
     */
    public void setSoundVolume(float soundVolume) {
        this.soundVolume = soundVolume;
    }

    /**
     * Sets the musicVolume field
     */
    public void setMusicVolume(float musicVolume) {
        this.musicVolume = musicVolume;
    }
}
