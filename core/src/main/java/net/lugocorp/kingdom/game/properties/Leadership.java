package net.lugocorp.kingdom.game.properties;
import net.lugocorp.kingdom.game.layers.Governable;
import net.lugocorp.kingdom.game.model.Fate;
import net.lugocorp.kingdom.game.player.Player;
import java.util.Optional;

/**
 * Utility class to help with Governables
 */
public class Leadership {
    private final Governable instance;

    public Leadership(Governable instance) {
        this.instance = instance;
    }

    // This is for Kryo purposes only
    public Leadership() {
        this.instance = null;
    }

    /**
     * Returns this instance's leading Player
     */
    public Optional<Player> get() {
        return this.instance.getLeader();
    }

    /**
     * Returns true if this Unit belongs to the human Player
     */
    public boolean belongsToHuman() {
        return this.get().map((Player p) -> p.isHumanPlayer()).orElse(false);
    }

    /**
     * Returns true if this Unit belongs to the given Player
     */
    public boolean belongsToPlayer(Optional<Player> p) {
        return this.get().equals(p);
    }

    /**
     * Calls into the other belongsToPlayer()
     */
    public boolean belongsToPlayer(Player p) {
        return this.belongsToPlayer(Optional.of(p));
    }

    /**
     * Returns true if this instance has the same Player as the given Governable
     */
    public boolean sameLeader(Governable e) {
        return this.get().equals(e.getLeader());
    }

    /**
     * Returns true if this instance's Player has the given Fate instance
     */
    public boolean hasFate(Fate fate) {
        return this.get().map((Player p) -> p.getFate() == fate).orElse(false);
    }
}
