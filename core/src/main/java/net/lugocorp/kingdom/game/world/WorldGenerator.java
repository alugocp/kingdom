package net.lugocorp.kingdom.game.world;
import net.lugocorp.kingdom.game.Game;

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
                game.generator.tile("Grassland", x, y).spawn(game);
            }
        }
        game.generator.unit("Axolotl", 0, 1).spawn(game);
        game.generator.unit("Crystal", 1, 1).spawn(game);
        game.generator.unit("Crystal", 3, 1).spawn(game);
        game.generator.building("Vault", 3, 2).spawn(game);
        game.generator.building("Mine", 3, 1).spawn(game);
        game.generator.building("Mine", 4, 2).spawn(game);
        game.generator.building("Forest", 5, 3).spawn(game);
        game.generator.building("Forest", 6, 3).spawn(game);
        game.generator.building("Forest", 5, 4).spawn(game);
        game.generator.building("Forest", 0, 0).spawn(game);
        game.setLeader(game.world.getTile(0, 1).get().unit.get(), game.human);
        game.addComputerPlayer("AI");
    }
}
