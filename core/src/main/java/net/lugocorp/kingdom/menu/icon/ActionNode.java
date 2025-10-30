package net.lugocorp.kingdom.menu.icon;
import net.lugocorp.kingdom.engine.AudioVideo;
import net.lugocorp.kingdom.math.Point;
import net.lugocorp.kingdom.math.Rect;
import net.lugocorp.kingdom.menu.MenuNode;
import net.lugocorp.kingdom.menu.structure.ListNode;
import net.lugocorp.kingdom.menu.text.SubheaderNode;
import net.lugocorp.kingdom.menu.text.TextNode;
import java.util.Optional;

/**
 * Can be used to represent an Ability or other such action
 */
public class ActionNode extends IconNode {
    public static final int SIDE = 50;
    private final Runnable action;
    private final ListNode node;
    private boolean active = true;
    // TODO some visual change on hover (do for all IconNodes?), make noise on
    // click, visually distinguish between enabled/disabled/passive state
    // TODO implement a GridNode that can arrange elements in a dynamically wide
    // grid (put to use in InventoryNode as well)

    public ActionNode(AudioVideo av, String name, String icon, Optional<String> desc, Runnable action) {
        super(av, icon, ActionNode.SIDE);
        this.node = new ListNode().add(new SubheaderNode(av, name));
        desc.ifPresent((String s) -> this.node.add(new TextNode(av, s)));
        this.action = action;
    }

    /**
     * Sets whether this ActionNode is active or not
     */
    public ActionNode enable(boolean active) {
        this.active = active;
        return this;
    }

    /** {@inheritdoc} */
    @Override
    public void click(Rect bounds, Point p) {
        if (this.active && bounds.contains(p)) {
            this.action.run();
        }
    }

    /** {@inheritdoc} */
    @Override
    protected MenuNode getPopupNode() {
        return this.node;
    }
}
