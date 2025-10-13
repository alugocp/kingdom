package net.lugocorp.kingdom.game.unit;
import net.lugocorp.kingdom.game.model.Fate;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.game.player.Player;
import net.lugocorp.kingdom.ui.views.GameView;
import java.util.Optional;

/**
 * This class handles logic surrounding a Unit's leading Player
 */
public class Leadership {
    private final Unit unit;
    private Optional<Player> leader = Optional.empty();

    public Leadership(Unit unit) {
        this.unit = unit;
    }

    /**
     * Returns this instance's leading Player
     */
    public Optional<Player> getLeader() {
        return this.leader;
    }

    /**
     * Sets the Player that commands this instance (this should only ever be used in
     * the Game class)
     */
    public void setLeader(Optional<Player> leader) {
        this.leader = leader;
    }

    /**
     * Returns true if this Unit is leaderless and can be picked up by anyone
     */
    public boolean isFreeRadical() {
        return !this.leader.isPresent() && this.unit.loyalty.get() == 0;
    }

    /**
     * Returns true if this Unit belongs to the human Player
     */
    public boolean belongsToHuman() {
        return this.leader.map((Player p) -> p.isHumanPlayer()).orElse(false);
    }

    /**
     * Returns true if this Unit belongs to the given Player
     */
    public boolean belongsToPlayer(Player p) {
        return this.leader.map((Player p1) -> p.equals(p1)).orElse(false);
    }

    /**
     * Returns true if this instance has the same Player as the given Unit
     */
    public boolean sameLeader(Unit u) {
        return this.getLeader().equals(u.leadership.getLeader());
    }

    /**
     * Returns true if this instance's Player has the given Fate instance
     */
    public boolean hasFate(Fate fate) {
        return this.leader.map((Player p) -> p.getFate() == fate).orElse(false);
    }

    /**
     * Recruits this Unit into to a new Player
     */
    public void recruit(GameView view, Player player) {
        if (!this.isFreeRadical()) {
            throw new RuntimeException("Cannot recruit another player's unit");
        }
        view.game.setLeader(view, this.unit, player);
        this.unit.hunger.eat(view, true);
        this.unit.loyalty.reset(view);
    }
}
