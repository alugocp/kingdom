package net.lugocorp.kingdom.game.unit;
import net.lugocorp.kingdom.game.model.Fate;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.game.player.Player;
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

    // This is for Kryo purposes only
    public Leadership() {
        this.unit = null;
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
}
