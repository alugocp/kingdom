package net.lugocorp.kingdom.ai.plans;
import net.lugocorp.kingdom.ai.ActionResult;
import net.lugocorp.kingdom.ai.PlanNode;
import net.lugocorp.kingdom.ai.prediction.CapturedEvents;
import net.lugocorp.kingdom.ai.prediction.EventLog;
import net.lugocorp.kingdom.ai.prediction.SelectedTargets;
import net.lugocorp.kingdom.game.model.Ability;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.code.Tuple;
import net.lugocorp.kingdom.utils.math.Path;
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
    public float scoreByPrediction(Function<EventLog, Tuple<Path, Float>> selectPathAndScore) {
        Tuple<Path, Float> result = selectPathAndScore.apply(this.prediction);
        if (result.b > 0f) {
            this.selectedPath = Optional.of(result.a);
            return result.b;
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
