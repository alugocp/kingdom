package net.lugocorp.kingdom.ui.menu;
import net.lugocorp.kingdom.engine.AudioVideo;

/**
 * Can be used to represent an Ability or the move button
 */
public class ActionNode extends ListNode {

    public ActionNode(AudioVideo av, String name, String desc, boolean active, Runnable action) {
        this.addBorder().add(new ButtonNode(av, name, action).disable(!active)).add(new TextNode(av, desc));
    }
}
