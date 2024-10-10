package net.lugocorp.kingdom.world;
import net.lugocorp.kingdom.game.Game;
import net.lugocorp.kingdom.game.model.Tile;
import java.util.Optional;

/**
 * This class handles world generation logic
 */
public class WorldGenerator {

    /**
     * The main function that initiates world generation
     */
    public void generateWorld(Game game) {
        final World w = game.world;
        for (int x = 0; x < w.getWidth(); x++) {
            for (int y = 0; y < w.getHeight(); y++) {
                w.setTile(game.generator.tile("Grassland", x, y), x, y);
            }
        }
        w.getTile(0, 1).ifPresent((Tile t) -> {
            t.unit = Optional.of(game.generator.unit("Crystal", 0, 1));
            game.setLeader(t.unit.get(), game.human);
        });
        w.getTile(1, 1).ifPresent((Tile t) -> {
            t.unit = Optional.of(game.generator.unit("Crystal", 1, 1));
        });
        w.getTile(3, 1).ifPresent((Tile t) -> {
            t.unit = Optional.of(game.generator.unit("Crystal", 3, 1));
        });
        w.getTile(3, 2).ifPresent((Tile t) -> {
            t.building = Optional.of(game.generator.building("Mine", 3, 2));
        });
        w.getTile(3, 1).ifPresent((Tile t) -> {
            t.building = Optional.of(game.generator.building("Mine", 3, 1));
        });
        w.getTile(4, 2).ifPresent((Tile t) -> {
            t.building = Optional.of(game.generator.building("Mine", 4, 2));
        });
        game.addComputerPlayer("AI");
    }
}
