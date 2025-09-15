package net.lugocorp.kingdom.builtin.animation;
import net.lugocorp.kingdom.engine.animation.Animation;
import net.lugocorp.kingdom.engine.animation.Tween;
import net.lugocorp.kingdom.ui.Menu;
import net.lugocorp.kingdom.ui.game.TileMenu;

/**
 * Animation that plays when a TileMenu is opened
 */
public class OpenTileMenuAnimation extends Animation {
    private final Menu menu;

    public OpenTileMenuAnimation(Menu menu) {
        super(new Tween().duration(250).desc());
        this.menu = menu;
    }

    /** {@inheritdoc} */
    @Override
    protected void animate(float value) {
        this.menu.setX((int) (-TileMenu.WIDTH * value));
    }
}
