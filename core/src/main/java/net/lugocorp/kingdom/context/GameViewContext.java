package net.lugocorp.kingdom.context;
import net.lugocorp.kingdom.ui.views.GameView;

/**
 * This Context exists within an active GameView
 */
public class GameViewContext extends BaseContext {
    public final GameView view;

    public GameViewContext(BaseContext prev, GameView view) {
        super(prev.events, prev.generator, prev.navigate, prev.serial, prev.av, prev.mods);
        this.view = view;
    }
}
