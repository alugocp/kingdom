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
     * Run this when the Action is logged in an empty spot
     */
    public default void addedFirst() {
        // No-op
    }

    /**
     * Returns which Action to save when this Action is followed by the given Action
     */
    public Action followedBy(Action a);

    /**
     * Runs this method at the end of the turn (returns true if this Action should
     * remain active)
     */
    public boolean endOfTurn();

    /**
     * Returns a String explaining this Action to the player
     */
    public String getDescription();
}
