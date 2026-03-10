package net.lugocorp.kingdom.gameplay.mechanics;
import net.lugocorp.kingdom.game.Game;
import net.lugocorp.kingdom.game.model.Fate;
import net.lugocorp.kingdom.game.player.CompPlayer;
import net.lugocorp.kingdom.game.player.Player;
import net.lugocorp.kingdom.math.Coords;
import net.lugocorp.kingdom.menu.Menu;
import net.lugocorp.kingdom.menu.game.FateViewNode;
import net.lugocorp.kingdom.menu.structure.ListNode;
import net.lugocorp.kingdom.menu.structure.RowNode;
import net.lugocorp.kingdom.menu.structure.SpacerNode;
import net.lugocorp.kingdom.menu.text.ButtonNode;
import net.lugocorp.kingdom.menu.text.HeaderNode;
import net.lugocorp.kingdom.menu.text.NakedButtonNode;
import net.lugocorp.kingdom.ui.views.GameView;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * This utility class handles logic surrounding the Fates system
 */
public class Fates {

    /**
     * Returns a List of all Fates in the Game
     */
    public List<Fate> getFates(Game g) {
        final Set<String> stratifiers = g.events.fate.getStratifiers();
        final List<Fate> fates = new ArrayList<>();
        for (String name : stratifiers) {
            fates.add(g.generator.fate(name));
        }
        return fates;
    }

    /**
     * Returns a random Fate
     */
    public Fate chooseRandomFate(Game g) {
        final List<Fate> fates = this.getFates(g);
        return fates.get((int) Math.floor(Math.random() * fates.size()));
    }

    /**
     * Returns a Menu so the player can view their own Fate
     */
    public Menu getViewFatesMenu(GameView view, Player p) {
        final int width = Coords.SIZE.x - (Mechanics.MENU_MARGIN * 2);
        final List<CompPlayer> comps = view.game.comps;
        final Player next = p.isHumanPlayer()
                ? comps.get(0)
                : (p == comps.get(comps.size() - 1) ? view.game.human : comps.get(comps.indexOf(p) + 1));
        return new Menu(Mechanics.MENU_MARGIN, view.hud.top.getHeight(), width, false, new ListNode()
                .add(new RowNode().add(new NakedButtonNode(view.av, "x", () -> view.hud.popups.complete()))
                        .add(new HeaderNode(view.av,
                                p.isHumanPlayer() ? "Your fate" : String.format("%s's fate", p.name)))
                        .add(new ButtonNode(view.av,
                                next.isHumanPlayer() ? "View your fate" : String.format("View %s's fate", next.name),
                                () -> view.hud.popups.replaceUnrequired(this.getViewFatesMenu(view, next)))))
                .add(new SpacerNode()).add(new FateViewNode(view.av, p.getFate(), false)));
    }
}
