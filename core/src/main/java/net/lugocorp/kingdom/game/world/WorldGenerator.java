package net.lugocorp.kingdom.game.world;
import net.lugocorp.kingdom.game.Game;
import net.lugocorp.kingdom.game.model.Player;

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
        Player ai = g.addComputerPlayer("AI");
        g.generator.unit("Axolotl", 1, 1).spawn(g);
        g.generator.building("Vault", 1, 1).spawn(g);
        g.generator.unit("Crystal", 8, 4).spawn(g);
        g.generator.building("Vault", 8, 4).spawn(g);
        g.generator.building("Mine", 3, 1).spawn(g);
        g.generator.building("Mine", 3, 3).spawn(g);
        g.generator.building("Forest", 5, 3).spawn(g);
        g.generator.building("Forest", 6, 3).spawn(g);
        g.generator.building("Forest", 5, 4).spawn(g);
        g.generator.building("Forest", 0, 0).spawn(g);
        g.generator.building("Forest", 0, 3).spawn(g);
        g.generator.building("Forest", 1, 3).spawn(g);
        g.generator.building("Forest", 0, 4).spawn(g);
        g.generator.building("Forest", 1, 4).spawn(g);
        g.generator.building("Forest", 2, 4).spawn(g);
        g.generator.building("Mine", 0, 9).spawn(g);
        g.setLeader(g.world.getTile(1, 1).get().unit.get(), g.human);
        g.setLeader(g.world.getTile(8, 4).get().unit.get(), ai);
    }
}
