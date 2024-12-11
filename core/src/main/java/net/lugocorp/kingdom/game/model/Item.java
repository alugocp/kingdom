package net.lugocorp.kingdom.game.model;
import net.lugocorp.kingdom.game.events.Event;
import net.lugocorp.kingdom.game.events.EventReceiver;
import net.lugocorp.kingdom.ui.views.GameView;
import java.util.Optional;

/**
 * An in-game pickup to be used by Units
 */
public class Item implements EventReceiver {
    public final String name;
    public final Tags tags = new Tags();
    public Optional<String> icon = Optional.empty();
    public Rarity rarity = Rarity.COMMON;
    public String desc = "";
    public int gold = 0;

    Item(String name) {
        this.name = name;
    }

    /** {@inheritdoc} */
    @Override
    public void handleEventWithoutSignalBooster(GameView view, Event e) {
        view.game.events.item.handle(view, this, e);
    }

    /** {@inheritdoc} */
    @Override
    public String getStratifier() {
        return this.name;
    }

    /**
     * This nested class tracks relative chance to spawn Items
     */
    public static enum Rarity {
        COMMON("common"), UNCOMMON("uncommon"), RARE("rare");

        public final String label;

        private Rarity(String label) {
            this.label = label;
        }

        @Override
        public String toString() {
            return this.label;
        }
    };
}
