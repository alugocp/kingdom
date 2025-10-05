package net.lugocorp.kingdom.ui.nodes;
import net.lugocorp.kingdom.ui.Menu;
import net.lugocorp.kingdom.ui.MenuNode;
import net.lugocorp.kingdom.ui.MenuPopup;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.math.Point;
import net.lugocorp.kingdom.utils.math.Rect;
import java.util.Optional;

/**
 * Can be used to represent an Ability or the move button
 */
public class ActionNode extends ButtonNode {
    private final MenuPopup popup = new MenuPopup();
    private final Optional<MenuNode> desc;
    private final GameView view;

    public ActionNode(GameView view, String name, Optional<String> desc, boolean active, Runnable action) {
        super(view.av, name, action);
        this.enable(active);
        this.desc = desc.map((String s) -> new TextNode(view.av, s));
        this.view = view;
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
        this.desc.ifPresent((MenuNode n) -> this.popup.update(bounds, prev, curr, n));
    }
}
