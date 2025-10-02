package net.lugocorp.kingdom.ai.wishlist;
import net.lugocorp.kingdom.ai.Actor;
import net.lugocorp.kingdom.ai.action.Goal;
import net.lugocorp.kingdom.utils.code.Tuple;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * This class organizes a list of elements that the AI Player could want. It
 * helps the AI Player determine which element they want the most.
 */
abstract class Wishlist<T> {
    private final Actor actor;
    protected final Set<T> options = new HashSet<>();

    Wishlist(Actor actor) {
        this.actor = actor;
    }

    /**
     * Returns a score associated with the given option and Goal
     */
    protected abstract int getScoreForGoal(T option, Goal g);

    /**
     * Sets the available options to choose from
     */
    public void setOptions(Collection<T> ops) {
        this.options.clear();
        this.options.addAll(ops);
    }

    /**
     * Returns the score for the given option
     */
    int getDesireForOption(T t) {
        int score = 0;
        for (Goal g : this.actor.getGoals()) {
            score += this.getScoreForGoal(t, g);
        }
        return score;
    }

    /**
     * Returns all options and how much the AI Player wants each one
     */
    public Desires<T> getDesiredOptions() {
        final Desires<T> desires = new Desires<>();
        for (T t : this.options) {
            desires.add(new Tuple<T, Integer>(t, this.getDesireForOption(t)));
        }
        return desires;
    }
}
