package net.lugocorp.kingdom.game.actions;

/**
 * This Action represents a Unit moving
 */
public class MoveAction implements Action {
    private final int max;
    private int distance;

    public MoveAction(int distance, int max) {
        this.distance = distance;
        this.max = max;
    }

    /**
     * Returns the distance associated with this Action
     */
    int getDistance() {
        return this.distance;
    }

    /**
     * Adds to this Action's recorded distance
     */
    void addDistance(int distance) {
        this.distance += distance;
    }

    /**
     * Returns true if this Action has already moved the maximum amount
     */
    boolean isFinished() {
        return this.distance == this.max;
    }

    /** {@inheritdoc} */
    @Override
    public ActionType getType() {
        return ActionType.MOVE;
    }

    /** {@inheritdoc} */
    @Override
    public boolean canBeFollowedBy(ActionType type) {
        return type == ActionType.ACTIVATE ? false : !this.isFinished();
    }

    /** {@inheritdoc} */
    @Override
    public boolean nextTurnStart() {
        return false;
    }
}
