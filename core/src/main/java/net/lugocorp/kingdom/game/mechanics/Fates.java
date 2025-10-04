package net.lugocorp.kingdom.game.mechanics;
import net.lugocorp.kingdom.game.Game;
import net.lugocorp.kingdom.game.model.Fate;
import net.lugocorp.kingdom.ui.Menu;
import net.lugocorp.kingdom.ui.nodes.FateViewNode;
import net.lugocorp.kingdom.ui.nodes.ListNode;
import net.lugocorp.kingdom.ui.nodes.NakedButtonNode;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.math.Coords;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * This utility class handles logic surrounding the Fates system
 */
public class Fates {
    private final List<Fate> fates = new ArrayList<>();

    /**
     * Generates all registered Fates at the start of the Game
     */
    public void init(Game g) {
        Set<String> stratifiers = g.events.fate.getStratifiers();
        for (String name : stratifiers) {
            this.fates.add(g.generator.fate(name));
        }
    }

    /**
     * Returns a List of all Fates in the Game
     */
    public List<Fate> getFates() {
        return this.fates;
    }

    /**
     * Returns the first registered Fate
     */
    public Fate getFirstFate() {
        return this.fates.get(0);
    }

    /**
     * Returns a random Fate
     */
    public Fate chooseRandomFate() {
        return this.fates.get((int) Math.floor(Math.random() * this.fates.size()));
    }

    /**
     * Returns a Menu so the player can view their own Fate
     */
    public Menu getPlayerFateMenu(GameView view) {
        final int width = Coords.SIZE.x - (Mechanics.MENU_MARGIN * 2);
        return new Menu(Mechanics.MENU_MARGIN, view.hud.getHeight(), width, false,
                new ListNode().add(new NakedButtonNode(view.av, "x", () -> view.popups.complete()))
                        .add(new FateViewNode(view.av, view.game.human.getFate())));
    }
}
