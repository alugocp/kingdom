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
    public Action followedBy(Action a) {
        return a;
    }

    /** {@inheritdoc} */
    @Override
    public boolean endOfTurn() {
        return false;
    }

    /** {@inheritdoc} */
    @Override
    public String getDescription() {
        return "This unit is skipping its turn";
    }
}
