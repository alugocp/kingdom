package net.lugocorp.kingdom.ai.plans;
import net.lugocorp.kingdom.ai.action.ActionResult;
import net.lugocorp.kingdom.ai.action.PlanNode;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.math.Point;
import net.lugocorp.kingdom.pathfinding.Pathfinder;
import net.lugocorp.kingdom.ui.views.GameView;
import java.util.List;

/**
 * This PlanNode tells the Actor's Unit to move somewhere
 */
public class MoveNode extends PlanNode {
    private final Pathfinder pathfinder;
    public final Point dest;

    public MoveNode(Unit unit, Point dest) {
        super(unit);
        this.pathfinder = new Pathfinder(unit);
        this.dest = dest;
    }

    /** {@inheritdoc} */
    @Override
    public ActionResult act(GameView view) {
        // Determine the path to follow, and if we can't get to our destination then
        // throw out the entire Plan
        final List<Point> path = this.pathfinder.getPath(view, this.dest);
        if (path.size() == 0) {
            return ActionResult.POP_ALL;
        }

        // Set up the move command
        this.unit.movement.move(view, path).execute();
        return ActionResult.POP;
    }
}
