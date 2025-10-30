package net.lugocorp.kingdom.menu.game;
import net.lugocorp.kingdom.engine.AudioVideo;
import net.lugocorp.kingdom.game.model.Fate;
import net.lugocorp.kingdom.menu.Menu;
import net.lugocorp.kingdom.menu.icon.HelperNode;
import net.lugocorp.kingdom.menu.structure.ListNode;
import net.lugocorp.kingdom.menu.structure.RowNode;
import net.lugocorp.kingdom.menu.text.HeaderNode;
import net.lugocorp.kingdom.menu.text.SubheaderNode;
import net.lugocorp.kingdom.menu.text.TextNode;

/**
 * A node that displays some Fate and its description
 */
public class FateViewNode extends ListNode {
    private final ListNode desc = new ListNode();
    private final FateNode fate;
    private Menu menu = null;
    private int width = 0;

    public FateViewNode(AudioVideo av, Fate initial, boolean vertical) {
        this.fate = new FateNode(av, initial, () -> {
        });
        this.add(new HelperNode(av,
                "A fate is a set of abilities designed around a certain playstyle. Each player chooses their fate at the start of the game, and it cannot be changed once the game starts."));
        if (vertical) {
            this.add(this.fate).add(this.desc);
        } else {
            this.add(new RowNode().add(this.fate).add(this.desc));
        }
        this.setFate(av, initial);
    }

    /**
     * Changes the Fate that this FateViewNode is displaying
     */
    public void setFate(AudioVideo av, Fate fate) {
        this.fate.setFate(av, fate);
        this.desc.clear();
        this.desc.add(new HeaderNode(av, fate.name));
        boolean first = true;
        for (String s : fate.desc) {
            this.desc.add(first ? new SubheaderNode(av, s) : new TextNode(av, s));
            first = false;
        }
        if (this.width > 0) {
            this.pack(this.menu, this.width);
        }
    }

    /** {@inheritdoc} */
    @Override
    public void pack(Menu menu, int width) {
        super.pack(menu, width);
        this.width = width;
        this.menu = menu;
    }
}
