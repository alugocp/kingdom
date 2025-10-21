package net.lugocorp.kingdom.game.model;
import net.lugocorp.kingdom.builtin.Events;
import net.lugocorp.kingdom.game.actions.ActionType;
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
        return this.hasEventHandler(view, Events.AbilityActivatedEvent.class);
    }

    /**
     * Returns true if this Ability has an EventHandler for the given channel
     */
    public <E extends Event> boolean hasEventHandler(GameView view, Class<E> eventClass) {
        // TODO wrap uses in other methods
        return view.game.events.ability.hasEventHandler(this.getStratifier(), eventClass);
    }

    /**
     * Returns true if this Ability handles the Tick channel
     */
    public boolean hasTickHandler(GameView view) {
        return view.game.events.ability.hasEventHandler(this.getStratifier(), "Tick");
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
        final boolean canUnitDoThis = view.game.actions.canUnitDoThis(this.wielder, ActionType.ACTIVATE);
        final String popup = String.format("%s (%s)%s", this.desc,
                this.isActive(view) ? "click to activate" : "passive ability",
                canUnitDoThis ? "" : " (unit has exhausted their actions this turn)");
        return new ActionNode(view, this.name, Optional.of(popup), () -> {
            this.activate(view).execute();
            view.hud.bot.tileMenu.refresh();
        }).enable(this.wielder.leadership.belongsToHuman() && this.isActive(view)
                && view.game.mechanics.turns.canHumanPlayerAct() && canUnitDoThis);
    }
}
