package net.lugocorp.kingdom.ai;

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
    public Decision getDecision(Actor actor, DecisionChannel channel) {
        final Decision d = this.makeDecision(actor, channel);
        if (d.priority.value > this.highestPriorityThisTurn.value) {
            this.highestPriorityThisTurn = d.priority;
        }
        return d;
    }

    /**
     * Generates a Decision for the given Actor and DecisionChannel
     */
    protected abstract Decision makeDecision(Actor actor, DecisionChannel channel);
}
