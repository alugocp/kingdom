package net.lugocorp.kingdom.gameplay.actions;
import net.lugocorp.kingdom.builtin.Events;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.ui.views.GameView;

/**
 * This Action represents an Ability being activated
 */
public class ActivateAction implements Action {
    private int remaining;

    public ActivateAction(GameView view, Unit unit) {
        final Events.GetMaxActivationsEvent e = new Events.GetMaxActivationsEvent(unit);
        unit.handleEvent(view, e); // Don't need execute() here
        this.remaining = e.max - 1;
    }

    // Should only be used in conjunction with the Kryo system
    public ActivateAction() {
        this.remaining = 0;
    }

    /** {@inheritdoc} */
    @Override
    public ActionType getType() {
        return ActionType.ACTIVATE;
    }

    /** {@inheritdoc} */
    @Override
    public boolean canBeFollowedBy(ActionType a) {
        return this.remaining > 0 && a == ActionType.ACTIVATE;
    }

    /** {@inheritdoc} */
    @Override
    public Action followedBy(Action a) {
        this.remaining--;
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
        return this.remaining == 0
                ? "This unit has cast its max number of spells for this turn"
                : String.format("This unit can cast %d more spells this turn", this.remaining);
    }
}
