package net.lugocorp.kingdom.builtin.animation;
import net.lugocorp.kingdom.engine.animation.Animation;
import net.lugocorp.kingdom.engine.animation.Tween;
import net.lugocorp.kingdom.ui.game.TileMenu;
import net.lugocorp.kingdom.ui.menu.Menu;
import net.lugocorp.kingdom.ui.views.GameView;

/**
 * Animation that plays when a TileMenu is closed
 */
public class CloseTileMenuAnimation extends Animation {
    private final Runnable finish;
    private final Menu menu;

    public CloseTileMenuAnimation(Menu menu, Runnable finish) {
        super(new Tween().duration(250));
        this.finish = finish;
        this.menu = menu;
    }

    /** {@inheritdoc} */
    @Override
    protected void animate(float value) {
        this.menu.setX((int) (-TileMenu.WIDTH * value));
    }

    /** {@inheritdoc} */
    @Override
    public void onFinish(GameView view) {
        this.finish.run();
    }
}
