package net.lugocorp.kingdom.world;
import net.lugocorp.kingdom.assets.AssetsLoader;
import net.lugocorp.kingdom.game.Tile;

/**
 * This class handles world generation logic
 */
public class WorldGenerator {

    /**
     * The main function that initiates world generation
     */
    public World generateWorld(AssetsLoader assets, int width, int height) {
        World w = new World(width, height);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                final int x1 = x;
                final int y1 = y;
                w.getTile(x, y).ifPresent((Tile t) -> t.setModelInstance(assets.createModelInstance("tile"), x1, y1));
            }
        }
        return w;
    }
}