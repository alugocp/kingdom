package net.lugocorp.kingdom.menu.icon;
import net.lugocorp.kingdom.engine.AudioVideo;
import net.lugocorp.kingdom.menu.MenuNode;
import net.lugocorp.kingdom.menu.text.TextNode;

/**
 * A friendly popup that explains the more advanced mechanics to players
 */
public class HelperNode extends IconNode {
    private final MenuNode desc;

    public HelperNode(AudioVideo av, MenuNode desc) {
        super(av, "help-icon");
        this.desc = desc;
    }

    public HelperNode(AudioVideo av, String desc) {
        this(av, new TextNode(av, desc));
    }

    /** {@inheritdoc} */
    @Override
    protected MenuNode getPopupNode() {
        return this.desc;
    }
}
