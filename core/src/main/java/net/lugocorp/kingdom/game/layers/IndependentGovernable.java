package net.lugocorp.kingdom.game.layers;
import net.lugocorp.kingdom.game.player.Player;
import java.util.Optional;

/**
 * A Governable that can independently set its Leader
 */
public interface IndependentGovernable extends Governable {
    /**
     * Sets the Player associated with this IndependentGovernable
     */
    public void setLeader(Optional<Player> leader);
}
