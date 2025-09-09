package net.lugocorp.kingdom.ui.menu;
import net.lugocorp.kingdom.engine.AudioVideo;
import net.lugocorp.kingdom.engine.render.Drawable;
import net.lugocorp.kingdom.game.model.Artifact;
import net.lugocorp.kingdom.utils.math.Coords;
import net.lugocorp.kingdom.utils.math.Rect;

/**
 * A node that displays some Artifact
 */
public class ArtifactNode implements MenuNode {
    private static final int MARGIN = 15;
    public static final int HEIGHT = 400;
    public static final int WIDTH = 300;
    private final Drawable image;
    private final Drawable mask;
    private final Artifact artifact;
    private final TextNode text;

    public ArtifactNode(AudioVideo av, Artifact artifact) {
        String cost = String.format("%s chip", artifact.chips);
        if (artifact.chips > 1) {
            cost += "s";
        }
        this.artifact = artifact;
        this.text = new TextNode(av, String.format("%s: %s (costs %s)", artifact.name, artifact.desc, cost));
        this.image = new Drawable(av.loaders.sprites, artifact.image.orElse("placeholder"));
        this.mask = new Drawable(av.loaders.sprites, "artifact-mask");
    }

    /** {@inheritdoc} */
    @Override
    public int getHeight() {
        return ArtifactNode.HEIGHT;
    }

    /** {@inheritdoc} */
    @Override
    public void pack(Menu menu, int width) {
        this.text.pack(menu, ArtifactNode.WIDTH - (ArtifactNode.MARGIN * 2));
    }

    /** {@inheritdoc} */
    @Override
    public void draw(AudioVideo av, Rect bounds) {
        // Draw background image
        Rect flip = Coords.screen.flip(bounds);
        av.sprites.begin();
        this.image.render(av.sprites, flip.x, flip.y);
        this.mask.render(av.sprites, flip.x, flip.y);
        av.sprites.end();

        // Draw foreground text
        int h = this.text.getHeight();
        this.text.draw(av,
                new Rect(bounds.x + ArtifactNode.MARGIN, bounds.y + bounds.h - h - ArtifactNode.MARGIN, bounds.w, h));
    }
}
