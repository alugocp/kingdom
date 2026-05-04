package net.lugocorp.kingdom.game.layers;
import net.lugocorp.kingdom.game.player.Player;
import java.util.Optional;

/**
 * Interface for classes that can be associated with a Player
 */
public interface Governable {

    /**
     * Returns the Player associated with this instance, if there is one
     */
    public Optional<Player> getLeader();
}
