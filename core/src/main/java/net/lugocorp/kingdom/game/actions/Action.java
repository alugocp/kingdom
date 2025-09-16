package net.lugocorp.kingdom.game.actions;

/**
 * Represents an Action that a Unit can take in a turn (selected from the UI)
 */
public interface Action {

    /**
     * Returns a descriptor for this particular Action
     */
    public ActionType getType();

    /**
     * Returns true if an Action of the given type can follow this Action in the
     * same turn
     */
    public boolean canBeFollowedBy(ActionType type);

    /**
     * Runs this method at the beginning of the next turn (returns true if this
     * Action should stay in play)
     */
    public boolean nextTurnStart();

    /**
     * Returns a String explaining this Action to the player
     */
    public String getDescription();
}
