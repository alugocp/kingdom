package net.lugocorp.kingdom.ui.menu;
import net.lugocorp.kingdom.engine.AudioVideo;
import net.lugocorp.kingdom.game.model.Fate;

/**
 * A node that displays some Fate and its description
 */
public class FateViewNode extends RowNode {
    private final ListNode desc = new ListNode();
    private final FateNode fate;
    private Menu menu = null;
    private int width = 0;

    public FateViewNode(AudioVideo av, Fate initial) {
        this.fate = new FateNode(av, initial, () -> {
        });
        this.add(this.fate).add(this.desc);
        this.setFate(av, initial);
    }

    /**
     * Changes the Fate that this FateViewNode is displaying
     */
    public void setFate(AudioVideo av, Fate fate) {
        this.fate.setFate(av, fate);
        this.desc.clear();
        this.desc.add(new HeaderNode(av, fate.name));
        for (String s : fate.desc) {
            this.desc.add(new TextNode(av, s));
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
