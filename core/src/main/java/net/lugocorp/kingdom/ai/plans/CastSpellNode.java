package net.lugocorp.kingdom.ai.plans;
import net.lugocorp.kingdom.ai.ActionResult;
import net.lugocorp.kingdom.ai.PlanNode;
import net.lugocorp.kingdom.ai.prediction.CapturedEvents;
import net.lugocorp.kingdom.ai.prediction.EventLog;
import net.lugocorp.kingdom.game.model.Ability;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.ui.views.GameView;
import java.util.function.Function;

/**
 * Causes the Unit to cast an active Ability
 */
public class CastSpellNode extends PlanNode {
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
    public float scoreByPrediction(Function<EventLog, Float> lambda) {
        return lambda.apply(this.prediction);
    }

    /** {@inheritdoc} */
    @Override
    public ActionResult act(GameView view) {
        this.ability.activate(view).execute();
        return ActionResult.POP;
    }
}
