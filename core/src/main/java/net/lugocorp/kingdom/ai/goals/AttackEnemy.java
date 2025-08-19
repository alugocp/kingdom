package net.lugocorp.kingdom.ai.goals;
import net.lugocorp.kingdom.ai.Goal;
import net.lugocorp.kingdom.ai.Plan;
import net.lugocorp.kingdom.ai.PlanNode;
import net.lugocorp.kingdom.ai.plans.CastSpellNode;
import net.lugocorp.kingdom.ai.prediction.EventLog;
import net.lugocorp.kingdom.game.core.Events;
import net.lugocorp.kingdom.game.events.Event;
import net.lugocorp.kingdom.game.model.Ability;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.game.player.Player;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.code.Lambda;
import net.lugocorp.kingdom.utils.code.Tuple;
import net.lugocorp.kingdom.utils.math.Path;
import java.util.List;
import java.util.Optional;

/**
 * This class tells the Actor to attack Units and Buildings under enemy control
 */
public class AttackEnemy extends Goal {

    /**
     * Returns true if the Event describes the death of the given Unit
     */
    private boolean hasUnitDied(Event e, Unit u) {
        if (e instanceof Events.UnitDiedEvent) {
            return ((Events.UnitDiedEvent) e).unit == u;
        }
        return false;
    }

    /**
     * Returns true if the Event describes the death of a Unit with either: 1) a
     * different leader as the Unit, if sameLeader is false; 2) the same leader as
     * the Unit, if sameLeader is true
     */
    private boolean alignedUnitDied(Event e, Unit u, boolean sameLeader) {
        if (e instanceof Events.UnitDiedEvent) {
            return ((Events.UnitDiedEvent) e).unit.getLeader()
                    .map((Player l) -> l.equals(u.getLeader().get()) == sameLeader).orElse(false);
        }
        return false;
    }

    /**
     * Returns the amount of Damage dealt to Units under enemy control
     */
    private int damageDealtToEnemies(Event e, Unit u) {
        if (e instanceof Events.TakeDamageEvent) {
            Events.TakeDamageEvent evt = (Events.TakeDamageEvent) e;
            if (evt.target instanceof Unit) {
                return ((Unit) evt.target).getLeader().equals(u.getLeader()) ? 0 : evt.dmg.amount;
            }
        }
        return 0;
    }

    /** {@inheritdoc} */
    @Override
    public Optional<Plan> suggestPlan(GameView view, Unit u) {
        return this.getBestPlan(Lambda.map((Ability a) -> this.wrapPlanNode(view, new CastSpellNode(view, u, a)),
                u.getActiveAbilities()));
    }

    /** {@inheritdoc} */
    @Override
    protected float getScore(GameView view, PlanNode root) {
        CastSpellNode node = (CastSpellNode) root;
        Unit unit = root.unit;
        return node.scoreByPrediction((EventLog prediction) -> {
            Tuple<Path, Float> best = new Tuple<>(null, 0f);
            for (Path branch : prediction.getTargetPaths()) {
                List<Event> events = prediction.getEvents(branch);
                boolean attackerDied = Lambda.some((Event e) -> this.hasUnitDied(e, unit), events);
                int enemiesDied = Lambda.filter((Event e) -> this.alignedUnitDied(e, unit, false), events).size();
                int alliesDied = Lambda.filter((Event e) -> this.alignedUnitDied(e, unit, true), events).size();
                int damageDealt = Lambda.fold((Integer acc, Event e) -> acc + this.damageDealtToEnemies(e, unit), 0,
                        events);

                // Find our best target Path to return. If more of our Units died
                // from this Combat than enemy Units then it'll be scored at zero.
                float score = 0f;
                if (alliesDied < enemiesDied) {
                    score = 1f;
                }
                if (alliesDied == enemiesDied) {
                    score = (Math.min(damageDealt, 10f) / 10f);
                }
                if (score > best.b) {
                    best = new Tuple(branch, score);
                }
            }
            return best;
        });
    }
}
