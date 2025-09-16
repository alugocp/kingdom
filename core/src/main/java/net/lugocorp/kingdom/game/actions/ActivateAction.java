package net.lugocorp.kingdom.game.actions;

/**
 * This Action represents an Ability being activated
 */
public class ActivateAction implements Action {

    /** {@inheritdoc} */
    @Override
    public ActionType getType() {
        return ActionType.ACTIVATE;
    }

    /** {@inheritdoc} */
    @Override
    public boolean canBeFollowedBy(ActionType type) {
        return false;
    }

    /** {@inheritdoc} */
    @Override
    public boolean nextTurnStart() {
        return false;
    }

    /** {@inheritdoc} */
    @Override
    public String getDescription() {
        return "This unit has cast a spell this turn";
    }
}
