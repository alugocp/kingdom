package net.lugocorp.kingdom.game.world;
import net.lugocorp.kingdom.content.Labels;

/**
 * Enum for different terrain types
 */
enum Biome {
    GRASS(Labels.tile_grass), WATER(Labels.tile_water), SAND(Labels.tile_sand), ROCK(Labels.tile_rock), SNOW(
            Labels.tile_snow);
    final String terrain;

    private Biome(String terrain) {
        this.terrain = terrain;
    }
}
