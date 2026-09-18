package net.lugocorp.kingdom.ai;
import net.lugocorp.kingdom.builtin.Events;
import net.lugocorp.kingdom.game.layers.Entity;
import net.lugocorp.kingdom.game.model.Ability;
import net.lugocorp.kingdom.game.model.Item;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.gameplay.combat.Damage;
import net.lugocorp.kingdom.gameplay.events.Event;
import net.lugocorp.kingdom.math.Path;
import net.lugocorp.kingdom.math.Point;
import net.lugocorp.kingdom.prediction.EventLog;
import net.lugocorp.kingdom.prediction.Prediction;
import net.lugocorp.kingdom.prediction.SelectionTree;
import net.lugocorp.kingdom.ui.views.GameView;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * This class contains helper methods to analyze consequences for the CompPlayer
 */
public class Analysis {
    private final ActorState state;

    public Analysis(ActorState state) {
        this.state = state;
    }

    /**
     * Runs the given function with a fake/temporary/hypothetical Point for the
     * given Unit's position
     */
    private <T> T spoofUnitPosition(Unit u, Point p, Supplier<T> func) {
        final int x = u.getX();
        final int y = u.getY();
        u.setX(p.x);
        u.setY(p.y);
        final T t = func.get();
        u.setX(x);
        u.setY(y);
        return t;
    }

    /**
     * Makes a Prediction for each possible selection Path after activating the
     * given Ability, then returns the highest Priority Path
     */
    public Optional<Prioritized<Path>> activeAbility(GameView view, Ability ability,
            Function<Prediction, Priority> prioritize) {
        final Consideration<Path> consideration = new Consideration<>();
        final SelectionTree tree = this.state.deliberate();
        ability.activate(view);
        tree.iteratePredictions((Prediction prediction) -> {
            final Priority priority = prioritize.apply(prediction);
            consideration.consider(priority, prediction.path);
        });
        return consideration.getState();
    }

    /**
     * Calls activeAbility() but with a hypothetical position for the Unit
     */
    public Optional<Prioritized<Path>> activeAbility(GameView view, Ability ability, Point p,
            Function<Prediction, Priority> prioritize) {
        return this.spoofUnitPosition(ability.wielder, p, () -> this.activeAbility(view, ability, prioritize));
    }

    /**
     * Predicts the outcome of the given Event on the given Ability, and assigns a
     * Priority to the outcome
     */
    public Priority passiveAbility(GameView view, Ability ability, Event event,
            Function<List<Event>, Priority> prioritize) {
        final int handle = EventLog.getHandle();
        ability.handleEvent(view, event);
        return prioritize.apply(EventLog.getEvents(handle));
    }

    /**
     * Calls passiveAbility() but with a hypothetical position for the Unit
     */
    public Priority passiveAbility(GameView view, Ability ability, Event event, Point p,
            Function<List<Event>, Priority> prioritize) {
        return this.spoofUnitPosition(ability.wielder, p, () -> this.passiveAbility(view, ability, event, prioritize));
    }

    /**
     * Makes a Prediction for what will happen if the given Unit consumes the given
     * Item
     */
    public Priority itemConsumption(GameView view, Unit unit, Item item, Function<List<Event>, Priority> prioritize) {
        final int handle = EventLog.getHandle();
        item.handleEvent(view, new Events.ItemConsumedEvent(item, unit));
        return prioritize.apply(EventLog.getEvents(handle));
    }

    /**
     * Makes a Prediction for what will happen if the given Unit attacks the given
     * target Entity
     */
    public Priority attack(GameView view, Unit attacker, Damage damage, Entity target,
            Function<List<Event>, Priority> prioritize) {
        final int handle = EventLog.getHandle();
        attacker.combat.attack(view, target, damage);
        return prioritize.apply(EventLog.getEvents(handle));
    }
}
