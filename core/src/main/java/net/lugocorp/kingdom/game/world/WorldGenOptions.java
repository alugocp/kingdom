package net.lugocorp.kingdom.game.world;

/**
 * Contains parameters for World generation
 */
public class WorldGenOptions {
    public WorldSize size = WorldSize.values()[0];
    public long seed;

    public WorldGenOptions(long seed) {
        this.seed = seed;
    }
}
