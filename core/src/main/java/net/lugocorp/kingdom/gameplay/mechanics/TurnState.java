package net.lugocorp.kingdom.gameplay.mechanics;

/**
 * This class represents the different states a Turn can be in
 */
public enum TurnState {
    // The Game is setting up the current Player's turn
    TRANSITION,

    // The Player is acting now
    ACTIVE;
}
