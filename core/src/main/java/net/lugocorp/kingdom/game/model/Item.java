package net.lugocorp.kingdom.game.model;
import net.lugocorp.kingdom.game.events.Event;
import net.lugocorp.kingdom.game.events.EventReceiver;
import net.lugocorp.kingdom.game.model.fields.Rarity;
import net.lugocorp.kingdom.game.model.fields.Tags;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.SideEffect;
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

    /**
     * This should only be used in conjunction with Kryo rehydration
     */
    public Item() {
        this.name = null;
    }

    /** {@inheritdoc} */
    @Override
    public SideEffect handleEventWithoutSignalBooster(GameView view, Event e) {
        return view.game.events.item.handle(view, this, e);
    }

    /** {@inheritdoc} */
    @Override
    public String getStratifier() {
        return this.name;
    }
}
