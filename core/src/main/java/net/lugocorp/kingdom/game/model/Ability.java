package net.lugocorp.kingdom.game.model;
import net.lugocorp.kingdom.game.core.Events.AbilityActivatedEvent;
import net.lugocorp.kingdom.game.events.Event;
import net.lugocorp.kingdom.game.events.EventReceiver;
import net.lugocorp.kingdom.ui.menu.ActionNode;
import net.lugocorp.kingdom.ui.menu.MenuNode;
import net.lugocorp.kingdom.ui.menu.MenuSubject;
import net.lugocorp.kingdom.ui.views.GameView;
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

    /** {@inheritdoc} */
    @Override
    public void handleEventWithoutSignalBooster(GameView view, Event e) {
        view.game.events.ability.handle(view, this, e);
    }

    /** {@inheritdoc} */
    @Override
    public String getStratifier() {
        return this.name;
    }

    /** {@inheritdoc} */
    @Override
    public MenuNode getMenuContent(GameView view, Optional<Point> p) {
        return new ActionNode(view.av, this.name, this.desc,
                this.wielder.leader.map((Player p1) -> p1.isHumanPlayer()).orElse(false)
                        && view.game.events.ability.hasEventHandler(this.getStratifier(), "AbilityActivatedEvent")
                        && !view.game.mechanics.turns.hasUnitActed(this.wielder),
                () -> {
                    this.handleEvent(view, new AbilityActivatedEvent(this));
                    view.menu.refresh(true);
                });
    }
}
