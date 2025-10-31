package net.lugocorp.kingdom.menu.icon;
import net.lugocorp.kingdom.engine.AudioVideo;
import net.lugocorp.kingdom.menu.MenuNode;
import net.lugocorp.kingdom.menu.structure.ListNode;
import net.lugocorp.kingdom.menu.text.SubheaderNode;
import net.lugocorp.kingdom.menu.text.TextNode;

/**
 * An icon that displays a header and description when hovered
 */
public class HeaderDescNode extends IconNode {
    public static final int SIDE = 50;
    private final MenuNode desc;

    public HeaderDescNode(AudioVideo av, String icon, String header, String desc) {
        super(av, icon, HeaderDescNode.SIDE);
        this.desc = new ListNode().add(new SubheaderNode(av, header)).add(new TextNode(av, desc));
    }

    /** {@inheritdoc} */
    @Override
    protected MenuNode getPopupNode() {
        return this.desc;
    }
}
