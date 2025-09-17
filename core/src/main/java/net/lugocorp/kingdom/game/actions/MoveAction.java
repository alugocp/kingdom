package net.lugocorp.kingdom.game.actions;
import net.lugocorp.kingdom.utils.math.Point;
import java.util.List;

/**
 * This Action represents a Unit moving
 */
public class MoveAction implements Action {
    private final List<Point> path;
    private final int max;
    private int distance;

    public MoveAction(List<Point> path, int distance, int max) {
        this.distance = distance;
        this.path = path;
        this.max = max;
    }

    /**
     * Returns the distance associated with this Action
     */
    int getDistance() {
        return this.distance;
    }

    /**
     * Removes the first n elements from this instance's path
     */
    private void unshift(int n) {
        for (int a = 0; a < n; a++) {
            this.path.remove(0);
        }
    }

    /**
     * Returns true if the given MoveAction's path follows this MoveAction's path
     */
    private boolean pathFollowsThisPath(MoveAction ma) {
        if (ma.path.size() > this.path.size()) {
            return false;
        }
        for (int a = 0; a < ma.path.size(); a++) {
            if (!ma.path.get(a).equals(this.path.get(a))) {
                return false;
            }
        }
        return true;
    }

    /** {@inheritdoc} */
    @Override
    public ActionType getType() {
        return ActionType.MOVE;
    }

    /** {@inheritdoc} */
    @Override
    public void addedFirst() {
        this.unshift(this.distance);
    }

    /** {@inheritdoc} */
    @Override
    public Action followedBy(Action a) {
        switch (a.getType()) {
            case SKIP :
            case SKIP_INVENTORY :
                return this;
            case ACTIVATE :
                return a;
            case MOVE : {
                final MoveAction ma = (MoveAction) a;
                if (this.pathFollowsThisPath(ma)) {
                    this.distance += ma.distance;
                    this.unshift(ma.distance);
                    return this;
                } else {
                    ma.unshift(ma.distance);
                    ma.distance += this.distance;
                    return ma;
                }
            }
        }
        throw new RuntimeException("Following actions should never get here");
    }

    /** {@inheritdoc} */
    @Override
    public boolean endOfTurn() {
        final boolean remain = this.path.size() > 0;
        if (remain && this.distance < this.max) {
            // TODO MOVEMENT kick off unit.movement.move() right here
        }
        return remain;
    }

    /** {@inheritdoc} */
    @Override
    public String getDescription() {
        return this.distance == this.max
                ? "This unit has moved its maximum distance this turn"
                : "This unit has moved this turn";
    }
}
