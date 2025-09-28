package net.lugocorp.kingdom.ui.nodes;
import net.lugocorp.kingdom.engine.AudioVideo;
import net.lugocorp.kingdom.engine.render.Drawable;
import net.lugocorp.kingdom.game.model.Artifact;
import net.lugocorp.kingdom.ui.Menu;
import net.lugocorp.kingdom.ui.MenuNode;
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
    private final ArtifactNameNode name;
    private final TextNode desc;

    public ArtifactNode(AudioVideo av, Artifact artifact) {
        String cost = String.format("%s chip", artifact.chips);
        if (artifact.chips > 1) {
            cost += "s";
        }
        this.artifact = artifact;
        this.name = new ArtifactNameNode(av, artifact.name);
        this.desc = new TextNode(av, String.format("%s (costs %s)", artifact.desc, cost));
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
        final int w = ArtifactNode.WIDTH - (ArtifactNode.MARGIN * 2);
        this.desc.pack(menu, w);
        this.name.pack(menu, w);
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
        final int h1 = this.desc.getHeight();
        this.desc.draw(av,
                new Rect(bounds.x + ArtifactNode.MARGIN, bounds.y + bounds.h - h1 - ArtifactNode.MARGIN, bounds.w, h1));
        final int h2 = this.name.getHeight();
        this.name.draw(av, new Rect(bounds.x + ArtifactNode.MARGIN, bounds.y + bounds.h - h1 - h2 - ArtifactNode.MARGIN,
                bounds.w, h2));
    }
}
