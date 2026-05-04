package net.lugocorp.kingdom.game.world;

/**
 * Represents the possible size values for World generation
 */
public enum WorldSize {
    SMALL("Small", 25, 25, 25), MEDIUM("Medium", 35, 35, 50);
    public final String label;
    public final int towers;
    public final int w;
    public final int h;

    private WorldSize(String label, int w, int h, int towers) {
        this.label = label;
        this.towers = towers;
        this.w = w;
        this.h = h;
    }

    /**
     * Returns the total area in tiles associated with this WorldSize value
     */
    public int getArea() {
        return this.w * this.h;
    }

    /**
     * Returns the nth value from the enum
     */
    public static WorldSize fromIndex(int index) {
        return WorldSize.values()[index];
    }
}
