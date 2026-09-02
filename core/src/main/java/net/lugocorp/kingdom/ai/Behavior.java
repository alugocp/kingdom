package net.lugocorp.kingdom.ai;

/**
 * Behaviors are objects that can live for one or more turns and direct a
 * DecisionChannel's actions during that lifetime
 */
public abstract class Behavior {

    /**
     * Runs the actual logic that governs this Behavior
     */
    public abstract void act();

    /**
     * Returns true when this Behavior's lifetime has expired
     */
    public abstract boolean isFinished();
}
