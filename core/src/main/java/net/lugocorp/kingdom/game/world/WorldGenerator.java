package net.lugocorp.kingdom.game.world;
import net.lugocorp.kingdom.game.Game;

/**
 * This class handles world generation logic
 */
public class WorldGenerator {

    /**
     * The main function that initiates world generation
     */
    public void generateWorld(Game g) {
        for (int x = 0; x < g.world.getWidth(); x++) {
            for (int y = 0; y < g.world.getHeight(); y++) {
                g.generator.tile("Grassland", x, y).spawn(g);
            }
        }
        g.generator.unit("Axolotl", 0, 1).spawn(g);
        g.generator.unit("Crystal", 1, 1).spawn(g);
        g.generator.unit("Crystal", 3, 1).spawn(g);
        g.generator.building("Vault", 3, 2).spawn(g);
        g.generator.building("Mine", 3, 1).spawn(g);
        g.generator.building("Mine", 4, 2).spawn(g);
        g.generator.building("Forest", 5, 3).spawn(g);
        g.generator.building("Forest", 6, 3).spawn(g);
        g.generator.building("Forest", 5, 4).spawn(g);
        g.generator.building("Forest", 0, 0).spawn(g);
        g.setLeader(g.world.getTile(0, 1).get().unit.get(), g.human);
        g.addComputerPlayer("AI");
    }
}
