package net.lugocorp.kingdom.ai.goals;
import net.lugocorp.kingdom.ai.Behavior;
import net.lugocorp.kingdom.ai.Consideration;
import net.lugocorp.kingdom.ai.Decision;
import net.lugocorp.kingdom.ai.DecisionChannel;
import net.lugocorp.kingdom.ai.DecisionClass;
import net.lugocorp.kingdom.ai.Goal;
import net.lugocorp.kingdom.ai.Prioritized;
import net.lugocorp.kingdom.ai.Priority;
import net.lugocorp.kingdom.ai.behaviors.ActivateAbilityBehavior;
import net.lugocorp.kingdom.ai.behaviors.ListBehavior;
import net.lugocorp.kingdom.ai.behaviors.MoveUnitBehavior;
import net.lugocorp.kingdom.ai.behaviors.RecruitUnitBehavior;
import net.lugocorp.kingdom.builtin.Events;
import net.lugocorp.kingdom.game.glyph.Glyph;
import net.lugocorp.kingdom.game.model.Ability;
import net.lugocorp.kingdom.game.model.Tile;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.game.player.CompPlayer;
import net.lugocorp.kingdom.gameplay.events.Event;
import net.lugocorp.kingdom.math.Hexagons;
import net.lugocorp.kingdom.math.Path;
import net.lugocorp.kingdom.math.Point;
import net.lugocorp.kingdom.pathfinding.Pathfinder;
import net.lugocorp.kingdom.prediction.Prediction;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.Lambda;
import net.lugocorp.kingdom.utils.Tuple;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * This causes the CompPlayer to spawn Buildings
 */
public class BuildInfrastructureGoal extends Goal {

    /** {@inheritdoc} */
    @Override
    protected Decision makeDecision(GameView view, CompPlayer player, DecisionChannel channel) {
        // Recruit unit handler (recruit a support Unit)
        if (channel.is(DecisionClass.RECRUIT_UNIT)) {
            return new Decision(channel, this, Priority.GOOD_IDEA, new RecruitUnitBehavior(player, Glyph.SUPPORT,
                    (Unit u) -> Priority.NEUTRAL, (Set<Point> options) -> {
                        final Set<Point> undeveloped = Lambda
                                .filter((Point p) -> !view.game.world.getTile(p).get().building.isPresent(), options);
                        return undeveloped.size() > 0 ? Lambda.random(undeveloped) : Lambda.random(options);
                    }));
        }

        // Unit handler
        if (channel.is(DecisionClass.UNIT)) {
            final Unit unit = channel.getUnit();
            final Consideration<Behavior> consideration = new Consideration<>();
            final Optional<Tuple<Point, List<Point>>> override = this.getOverridePoint(view, unit);
            for (Ability ability : unit.abilities.getActives()) {
                final Optional<Prioritized<Path>> best = player.actor.analyze.activeAbility(view, ability,
                        override.map((Tuple<Point, List<Point>> t) -> t.a).orElse(unit.getPoint()),
                        (Prediction prediction) -> {
                            for (Event event : prediction.log) {
                                if (event instanceof Events.GenerateBuildingEvent) {
                                    final Events.GenerateBuildingEvent e = (Events.GenerateBuildingEvent) event;
                                    if (e.blob.leadership.sameLeader(unit)) {
                                        return Priority.GOOD_IDEA;
                                    }
                                }
                            }
                            return Priority.FATAL;
                        });
                best.ifPresent((Prioritized<Path> b) -> {
                    final ActivateAbilityBehavior activate = new ActivateAbilityBehavior(unit, ability, b.value);
                    consideration
                            .consider(
                                    b.priority, override
                                            .map((Tuple<Point, List<Point>> t) -> (Behavior) new ListBehavior(
                                                    new MoveUnitBehavior(unit, t.b), activate))
                                            .orElse((Behavior) activate));
                });
            }
            final Optional<Prioritized<Behavior>> behavior = consideration.getState();
            if (behavior.isPresent()) {
                return new Decision(channel, this, behavior.get().priority, behavior.get().value);
            }
        }

        // No decision fallback
        return this.noDecision(channel);
    }

    /**
     * Optionally returns a better Point to spawn a Building on
     */
    private Optional<Tuple<Point, List<Point>>> getOverridePoint(GameView view, Unit u) {
        // Don't bother if there's no Building at the current Point
        if (!view.game.world.getTile(u.getPoint()).get().building.isPresent()) {
            return Optional.empty();
        }

        // Get a random neighboring Point that the given Unit can traverse to anf that
        // does not have a Building
        final Pathfinder pathfinder = new Pathfinder(u);
        final Set<Tuple<Point, List<Point>>> options = Lambda.filter((Tuple<Point, List<Point>> t) -> t.b.size() > 0,
                Lambda.map((Point p) -> new Tuple<Point, List<Point>>(p, pathfinder.getPath(view, p)), Lambda.filter(
                        (Point p) -> view.game.world.getTile(p).map((Tile t) -> !t.building.isPresent()).orElse(false),
                        Hexagons.getNeighbors(u.getPoint(), 3))));
        return options.size() == 0 ? Optional.empty() : Optional.of(Lambda.random(options));
    }
}
