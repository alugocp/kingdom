package net.lugocorp.kingdom.engine.render;
import net.lugocorp.kingdom.engine.assets.SpriteLoader;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import java.util.Optional;

/**
 * Represents any object that can have an associated sprite
 */
public class Drawable {
    private final SpriteLoader sprites;
    private String spriteName = "PLACEHOLDER";
    protected Optional<TextureRegion> sprite = Optional.empty();

    public Drawable(SpriteLoader sprites) {
        this.sprites = sprites;
    }

    public Drawable(SpriteLoader sprites, String name) {
        this(sprites);
        this.setSprite(name);
    }

    /**
     * Triggers a new Model load request for this object
     */
    public void setSprite(String name) {
        this.sprite = Optional.empty();
        this.spriteName = name;
    }

    /**
     * Renders this Drawable's sprite if it has one
     */
    public void render(SpriteBatch batch, int x, int y) {
        if (!this.sprite.isPresent()) {
            this.sprite = this.sprites.getTextureRegion(this.spriteName);
        }
        this.sprite.ifPresent((TextureRegion sprite) -> batch.draw(sprite, x, y));
    }
}
