package net.lugocorp.kingdom.game.world;

/**
 * Contains parameters for World generation
 */
public class WorldGenOptions {
    public WorldGenOptions.WorldSize size = WorldGenOptions.WorldSize.SMALL;
    public String seed;

    public WorldGenOptions(String seed) {
        this.seed = seed;
    }

    /**
     * Represents the possible size values for World generation
     */
    public static enum WorldSize {
        SMALL("Small", 50, 50), MEDIUM("Medium", 150, 150), LARGE("Large", 250, 250);
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
        public static WorldGenOptions.WorldSize fromIndex(int index) {
            return WorldGenOptions.WorldSize.values()[index];
        }
    }
}
