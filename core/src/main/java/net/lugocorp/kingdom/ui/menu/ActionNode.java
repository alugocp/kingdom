package net.lugocorp.kingdom.ui.menu;
import net.lugocorp.kingdom.engine.AudioVideo;
import java.util.Optional;

/**
 * Can be used to represent an Ability or the move button
 */
public class ActionNode extends ListNode {

    public ActionNode(AudioVideo av, String name, Optional<String> desc, boolean active, Runnable action) {
        this.addBorder().add(new ButtonNode(av, name, action).enable(active));
        desc.ifPresent((String s) -> this.add(new TextNode(av, s)));
    }
}
