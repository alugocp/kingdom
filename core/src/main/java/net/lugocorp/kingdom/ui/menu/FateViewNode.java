package net.lugocorp.kingdom.ui.menu;
import net.lugocorp.kingdom.engine.Graphics;
import net.lugocorp.kingdom.game.model.Fate;

/**
 * A node that displays some Fate and its description
 */
public class FateViewNode extends RowNode {
    private final ListNode desc = new ListNode();
    private final FateNode fate;
    private int width = 0;

    public FateViewNode(Graphics graphics, Fate initial) {
        this.fate = new FateNode(graphics, initial, () -> {
        });
        this.add(this.fate).add(this.desc);
        this.setFate(graphics, initial);
    }

    /**
     * Changes the Fate that this FateViewNode is displaying
     */
    public void setFate(Graphics graphics, Fate fate) {
        this.fate.setFate(graphics, fate);
        this.desc.clear();
        this.desc.add(new HeaderNode(graphics, fate.name));
        for (String s : fate.desc) {
            this.desc.add(new TextNode(graphics, s));
        }
        if (this.width > 0) {
            this.pack(this.width);
        }
    }

    /** {@inheritdoc} */
    @Override
    public void pack(int width) {
        this.width = width;
        super.pack(width);
    }
}
