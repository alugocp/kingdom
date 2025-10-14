package net.lugocorp.kingdom.game.world;

/**
 * Enum for different terrain types
 */
enum Biome {
    GRASS("Grass"), WATER("Water"), SAND("Sand"), ROCK("Rock"), SNOW("Snow");
    final String terrain;

    private Biome(String terrain) {
        this.terrain = terrain;
    }
}
