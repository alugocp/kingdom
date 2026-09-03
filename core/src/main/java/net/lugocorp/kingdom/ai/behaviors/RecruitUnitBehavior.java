package net.lugocorp.kingdom.ai.behaviors;
import net.lugocorp.kingdom.ai.Behavior;
import net.lugocorp.kingdom.ai.Priority;
import net.lugocorp.kingdom.game.glyph.Glyph;
import net.lugocorp.kingdom.game.model.Unit;
import java.util.function.Function;

/**
 * This Behavior tells the CompPlayer to recruit a specific type of Unit
 */
public class RecruitUnitBehavior implements Behavior {
    private final Function<Unit, Priority> criteria;
    private final Glyph glyph;

    public RecruitUnitBehavior(Glyph glyph, Function<Unit, Priority> criteria) {
        this.criteria = criteria;
        this.glyph = glyph;
    }

    /** {@inheritdoc} */
    @Override
    public void act() {
        // TODO
    }

    /** {@inheritdoc} */
    @Override
    public boolean isFinished() {
        return true;
    }
}
