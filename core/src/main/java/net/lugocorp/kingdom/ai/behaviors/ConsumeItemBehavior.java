package net.lugocorp.kingdom.ai.behaviors;
import net.lugocorp.kingdom.ai.Behavior;
import net.lugocorp.kingdom.builtin.Events;
import net.lugocorp.kingdom.game.model.Item;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.ui.views.GameView;

/**
 * This Behavior tells the given Unit to consume an Item
 */
public class ConsumeItemBehavior implements Behavior {
    private final Item item;
    private final Unit unit;

    public ConsumeItemBehavior(Unit unit, Item item) {
        this.unit = unit;
        this.item = item;
    }

    /** {@inheritdoc} */
    @Override
    public void act(GameView view) {
        if (this.unit.haul.has(this.item)) {
            final Events.ItemConsumedEvent event = new Events.ItemConsumedEvent(this.item, this.unit);
            this.item.handleEvent(view, event).execute();
            if (event.consumed) {
                this.unit.haul.remove(item);
            }
        }
    }

    /** {@inheritdoc} */
    @Override
    public boolean isFinished(GameView view) {
        return true;
    }
}
