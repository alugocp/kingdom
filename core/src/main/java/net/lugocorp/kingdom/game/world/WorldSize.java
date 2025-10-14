package net.lugocorp.kingdom.game.world;

/**
 * Represents the possible size values for World generation
 */
public enum WorldSize {
    SMALL("Small", 35, 35); // , MEDIUM("Medium", 150, 150), LARGE("Large", 250, 250);
    public final String label;
    public final int w;
    public final int h;

    private WorldSize(String label, int w, int h) {
        this.label = label;
        this.w = w;
        this.h = h;
    }

    /**
     * Returns the nth value from the enum
     */
    public static WorldSize fromIndex(int index) {
        return WorldSize.values()[index];
    }
}
