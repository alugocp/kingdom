package net.lugocorp.kingdom.game.model;
import net.lugocorp.kingdom.builtin.Events;
import net.lugocorp.kingdom.game.properties.Rarity;
import net.lugocorp.kingdom.game.properties.Tags;
import net.lugocorp.kingdom.gameplay.events.Event;
import net.lugocorp.kingdom.gameplay.events.EventReceiver;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.SideEffect;
import com.badlogic.gdx.math.Matrix4;
import java.util.Optional;

/**
 * An in-game pickup to be used by Units
 */
public class Item implements EventReceiver {
    private Optional<Matrix4> recolor = Optional.empty();
    public final Tags tags = new Tags();
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
     * Gets this Item's recolor matrix
     */
    public Optional<Matrix4> getRecolor() {
        return this.recolor;
    }

    /**
     * Sets this Item's recolor matrix
     */
    public void setRecolor(Optional<Matrix4> recolor) {
        this.recolor = recolor;
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
