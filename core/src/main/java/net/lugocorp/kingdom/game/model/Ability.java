package net.lugocorp.kingdom.game.model;
import net.lugocorp.kingdom.builtin.Events;
import net.lugocorp.kingdom.game.events.Event;
import net.lugocorp.kingdom.game.events.EventReceiver;
import net.lugocorp.kingdom.ui.MenuNode;
import net.lugocorp.kingdom.ui.MenuSubject;
import net.lugocorp.kingdom.ui.nodes.ActionNode;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.code.SideEffect;
import net.lugocorp.kingdom.utils.math.Point;
import java.util.Optional;

/**
 * A passive or active effect that Units, Buildings and Tiles can use
 */
public class Ability implements EventReceiver, MenuSubject {
    public final Unit wielder;
    public final String name;
    public String desc = "";

    Ability(Unit wielder, String name) {
        this.wielder = wielder;
        this.name = name;
    }

    /**
     * This should only be used in conjunction with Kryo rehydration
     */
    public Ability() {
        this.wielder = null;
        this.name = null;
    }

    /**
     * Activates this Ability (only does something if it's active)
     */
    public SideEffect activate(GameView view) {
        return this.handleEvent(view, new Events.AbilityActivatedEvent(this));
    }

    /**
     * Returns true fi this Ability can be activated
     */
    public boolean isActive(GameView view) {
        return this.hasEventHandler(view, "AbilityActivatedEvent");
    }

    /**
     * Returns true if this Ability has an EventHandler for the given channel
     */
    public boolean hasEventHandler(GameView view, String channel) {
        return view.game.events.ability.hasEventHandler(this.getStratifier(), channel);
    }

    /** {@inheritdoc} */
    @Override
    public SideEffect handleEventWithoutSignalBooster(GameView view, Event e) {
        return view.game.events.ability.handle(view, this, e);
    }

    /** {@inheritdoc} */
    @Override
    public String getStratifier() {
        return this.name;
    }

    /** {@inheritdoc} */
    @Override
    public MenuNode getMenuContent(GameView view, Optional<Point> p) {
        return new ActionNode(view, this.name, Optional.of(this.desc), this.wielder.belongsToHuman()
                && this.isActive(view) && !view.game.mechanics.turns.hasUnitActed(this.wielder), () -> {
                    this.activate(view).execute();
                    view.menu.refresh(true);
                });
    }
}
