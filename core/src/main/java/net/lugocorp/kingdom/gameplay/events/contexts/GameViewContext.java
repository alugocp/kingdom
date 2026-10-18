package net.lugocorp.kingdom.gameplay.events.contexts;
import net.lugocorp.kingdom.ui.views.GameView;

/**
 * This Context exists within an active GameView
 */
public class GameViewContext extends CreationContext {
    public final GameView view;

    public GameViewContext(CreationContext prev, GameView view) {
        super(prev.events, prev.generator);
        this.view = view;
    }
}
