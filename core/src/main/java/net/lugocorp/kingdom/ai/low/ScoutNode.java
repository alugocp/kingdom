package net.lugocorp.kingdom.ai.low;
import net.lugocorp.kingdom.ai.ActionResult;
import net.lugocorp.kingdom.ai.LowNode;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.math.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * This LowNode tells the Actor's Unit to scout the map
 */
public class ScoutNode extends LowNode {

    public ScoutNode(Unit unit, Function<LowNode, Float> calculateScore) {
        super(unit, calculateScore);
    }

    /** {@inheritdoc} */
    @Override
    public ActionResult act(GameView view) {
        return ActionResult.RIDE;
    }

    /** {@inheritdoc} */
    @Override
    public List<LowNode> generateTrees() {
        List<LowNode> trees = new ArrayList<>();
        Point p = new Point(this.unit.getX() - 1, this.unit.getY());
        trees.add(new MoveNode(p, this.unit, (LowNode n) -> this.getScore()));
        return trees;
    }
}
