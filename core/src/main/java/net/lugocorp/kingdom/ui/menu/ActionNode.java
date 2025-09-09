package net.lugocorp.kingdom.ui.menu;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.math.Point;
import net.lugocorp.kingdom.utils.math.Rect;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import java.util.Optional;

/**
 * Can be used to represent an Ability or the move button
 */
public class ActionNode extends ButtonNode {
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
    protected BitmapFont getFont() {
        if (!this.isEnabled() && this.isHovered()) {
            return this.av.fonts.getFont(24, 0xaaaaaa);
        }
        return super.getFont();
    }

    /** {@inheritdoc} */
    @Override
    public void mouseMoved(Rect bounds, Point prev, Point curr) {
        super.mouseMoved(bounds, prev, curr);
        if (!this.desc.isPresent()) {
            return;
        }

        // Perform logic for the description popup
        final boolean prevIn = bounds.contains(prev);
        final boolean currIn = bounds.contains(curr);
        // TODO make this work for any menu, not just the game tile menu
        Menu menu = this.view.menu.get().get();
        if (currIn) {
            menu.setMiniMenu(this.desc.get(), curr.x + 25, curr.y + 15);
        } else if (prevIn) {
            menu.closeMiniMenu();
        }
    }
}
