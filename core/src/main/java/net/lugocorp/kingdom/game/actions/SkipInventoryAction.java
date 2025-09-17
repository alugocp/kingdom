package net.lugocorp.kingdom.game.actions;
import net.lugocorp.kingdom.game.properties.Inventory;

/**
 * This Action represents a Unit skipping their turn
 */
public class SkipInventoryAction implements Action {
    private final Inventory haul;

    public SkipInventoryAction(Inventory haul) {
        this.haul = haul;
    }

    /** {@inheritdoc} */
    @Override
    public ActionType getType() {
        return ActionType.SKIP_INVENTORY;
    }

    /** {@inheritdoc} */
    @Override
    public Action followedBy(Action a) {
        return a;
    }

    /** {@inheritdoc} */
    @Override
    public boolean endOfTurn() {
        return !this.haul.isFull();
    }

    /** {@inheritdoc} */
    @Override
    public String getDescription() {
        return "This unit is waiting to fill its haul inventory";
    }
}
