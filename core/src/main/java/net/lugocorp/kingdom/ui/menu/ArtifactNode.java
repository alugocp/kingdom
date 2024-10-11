package net.lugocorp.kingdom.ui.menu;
import net.lugocorp.kingdom.engine.GameGraphics;
import net.lugocorp.kingdom.engine.Graphics;
import net.lugocorp.kingdom.game.model.Artifact;
import net.lugocorp.kingdom.math.Coords;
import net.lugocorp.kingdom.math.Point;
import net.lugocorp.kingdom.math.Rect;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * A node that displays some Artifact
 */
public class ArtifactNode implements MenuNode {
    private static final int HEIGHT = 400;
    public static final int WIDTH = 300;
    private final TextureRegion image;
    private final Artifact artifact;
    private final TextNode text;

    public ArtifactNode(GameGraphics graphics, Artifact artifact) {
        this.artifact = artifact;
        this.text = new TextNode(graphics, String.format("%s: %s", artifact.name, artifact.desc));
        this.image = graphics.loaders.sprites.get(artifact.image.orElse("placeholder"));
    }

    /** {@inheritdoc} */
    @Override
    public int getHeight() {
        return ArtifactNode.HEIGHT;
    }

    /** {@inheritdoc} */
    @Override
    public void pack(int width) {
        this.text.pack(ArtifactNode.WIDTH);
    }

    /** {@inheritdoc} */
    @Override
    public void draw(Graphics graphics, Rect bounds) {
        // Draw background image
        Rect flip = Coords.screen.flip(bounds);
        graphics.sprites.begin();
        graphics.sprites.draw(this.image, flip.x, flip.y);
        graphics.sprites.end();

        // Draw foreground text
        int h = this.text.getHeight();
        this.text.draw(graphics, new Rect(bounds.x, bounds.y - h, bounds.w, h));
    }

    /** {@inheritdoc} */
    @Override
    public void click(Menu menu, Rect bounds, Point p) {
    }
}
