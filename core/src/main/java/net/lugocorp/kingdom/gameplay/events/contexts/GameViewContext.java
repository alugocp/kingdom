package net.lugocorp.kingdom.gameplay.events.contexts;
import net.lugocorp.kingdom.gameplay.events.Context;
import net.lugocorp.kingdom.ui.views.GameView;

public class GameViewContext extends Context {
    public final GameView view;

    public GameViewContext(GameView view) {
        super("GameView");
        this.view = view;
    }
}
