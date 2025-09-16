package net.lugocorp.kingdom.game.actions;

/**
 * This Action represents a Unit skipping their turn
 */
public class SkipAction implements Action {

    /** {@inheritdoc} */
    @Override
    public ActionType getType() {
        return ActionType.SKIP;
    }

    /** {@inheritdoc} */
    @Override
    public boolean canBeFollowedBy(ActionType type) {
        return true;
    }

    /** {@inheritdoc} */
    @Override
    public boolean nextTurnStart() {
        return false;
    }
}
