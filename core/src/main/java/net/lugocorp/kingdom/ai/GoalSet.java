package net.lugocorp.kingdom.ai;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Contains all the Goals utilized by CompPlayers
 */
public class GoalSet implements Iterable<Goal> {
    public static final GoalSet singleton = new GoalSet();
    private final List<Goal> goals = new ArrayList<>();

    public GoalSet() {
        // Add goals in order here with their dependencies as constructor parameters
    }

    /**
     * Returns an Iterator for the Goals in this GoalSet
     */
    @Override
    public Iterator<Goal> iterator() {
        return this.goals.iterator();
    }
}
