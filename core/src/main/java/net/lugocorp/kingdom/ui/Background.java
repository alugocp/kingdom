package net.lugocorp.kingdom.ui;
import net.lugocorp.kingdom.engine.AudioVideo;
import net.lugocorp.kingdom.engine.render.Drawable;
import net.lugocorp.kingdom.math.Coords;
import net.lugocorp.kingdom.menu.Menu;

/**
 * Convenient class to tile a textured background over the game window
 */
public class Background {
    private final AudioVideo av;
    private final Drawable sprite;

    public Background(AudioVideo av) {
        this.sprite = new Drawable(av.loaders.sprites, "menu");
        this.av = av;
    }

    /**
     * Tiles the textured background sprite
     */
    public void render() {
        this.av.sprites.begin();
        for (int a = 0; a < Coords.SIZE.x; a += Menu.TEXTURE_SIDE) {
            for (int b = 0; b < Coords.SIZE.y; b += Menu.TEXTURE_SIDE) {
                this.sprite.render(this.av.sprites, a, b);
            }
        }
        this.av.sprites.end();
    }
}
