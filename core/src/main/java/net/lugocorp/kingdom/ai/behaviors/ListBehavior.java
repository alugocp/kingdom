package net.lugocorp.kingdom.ai.behaviors;
import net.lugocorp.kingdom.ai.Behavior;
import net.lugocorp.kingdom.ui.views.GameView;
import java.util.ArrayList;
import java.util.List;

/**
 * This Behavior calls down into arbitrarily many other Behaviors
 */
public class ListBehavior implements Behavior {
    private final List<Behavior> children = new ArrayList<>();

    public ListBehavior(Behavior... children) {
        for (int a = 0; a < children.length; a++) {
            this.children.add(children[a]);
        }
    }

    /** {@inheritdoc} */
    @Override
    public void act(GameView view) {
        this.children.get(0).act(view);
    }

    /** {@inheritdoc} */
    @Override
    public boolean isFinished(GameView view) {
        if (this.children.get(0).isFinished(view)) {
            this.children.remove(0);
        }
        return this.children.size() == 0;
    }
}
