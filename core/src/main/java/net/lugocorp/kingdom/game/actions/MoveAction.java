package net.lugocorp.kingdom.game.actions;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.math.Point;
import net.lugocorp.kingdom.ui.views.GameView;
import java.util.ArrayList;
import java.util.List;

/**
 * This Action represents a Unit moving
 */
public class MoveAction implements Action {
    private final List<Point> path = new ArrayList<>();
    private final GameView view;
    private final Unit unit;
    private int distance;
    private int max;

    public MoveAction(GameView view, Unit unit, List<Point> path, int distance) {
        this.max = unit.movement.getMaxDistance(view);
        this.distance = distance;
        this.view = view;
        this.unit = unit;
        this.path.addAll(path);
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
                this.path.clear();
                this.path.addAll(ma.path);
                this.unshift(ma.distance);
                this.distance += ma.distance;
                return this;
            }
        }
        throw new RuntimeException("Following actions should never get here");
    }

    /** {@inheritdoc} */
    @Override
    public boolean startOfTurn() {
        // Drop this Action if any Point becomes inaccessible to the Unit
        for (Point p : this.path) {
            if (!this.unit.movement.canMoveToPoint(this.view, p)) {
                return false;
            }
        }
        return true;
    }

    /** {@inheritdoc} */
    @Override
    public boolean endOfTurn() {
        // Recalculate the max distance we can travel this turn
        this.max = this.unit.movement.getMaxDistance(this.view);

        // If there's more path to move on and we haven't hit max
        // distance yet this turn then do that now
        if (this.path.size() > 0 && this.distance < this.max) {
            this.unit.movement.move(this.view, this.path).execute();
        }

        // Reset distance moved for the next turn
        this.distance = 0;
        return this.path.size() > 0;
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
