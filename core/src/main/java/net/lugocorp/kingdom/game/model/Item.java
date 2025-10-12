package net.lugocorp.kingdom.game.model;
import net.lugocorp.kingdom.builtin.Events;
import net.lugocorp.kingdom.game.events.Event;
import net.lugocorp.kingdom.game.events.EventReceiver;
import net.lugocorp.kingdom.game.properties.Rarity;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.code.SideEffect;
import java.util.Optional;

/**
 * An in-game pickup to be used by Units
 */
public class Item implements EventReceiver {
    private Optional<String> tag = Optional.empty();
    public final String name;
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

    /**
     * Returns this Item's tag (if it has one)
     */
    public Optional<String> getTag() {
        return this.tag;
    }

    /**
     * Sets this Item's tag
     */
    public void setTag(String tag) {
        this.tag = Optional.of(tag);
    }

    /**
     * Returns true if this Item has the given tag
     */
    public boolean hasTag(String tag) {
        return this.tag.map((String t) -> tag.equals(t)).orElse(false);
    }

    /**
     * Returns true if this Item can be consumed
     */
    public boolean isConsumable(GameView view) {
        return view.game.events.item.hasEventHandler(this.getStratifier(), Events.ItemConsumedEvent.class);
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
