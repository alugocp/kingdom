package net.lugocorp.kingdom.game.world;
import net.lugocorp.kingdom.game.Game;
import net.lugocorp.kingdom.game.model.Glyph;
import net.lugocorp.kingdom.game.model.Player;
import net.lugocorp.kingdom.ui.views.GameView;
import java.util.Optional;

/**
 * This class handles world generation logic
 */
public class WorldGenerator {

    /**
     * The main function that initiates world generation
     */
    public void generateWorld(GameView view) {
        Game g = view.game;
        for (int x = 0; x < g.world.getWidth(); x++) {
            for (int y = 0; y < g.world.getHeight(); y++) {
                if (y <= 1 && x >= 4 && x <= 7) {
                    if (y == 1) {
                        g.generator.tile("Water", x, y).spawn(view);
                    } else {
                        g.generator.tile("Rock", x, y).spawn(view);
                    }
                } else {
                    g.generator.tile("Grassland", x, y).spawn(view);
                }
                g.world.getTile(x, y).get().glyph = Optional.of(Glyph.random());
            }
        }
        Player ai = g.addComputerPlayer("AI");
        g.getInitialUnit(g.human, 1, 1, Glyph.BATTLE).spawn(view);
        g.generator.building("Vault", 1, 1).spawn(view);
        g.generator.unit("Crystal", 8, 4).spawn(view);
        g.generator.unit("Crystal", 6, 3).spawn(view);
        g.generator.unit("Blob", 8, 1).spawn(view);
        g.generator.building("Vault", 8, 4).spawn(view);
        g.generator.building("Mine", 3, 1).spawn(view);
        g.generator.building("Mine", 3, 3).spawn(view);
        g.generator.building("Forest", 5, 3).spawn(view);
        g.generator.building("Forest", 6, 3).spawn(view);
        g.generator.building("Forest", 5, 4).spawn(view);
        g.generator.building("Forest", 0, 0).spawn(view);
        g.generator.building("Forest", 0, 3).spawn(view);
        g.generator.building("Forest", 1, 3).spawn(view);
        g.generator.building("Forest", 0, 4).spawn(view);
        g.generator.building("Forest", 1, 4).spawn(view);
        g.generator.building("Forest", 2, 4).spawn(view);
        g.generator.building("Mine", 0, 9).spawn(view);
        g.generator.patron("Test Patron", 4, 2).spawn(view);
        g.setLeader(g.world.getTile(8, 4).get().unit.get(), ai);
        g.setLeader(g.world.getTile(6, 3).get().unit.get(), ai);
        g.setLeader(g.world.getTile(8, 1).get().unit.get(), ai);
    }
}
