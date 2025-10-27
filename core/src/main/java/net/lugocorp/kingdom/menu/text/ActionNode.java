package net.lugocorp.kingdom.menu.text;
import net.lugocorp.kingdom.engine.AudioVideo;
import net.lugocorp.kingdom.math.Point;
import net.lugocorp.kingdom.math.Rect;
import net.lugocorp.kingdom.menu.Menu;
import net.lugocorp.kingdom.menu.MenuNode;
import net.lugocorp.kingdom.menu.MenuPopup;
import java.util.Optional;

/**
 * Can be used to represent an Ability or the move button
 */
public class ActionNode extends ButtonNode {
    private final MenuPopup popup = new MenuPopup();
    private final Optional<MenuNode> desc;

    public ActionNode(AudioVideo av, String name, Optional<String> desc, Runnable action) {
        super(av, name, action);
        this.desc = desc.map((String s) -> new TextNode(av, s));
    }

    /** {@inheritdoc} */
    @Override
    public void pack(Menu menu, int width) {
        super.pack(menu, width);
        this.popup.setMenu(menu);
    }

    /** {@inheritdoc} */
    @Override
    public void mouseMoved(Rect bounds, Point prev, Point curr) {
        super.mouseMoved(bounds, prev, curr);
        this.desc.ifPresent((MenuNode n) -> this.popup.update(bounds, curr, n));
    }
}
