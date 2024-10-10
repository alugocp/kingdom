package net.lugocorp.kingdom.game.mechanics;
import net.lugocorp.kingdom.game.model.Tile;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.ui.Hud;
import net.lugocorp.kingdom.ui.menu.ButtonNode;
import net.lugocorp.kingdom.ui.menu.ListNode;
import net.lugocorp.kingdom.ui.menu.Menu;
import net.lugocorp.kingdom.ui.menu.RowNode;
import net.lugocorp.kingdom.ui.views.GameView;
import com.badlogic.gdx.Gdx;
import java.util.Optional;

/**
 * This class manages the logic for new Unit acquisition
 */
public class NewUnit {
    public static final int MAX_UNIT_POINTS = 100;

    /**
     * Returns the number of unit points that a Player should get each turn
     */
    public static int getUnitPointsYield(int bareTiles, int tiles) {
        return (int) Math.floor(20f * bareTiles / tiles);
    }

    /**
     * Instantiates a popup Menu to handle the new Unit acquisition UI
     */
    public static Menu getNewUnitMenu(GameView view) {
        // TODO pick a random point to spawn the Unit before this point
        // TODO randomly select the Unit options here
        Unit u1 = view.game.generator.unit("Crystal", 0, 0);
        Unit u2 = view.game.generator.unit("Crystal", 0, 0);
        Unit u3 = view.game.generator.unit("Crystal", 0, 0);
        ListNode node = new ListNode().add(new ButtonNode(view.game.graphics, "x", () -> view.popups.setDisplay(false)))
                .add(new RowNode().add(u1.getMenuContent(view, 0, 0)).add(u2.getMenuContent(view, 0, 0))
                        .add(u3.getMenuContent(view, 0, 0)))
                .add(new RowNode().add(new ButtonNode(view.game.graphics, "Choose", () -> NewUnit.choose(view, u1)))
                        .add(new ButtonNode(view.game.graphics, "Choose", () -> NewUnit.choose(view, u2)))
                        .add(new ButtonNode(view.game.graphics, "Choose", () -> NewUnit.choose(view, u3))));
        return new Menu(Hud.BUTTON_WIDTH, Hud.HEIGHT, Gdx.graphics.getWidth() - (Hud.BUTTON_WIDTH * 2), false, node);
    }

    /**
     * Completes the associated popup Menu and spawns a new Unit in the World
     */
    private static void choose(GameView view, Unit u) {
        view.game.human.unitPoints -= NewUnit.MAX_UNIT_POINTS;
        view.popups.complete();
        view.game.world.getTile(u.getX(), u.getY()).ifPresent((Tile t) -> {
            if (t.unit.isPresent()) {
                view.logger.log("Cannot spawn unit on occupied tile");
            } else {
                t.unit = Optional.of(u);
                view.game.setLeader(u, view.game.human);
            }
        });
    }
}
