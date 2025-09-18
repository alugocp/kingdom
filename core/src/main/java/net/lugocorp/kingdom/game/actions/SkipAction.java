package net.lugocorp.kingdom.game.actions;

/**
 * This Action represents a Unit skipping their turn
 */
public class SkipAction implements Action {
    private final boolean indefinite;

    public SkipAction(boolean indefinite) {
        this.indefinite = indefinite;
    }

    /** {@inheritdoc} */
    @Override
    public ActionType getType() {
        return ActionType.SKIP;
    }

    /** {@inheritdoc} */
    @Override
    public boolean canBeFollowedBy(ActionType a) {
        return true;
    }

    /** {@inheritdoc} */
    @Override
    public Action followedBy(Action a) {
        return a;
    }

    /** {@inheritdoc} */
    @Override
    public boolean endOfTurn() {
        return this.indefinite;
    }

    /** {@inheritdoc} */
    @Override
    public String getDescription() {
        return this.indefinite ? "This unit is skipping its turns" : "This unit is skipping its turn";
    }
}
