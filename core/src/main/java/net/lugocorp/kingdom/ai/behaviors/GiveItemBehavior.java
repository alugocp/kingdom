package net.lugocorp.kingdom.ai.behaviors;
import net.lugocorp.kingdom.ai.Behavior;
import net.lugocorp.kingdom.game.layers.Entity;
import net.lugocorp.kingdom.game.model.Building;
import net.lugocorp.kingdom.game.model.Item;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.game.properties.Inventory;
import net.lugocorp.kingdom.math.Hexagons;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.Lambda;
import java.util.function.Function;

/**
 * This Behavior tells the given Entity to give some Item to another Entity
 */
public class GiveItemBehavior implements Behavior {
    private final Function<Item, Boolean> criteria;
    private final Entity receiver;
    private final Entity sender;

    public GiveItemBehavior(Entity sender, Entity receiver, Function<Item, Boolean> criteria) {
        this.criteria = criteria;
        this.receiver = receiver;
        this.sender = sender;
    }

    /** {@inheritdoc} */
    @Override
    public void act(GameView view) {
        if (!Hexagons.areNeighbors(this.sender.getPoint(), this.receiver.getPoint())) {
            return;
        }
        final Inventory r = (receiver instanceof Unit) ? ((Unit) receiver).haul : ((Building) receiver).items.get();
        final Inventory s = (sender instanceof Unit) ? ((Unit) sender).haul : ((Building) sender).items.get();
        s.transfer(r, Lambda.random(Lambda.filter((Item i) -> this.criteria.apply(i), Lambda.toList(s))));
    }

    /** {@inheritdoc} */
    @Override
    public boolean isFinished(GameView view) {
        return true;
    }
}
