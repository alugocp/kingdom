package net.lugocorp.kingdom.ai;
import net.lugocorp.kingdom.ai.goals.DefendBuildingsGoal;
import net.lugocorp.kingdom.ai.goals.ExploreMapGoal;
import net.lugocorp.kingdom.ai.goals.FeedUnitsGoal;
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
        this.goals.add(new ExploreMapGoal());
        this.goals.add(new DefendBuildingsGoal());
        this.goals.add(new FeedUnitsGoal());
    }

    /**
     * Returns an Iterator for the Goals in this GoalSet
     */
    @Override
    public Iterator<Goal> iterator() {
        return this.goals.iterator();
    }
}
