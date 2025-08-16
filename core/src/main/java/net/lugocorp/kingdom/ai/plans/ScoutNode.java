package net.lugocorp.kingdom.ai.plans;
import net.lugocorp.kingdom.ai.ActionResult;
import net.lugocorp.kingdom.ai.PlanNode;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.math.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * This PlanNode tells the Actor's Unit to scout the map
 */
public class ScoutNode extends PlanNode {

    public ScoutNode(Unit unit, Function<PlanNode, Float> calculateScore) {
        super(unit, calculateScore);
    }

    /** {@inheritdoc} */
    @Override
    public ActionResult act(GameView view) {
        return ActionResult.RIDE;
    }

    /** {@inheritdoc} */
    @Override
    public List<PlanNode> generateTrees() {
        List<PlanNode> trees = new ArrayList<>();
        Point p = new Point(this.unit.getX() - 1, this.unit.getY());
        trees.add(new MoveNode(p, this.unit, (PlanNode n) -> this.getScore()));
        return trees;
    }
}
