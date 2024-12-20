package net.lugocorp.kingdom.ui.menu;
import net.lugocorp.kingdom.engine.Graphics;

/**
 * Can be used to represent an Ability or the move button
 */
public class ActionNode extends ListNode {

    public ActionNode(Graphics graphics, String name, String desc, boolean active, Runnable action) {
        this.addBorder().add(new ButtonNode(graphics, name, action).disable(!active)).add(new TextNode(graphics, desc));
    }
}
