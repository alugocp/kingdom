package net.lugocorp.kingdom.game.model;
import net.lugocorp.kingdom.game.combat.HitPoints;
import net.lugocorp.kingdom.ui.menu.HeaderNode;
import net.lugocorp.kingdom.ui.menu.ListNode;
import net.lugocorp.kingdom.ui.menu.MenuNode;
import net.lugocorp.kingdom.ui.menu.TextNode;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.math.Point;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Represents a local spirit that Players can compete for favor with
 */
public class Patron extends Building {
    private Map<Player, Integer> favor = new HashMap<>();
    private Set<Point> domain = new HashSet<>();

    Patron(String name, int x, int y) {
        super(name, x, y);
        this.setHealth(new Patron.FavorPoints(this));
        this.obstacle = true;
    }

    /** {@inheritdoc} */
    @Override
    public MenuNode getMenuContent(GameView view, Optional<Point> p) {
        Optional<Player> leader = p.flatMap((Point p1) -> view.game.world.getTile(p1.x, p1.y))
                .flatMap((Tile t) -> t.leader);
        ListNode node = new ListNode().add(new HeaderNode(view.game.graphics, this.name))
                .add(new TextNode(view.game.graphics, this.desc));
        return node;
    }

    /**
     * This nested class implements differences in how Patrons handle attacks/heals
     * than other Buildings
     */
    private static class FavorPoints extends HitPoints<Building> {
        private FavorPoints(Patron bearer) {
            super(bearer);
            this.invulnerable();
        }
    }
}
