package net.lugocorp.kingdom.ai.goals;
import net.lugocorp.kingdom.ai.Decision;
import net.lugocorp.kingdom.ai.DecisionChannel;
import net.lugocorp.kingdom.ai.Goal;
import net.lugocorp.kingdom.ai.behaviors.RecruitUnitBehavior;
import net.lugocorp.kingdom.game.glyph.Glyph;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.game.player.CompPlayer;
import net.lugocorp.kingdom.ui.views.GameView;

/**
 * This causes the CompPlayer to explore the World
 */
public class ExploreMapGoal extends Goal {

    /** {@inheritdoc} */
    @Override
    protected Decision makeDecision(GameView view, CompPlayer player, DecisionChannel channel) {
        final World world = view.game.world;
        final float ratio = (float) player.actor.memory.getKnownCells().size() / (float) world.getSize();
        final int fastUnits = this.getNumberOfFastUnits(view, player);

        // Recruit unit handler (recruit a fast Trade Glyph Unit)
        if (channel.is(DecisionClass.RECRUIT_UNIT)) {
            Priority p = Priority.NEUTRAL;
            if (fastUnits == 2) {
                p = Priority.GOOD_IDEA;
            }
            if (fastUnits == 1) {
                p = Priority.OPTIMAL;
            }
            if (fastUnits == 0) {
                p = Priority.NECESSITY;
            }
            return new Decision(channel, p, new RecruitUnitBehavior(Glyph.TRADE,
                    (Unit u) -> u.movement.getMaxDistance(view) > 2 ? Priority.GOOD_IDEA : Priority.NEUTRAL));
        }

        // Unit handler
        if (channel.is(DecisionClass.UNIT)) {
            final int max = channel.getUnit().movement.getMaxDistance(view);

            // Slow Units
            if (max < 2) {
                Priority p = ratio < 0.1 ? Priority.NEUTRAL : Priority.BAD_IDEA;
                if (fastUnits > 0) {
                    p = p.decrement();
                }
                return new Decision(channel, p, this.getExplorationBehavior(channel.getUnit()));
            }

            // Fast Units
            if (max > 2) {
                // Good or optimal idea
                Priority p = Priority.NEUTRAL;
                if (p < 0.1) {
                    p = Priority.NECESSITY;
                } else if (p < 0.35) {
                    p = Priority.OPTIMAL;
                } else if (p < 0.80) {
                    p = Priority.GOOD_IDEA;
                }
                if (fastUnits > 2) {
                    p = p.decrement();
                }
                return new Decision(channel, p, this.getExplorationBehavior(channel.getUnit()));
            }

            // Average Units
            Priority p = ratio < 0.6 ? Priority.GOOD_IDEA : Priority.NEUTRAL;
            if (fastUnits > 1) {
                p = p.decrement();
            }
            return new Decision(channel, p, this.getExplorationBehavior(channel.getUnit()));
        }

        // No decision fallback
        return this.noDecision(channel);
    }

    /**
     * Returns the number of fast Units controlled by the given CompPlayer
     */
    private int getNumberOfFastUnits(GameView view, CompPlayer player) {
        int count = 0;
        for (Unit u : player.units) {
            if (u.movement.getMaxDistance(view) > 2) {
                count++;
            }
        }
        return count;
    }

    /**
     * Returns a Behavior that sets a destination for the given Unit
     */
    private Behavior getExplorationBehavior(Unit unit) {
        // TODO finish me
        return null;
    }
}
