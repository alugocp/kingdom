package net.lugocorp.kingdom.ai.high;
import net.lugocorp.kingdom.ai.HighNode;
import net.lugocorp.kingdom.ai.LowNode;
import net.lugocorp.kingdom.ai.low.ScoutNode;
import net.lugocorp.kingdom.game.model.Unit;
import java.util.ArrayList;
import java.util.List;

/**
 * This class tells the Actor to explore the map
 */
public class ExploreMapNode implements HighNode {

    /** {@inheritdoc} */
    @Override
    public List<LowNode> suggestLowNodes(Unit u) {
        List<LowNode> list = new ArrayList();
        list.add(new ScoutNode(u, this::scoreNode));
        return list;
    }

    /** {@inheritdoc} */
    @Override
    public float scoreNode(LowNode leaf) {
        // TODO implement me
        return 1f;
    }
}
