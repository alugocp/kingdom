package net.lugocorp.kingdom.gameplay.actions;
import java.util.function.Supplier;

/**
 * This Action represents a Unit skipping their turn
 */
public class SkipAction implements Action {
    private final Supplier<Boolean> readyToDrop;
    private final String label;

    public SkipAction(String label, Supplier<Boolean> readyToDrop) {
        this.readyToDrop = readyToDrop;
        this.label = label;
    }

    // Should only be used in conjunction with the Kryo system
    public SkipAction() {
        this.readyToDrop = null;
        this.label = "";
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
        return !this.readyToDrop.get();
    }

    /** {@inheritdoc} */
    @Override
    public String getDescription() {
        return this.label;
    }
}
