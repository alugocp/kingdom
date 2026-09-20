package net.lugocorp.kingdom.ai.behaviors;
import net.lugocorp.kingdom.ai.Behavior;
import net.lugocorp.kingdom.ai.Prioritized;
import net.lugocorp.kingdom.ai.Priority;
import net.lugocorp.kingdom.game.layers.Entity;
import net.lugocorp.kingdom.game.model.Ability;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.game.player.CompPlayer;
import net.lugocorp.kingdom.gameplay.actions.SkipAction;
import net.lugocorp.kingdom.math.Hexagons;
import net.lugocorp.kingdom.math.MutablePoint;
import net.lugocorp.kingdom.math.Path;
import net.lugocorp.kingdom.math.Point;
import net.lugocorp.kingdom.pathfinding.Pathfinder;
import net.lugocorp.kingdom.prediction.Prediction;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.Chooser;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * This Behavior tells the given Unit to follow and cast some Ability(ies)
 * against the given target
 */
public class FollowAndCastBehavior implements Behavior {
    private final Function<Prediction, Boolean> criteria;
    private final Set<Ability> abilities = new HashSet<>();
    private final Supplier<Boolean> shouldKill;
    private final CompPlayer player;
    private final Entity target;
    private final Unit unit;
    private Optional<Path> selection = Optional.empty();
    private MutablePoint dest = new MutablePoint(-1, -1);
    private boolean kill = false;

    public FollowAndCastBehavior(CompPlayer player, Unit unit, Entity target, Supplier<Boolean> shouldKill,
            Function<Prediction, Boolean> criteria) {
        this.shouldKill = shouldKill;
        this.criteria = criteria;
        this.player = player;
        this.target = target;
        this.unit = unit;
    }

    /** {@inheritdoc} */
    @Override
    public Optional<Point> getSelection() {
        return this.selection.flatMap((Path p) -> p.isEmpty() ? Optional.empty() : Optional.of(p.popFromFront()));
    }

    /** {@inheritdoc} */
    @Override
    public void act(GameView view) {
        // Recalculate if the target has moved (or if this is our first turn with the
        // Behavior)
        if (!this.dest.equals(this.target.getPoint())) {
            this.recalculate(view);
        }

        // Check if we can use either Ability on the target
        boolean cast = false;
        for (Ability ability : this.abilities) {
            if (this.isWithinRange(view, ability)) {
                // TODO double check that we can activate this Ability
                ability.activate(view).execute();
                cast = true;
            }
        }

        // Move closer to the target otherwise
        if (!cast) {
            final Pathfinder pathfinder = new Pathfinder(this.unit);
            final List<Point> path = pathfinder.getPath(view, this.dest.toPoint());
            if (path.size() == 0) {
                this.kill = true;
                return;
            }

            // Move towards the destination
            this.unit.movement.move(view, path, true).execute();
            view.game.actions.unitHasActed(view, this.unit,
                    new SkipAction("This unit has already moved this turn", () -> true));
        }
    }

    /** {@inheritdoc} */
    @Override
    public boolean isFinished(GameView view) {
        return this.kill || this.shouldKill.get();
    }

    /**
     * Returns true if the Unit is within range to use the given Ability on the
     * target
     */
    private boolean isWithinRange(GameView view, Ability ability) {
        this.player.actor.state.deliberate();
        final Optional<Prioritized<Path>> result = this.player.actor.analyze.activeAbility(view, ability,
                (Prediction prediction) -> this.criteria.apply(prediction) ? Priority.GOOD_IDEA : Priority.FATAL);
        this.player.actor.state.enact(this);
        if (result.map((Prioritized<Path> prioritized) -> prioritized.priority != Priority.FATAL).orElse(false)) {
            this.selection = Optional.of(result.get().value);
        }
        return false;
    }

    /**
     * Recalculates the destination Point and relevant Abilities
     */
    private void recalculate(GameView view) {
        this.player.actor.state.deliberate();
        this.abilities.clear();
        final Chooser<Point> closest = new Chooser<>((Integer old, Integer next) -> next < old);
        for (Ability ability : this.unit.abilities.getActives()) {
            final Set<Point> options = Hexagons.getNeighbors(this.target.getPoint(), 6);
            for (Point p : options) {
                final Optional<Prioritized<Path>> result = this.player.actor.analyze.activeAbility(view, ability, p,
                        (Prediction prediction) -> this.criteria.apply(prediction)
                                ? Priority.GOOD_IDEA
                                : Priority.FATAL);
                if (result.map((Prioritized<Path> prioritized) -> prioritized.priority != Priority.FATAL)
                        .orElse(false)) {
                    closest.choice(p, p.distance(this.unit.getPoint()));
                    if (!this.abilities.contains(ability)) {
                        this.abilities.add(ability);
                    }
                }
            }
        }
        this.player.actor.state.enact(this);
        if (closest.has()) {
            final Point choice = closest.get();
            this.dest.set(choice.x, choice.y);
        } else {
            this.kill = true;
        }
    }
}
