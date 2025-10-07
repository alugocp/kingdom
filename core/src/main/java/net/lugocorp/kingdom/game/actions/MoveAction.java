package net.lugocorp.kingdom.game.actions;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.math.Point;
import java.util.List;

/**
 * This Action represents a Unit moving
 */
public class MoveAction implements Action {
    private final List<Point> path;
    private final GameView view;
    private final Unit unit;
    private int distance;
    private int max;

    public MoveAction(GameView view, Unit unit, List<Point> path, int distance) {
        this.max = unit.movement.getMaxDistance(view);
        this.distance = distance;
        this.path = path;
        this.view = view;
        this.unit = unit;
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
    public boolean canBeFollowedBy(ActionType a) {
        return this.distance == 0 || (a == ActionType.MOVE && this.distance < this.max);
    }

    /** {@inheritdoc} */
    @Override
    public Action followedBy(Action a) {
        switch (a.getType()) {
            case SKIP :
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
        this.max = this.unit.movement.getMaxDistance(this.view);

        // If there's more path to move on and we haven't hit max
        // distance yet this turn then do that now
        if (remain && this.distance < this.max) {
            final Point dest = this.path.get(Math.min(this.max - this.distance, this.path.size()) - 1);
            this.unit.movement.move(this.view, dest).execute();
        }

        // Reset distance moved for the next turn
        this.distance = 0;
        return remain;
    }

    /** {@inheritdoc} */
    @Override
    public String getDescription() {
        if (this.path.size() > 0) {
            return String.format("This unit plans to move %d more tile(s), but you can give it a different command",
                    this.path.size());
        }
        return this.distance == this.max
                ? "This unit has moved its maximum distance and has exhausted its actions this turn"
                : "This unit has moved this turn but can still go further";
    }
}
