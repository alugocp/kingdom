package net.lugocorp.kingdom.world;
import java.util.Optional;
import net.lugocorp.kingdom.assets.AssetsLoader;
import net.lugocorp.kingdom.game.Tile;
import net.lugocorp.kingdom.game.Unit;

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
                w.getTile(x, y).ifPresent((Tile t) -> t.setModelInstance(assets, "tile", x1,
                        (x1 == 2 && y1 == 0) ? 2 : ((x1 == 0 && y1 == 0) ? 1 : 0), y1));
                if (x == 0 && y == 1) {
                    w.getTile(x, y).ifPresent((Tile t) -> {
                        t.unit = Optional.of(new Unit());
                        t.unit.get().setModelInstance(assets, "crystal", 0, 0, 1);
                    });
                }
            }
        }
        return w;
    }
}
