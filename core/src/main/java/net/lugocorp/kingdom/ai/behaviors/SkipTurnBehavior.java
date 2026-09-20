package net.lugocorp.kingdom.ai.behaviors;
import net.lugocorp.kingdom.ai.Behavior;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.gameplay.actions.SkipAction;
import net.lugocorp.kingdom.ui.views.GameView;

/**
 * This Behavior tells the given Unit to skip its turn
 */
public class SkipTurnBehavior implements Behavior {
    private final Unit unit;

    public SkipTurnBehavior(Unit unit) {
        this.unit = unit;
    }

    /** {@inheritdoc} */
    @Override
    public void act(GameView view) {
        view.game.actions.unitHasActed(view, this.unit,
                new SkipAction("This unit will stay put this turn", () -> true));
    }

    /** {@inheritdoc} */
    @Override
    public boolean isFinished(GameView view) {
        return true;
    }
}
