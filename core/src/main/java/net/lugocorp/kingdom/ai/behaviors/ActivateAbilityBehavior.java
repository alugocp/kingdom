package net.lugocorp.kingdom.ai.behaviors;
import net.lugocorp.kingdom.ai.Behavior;
import net.lugocorp.kingdom.game.model.Ability;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.math.Path;
import net.lugocorp.kingdom.math.Point;
import net.lugocorp.kingdom.ui.views.GameView;

/**
 * This Behavior tells the given Unit to activate some Ability
 */
public class ActivateAbilityBehavior implements Behavior {
    private final Ability ability;
    private final Path selections;
    private final Unit unit;

    public ActivateAbilityBehavior(Unit unit, Ability ability, Path selections) {
        this.selections = selections;
        this.ability = ability;
        this.unit = unit;
    }

    /**
     * Returns the next Point parameter in a chain of calls to CompPlayer.select()
     */
    public Point getSelection() {
        return this.selections.popFromFront();
    }

    /** {@inheritdoc} */
    @Override
    public void act(GameView view) {
        // TODO double check that we can activate this Ability
        ability.activate(view).execute();
    }

    /** {@inheritdoc} */
    @Override
    public boolean isFinished(GameView view) {
        return true;
    }
}
