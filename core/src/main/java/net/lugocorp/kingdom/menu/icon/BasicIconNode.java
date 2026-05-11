package net.lugocorp.kingdom.menu.icon;
import net.lugocorp.kingdom.engine.AudioVideo;
import net.lugocorp.kingdom.menu.MenuNode;

/**
 * An icon that displays some MenuNode when hovered
 */
public class BasicIconNode extends IconNode {
    private final MenuNode desc;

    public BasicIconNode(AudioVideo av, String icon, MenuNode desc) {
        super(av, icon, IconNode.SIDE);
        this.desc = desc;
    }

    /** {@inheritdoc} */
    @Override
    protected MenuNode getPopupNode() {
        return this.desc;
    }
}
