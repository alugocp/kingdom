package net.lugocorp.kingdom.ai.goals;
import net.lugocorp.kingdom.ai.Consideration;
import net.lugocorp.kingdom.ai.Decision;
import net.lugocorp.kingdom.ai.DecisionChannel;
import net.lugocorp.kingdom.ai.DecisionClass;
import net.lugocorp.kingdom.ai.Goal;
import net.lugocorp.kingdom.ai.Priority;
import net.lugocorp.kingdom.ai.behaviors.RecruitUnitBehavior;
import net.lugocorp.kingdom.game.glyph.Glyph;
import net.lugocorp.kingdom.game.model.Ability;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.game.player.CompPlayer;
import net.lugocorp.kingdom.math.Path;
import net.lugocorp.kingdom.math.Point;
import net.lugocorp.kingdom.prediction.Prediction;
import net.lugocorp.kingdom.prediction.SelectionTree;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.Lambda;
import java.util.Set;

/**
 * This causes the CompPlayer to spawn Buildings
 */
public class BuildInfrastructureGoal extends Goal {

    /** {@inheritdoc} */
    @Override
    protected Decision makeDecision(GameView view, CompPlayer player, DecisionChannel channel) {
        // Recruit unit handler (recruit a defensive Unit)
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
            for (Ability ability : unit.abilities.getActives()) {
                final SelectionTree tree = player.actor.newSelectionTree();
                ability.activate(view);
                // Set up paths to event lists and analyze accordingly
                final Consideration<Path> consideration = new Consideration<>();
                tree.iteratePredictions((Prediction prediction) -> {
                    final Priority priority = null;
                    // TODO something here
                    consideration.consider(priority, prediction.path);
                });
            }
        }

        // No decision fallback
        return this.noDecision(channel);
    }
}
