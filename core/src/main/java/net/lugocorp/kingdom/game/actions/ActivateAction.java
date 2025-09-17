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
    public Action followedBy(Action a) {
        return this;
    }

    /** {@inheritdoc} */
    @Override
    public boolean endOfTurn() {
        return false;
    }

    /** {@inheritdoc} */
    @Override
    public String getDescription() {
        return "This unit has cast a spell this turn";
    }
}
