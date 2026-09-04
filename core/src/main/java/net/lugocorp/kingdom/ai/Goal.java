package net.lugocorp.kingdom.ai;
import net.lugocorp.kingdom.game.player.CompPlayer;
import net.lugocorp.kingdom.ui.views.GameView;

/**
 * A Goal is an abstract effort that the CompPlayers strive towards
 */
public abstract class Goal {
    private Priority highestPriorityThisTurn = Priority.FATAL;

    /**
     * Resets this Goal's data for the new turn
     */
    public void reset() {
        this.highestPriorityThisTurn = Priority.FATAL;
    }

    /**
     * Returns the highest Priority value this Goal has assigned for the given turn
     */
    public Priority getHighestPriorityThisTurn() {
        return this.highestPriorityThisTurn;
    }

    /**
     * Returns a Decision for the given Actor and DecisionChannel
     */
    public Decision getDecision(GameView view, CompPlayer player, DecisionChannel channel) {
        final Decision d = this.makeDecision(view, player, channel);
        if (d.priority.value > this.highestPriorityThisTurn.value) {
            this.highestPriorityThisTurn = d.priority;
        }
        return d;
    }

    /**
     * Returns an empty Decision
     */
    protected Decision noDecision(DecisionChannel channel) {
        // We can use null here because FATAL is supposed to mean "do not act upon this
        // Decision"
        return new Decision(channel, Priority.FATAL, null);
    }

    /**
     * Generates a Decision for the given Actor and DecisionChannel
     */
    protected abstract Decision makeDecision(GameView view, CompPlayer player, DecisionChannel channel);
}
