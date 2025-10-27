package net.lugocorp.kingdom.ai.plans;
import net.lugocorp.kingdom.ai.action.ActionResult;
import net.lugocorp.kingdom.ai.action.PlanNode;
import net.lugocorp.kingdom.ai.prediction.CapturedEvents;
import net.lugocorp.kingdom.ai.prediction.EventLog;
import net.lugocorp.kingdom.ai.prediction.SelectedTargets;
import net.lugocorp.kingdom.game.model.Ability;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.math.Path;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.Tuple;
import java.util.Optional;
import java.util.function.Function;

/**
 * Causes the Unit to cast an active Ability
 */
public class CastSpellNode extends PlanNode {
    private Optional<Path> selectedPath = Optional.empty();
    private final EventLog prediction;
    private final Ability ability;

    public CastSpellNode(GameView view, Unit unit, Ability ability) {
        super(unit);
        this.ability = ability;
        CapturedEvents.instance.on();
        ability.activate(view);
        this.prediction = CapturedEvents.instance.off();
    }

    /**
     * Grants another class access to our prediction of future Events, so that they
     * can calculate a score value from it
     */
    public float scoreByPrediction(Function<EventLog, Optional<Tuple<Path, Float>>> selectPathAndScore) {
        Optional<Tuple<Path, Float>> result = selectPathAndScore.apply(this.prediction);
        if (result.map((Tuple<Path, Float> r) -> r.b > 0f).orElse(false)) {
            this.selectedPath = Optional.of(result.get().a);
            return result.get().b;
        }
        return 0f;
    }

    /** {@inheritdoc} */
    @Override
    public ActionResult act(GameView view) {
        if (!this.selectedPath.isPresent()) {
            throw new RuntimeException("AI cannot activate an Ability without a selected Path");
        }
        SelectedTargets.instance.setActivePath(this.selectedPath.get());
        this.ability.activate(view).execute();
        SelectedTargets.instance.clear();
        return ActionResult.POP;
    }
}
