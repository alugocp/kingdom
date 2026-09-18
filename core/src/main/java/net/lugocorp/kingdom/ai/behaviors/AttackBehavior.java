package net.lugocorp.kingdom.ai.behaviors;
import net.lugocorp.kingdom.ai.Behavior;
import net.lugocorp.kingdom.ai.Prioritized;
import net.lugocorp.kingdom.ai.Priority;
import net.lugocorp.kingdom.builtin.Events;
import net.lugocorp.kingdom.game.layers.Entity;
import net.lugocorp.kingdom.game.model.Ability;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.game.player.CompPlayer;
import net.lugocorp.kingdom.gameplay.actions.SkipAction;
import net.lugocorp.kingdom.gameplay.events.Event;
import net.lugocorp.kingdom.math.Hexagons;
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

/**
 * This Behavior tells the given Unit to activate some Ability
 */
public class AttackBehavior implements Behavior {
    private final Set<Ability> attacks = new HashSet<>();
    private final CompPlayer player;
    private final Entity target;
    private final Unit unit;
    private Optional<Path> selection = Optional.empty();
    private Point dest = new Point(-1, -1);
    private boolean kill = false;

    public AttackBehavior(CompPlayer player, Unit unit, Entity target) {
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
        boolean attacked = false;
        for (Ability attack : this.attacks) {
            if (this.isWithinRange(view, attack)) {
                // TODO double check that we can activate this Ability
                attack.activate(view).execute();
                attacked = true;
            }
        }

        // Move closer to the target otherwise
        if (!attacked) {
            final Pathfinder pathfinder = new Pathfinder(this.unit);
            final List<Point> path = pathfinder.getPath(view, this.dest);
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
        return this.kill || this.target.combat.health.isDead();
    }

    /**
     * Returns true if the Unit is within range to use the given Ability on the
     * target
     */
    private boolean isWithinRange(GameView view, Ability attack) {
        this.player.actor.state.deliberate();
        final Optional<Prioritized<Path>> result = this.player.actor.analyze.activeAbility(view, attack,
                (Prediction prediction) -> {
                    for (Event e : prediction.log) {
                        if (e.getClass() == Events.TakeDamageEvent.class
                                && ((Events.TakeDamageEvent) e).target == this.target) {
                            return Priority.GOOD_IDEA;
                        }
                    }
                    return Priority.FATAL;
                });
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
        this.attacks.clear();
        final Chooser<Point> closest = new Chooser<>((Integer old, Integer next) -> next < old);
        for (Ability ability : this.unit.abilities.getActives()) {
            final Set<Point> options = Hexagons.getNeighbors(this.target.getPoint(), 6);
            for (Point p : options) {
                final Optional<Prioritized<Path>> result = this.player.actor.analyze.activeAbility(view, ability, p,
                        (Prediction prediction) -> {
                            for (Event e : prediction.log) {
                                if (e.getClass() == Events.TakeDamageEvent.class
                                        && ((Events.TakeDamageEvent) e).target == this.target) {
                                    return Priority.GOOD_IDEA;
                                }
                            }
                            return Priority.FATAL;
                        });
                if (result.map((Prioritized<Path> prioritized) -> prioritized.priority != Priority.FATAL)
                        .orElse(false)) {
                    closest.choice(p, p.distance(this.unit.getPoint()));
                    if (!this.attacks.contains(ability)) {
                        this.attacks.add(ability);
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
