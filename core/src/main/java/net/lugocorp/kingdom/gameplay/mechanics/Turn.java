package net.lugocorp.kingdom.gameplay.mechanics;
import net.lugocorp.kingdom.game.player.Player;
import java.util.ArrayList;
import java.util.List;

/**
 * This class stores information on the current turn
 */
public class Turn {
    private TurnState state = TurnState.TRANSITION;
    private List<Player> players = new ArrayList<>();
    private Player current = null;
    private int counter = 1;

    /**
     * Sets the Players that will each take Turns
     */
    public void setPlayers(List<Player> players) {
        this.current = players.get(0);
        this.players = players;
    }

    /**
     * Initializes a new set of turns
     */
    void next() {
        final int i = (this.players.indexOf(this.current) + 1) % this.players.size();
        this.current = this.players.get(i);
        this.state = TurnState.TRANSITION;
        if (i == 0) {
            this.counter++;
        }
    }

    /**
     * Sets this TurnState to ACTIVE
     */
    void activate() {
        this.state = TurnState.ACTIVE;
    }

    /**
     * Returns true if the first Player in our list is the Turn Player
     */
    boolean isFirstTurnPlayer() {
        return this.players.indexOf(this.current) == 0;
    }

    /**
     * Returns the current Player
     */
    public Player getPlayer() {
        return this.current;
    }

    /**
     * Returns the current TurnState
     */
    public TurnState getState() {
        return this.state;
    }

    /**
     * Returns the current Turn number
     */
    public int getCounter() {
        return this.counter;
    }
}
