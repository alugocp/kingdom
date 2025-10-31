package net.lugocorp.kingdom.menu.icon;
import net.lugocorp.kingdom.engine.AudioVideo;
import net.lugocorp.kingdom.engine.shaders.ElementShader;
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
    public static final int MODE_NOTHING = 0;
    public static final int MODE_DISABLED = 1;
    public static final int MODE_ACTIVE = 2;
    private final Runnable action;
    private final AudioVideo av;
    private final ListNode node;
    private int mode = ActionNode.MODE_ACTIVE;

    public ActionNode(AudioVideo av, String name, String icon, Optional<String> desc, Runnable action) {
        super(av, icon, ActionNode.SIDE);
        this.node = new ListNode().add(new SubheaderNode(av, name));
        desc.ifPresent((String s) -> this.node.add(new TextNode(av, s)));
        this.action = action;
        this.av = av;
    }

    /**
     * Sets whether this ActionNode is active or not
     */
    public ActionNode setMode(int mode) {
        this.mode = mode;
        return this;
    }

    /** {@inheritdoc} */
    @Override
    protected MenuNode getPopupNode() {
        return this.node;
    }

    protected int getElementShaderMode() {
        if (this.mode == ActionNode.MODE_DISABLED) {
            return ElementShader.GRAY_MODE;
        }
        return this.popup.isHovered() ? ElementShader.BRIGHT_MODE : ElementShader.DEFAULT_MODE;
    }

    /** {@inheritdoc} */
    @Override
    public void click(Rect bounds, Point p) {
        if (this.mode == ActionNode.MODE_ACTIVE && bounds.contains(p)) {
            this.av.loaders.sounds.play("sfx/arrow");
            this.action.run();
        }
    }
}
