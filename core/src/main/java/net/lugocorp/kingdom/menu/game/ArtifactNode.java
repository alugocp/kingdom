package net.lugocorp.kingdom.menu.game;
import net.lugocorp.kingdom.engine.AudioVideo;
import net.lugocorp.kingdom.engine.render.Drawable;
import net.lugocorp.kingdom.game.model.Artifact;
import net.lugocorp.kingdom.menu.Menu;
import net.lugocorp.kingdom.menu.MenuNode;
import net.lugocorp.kingdom.menu.text.BadgeNode;
import net.lugocorp.kingdom.menu.text.SubheaderNode;
import net.lugocorp.kingdom.menu.text.TextNode;
import net.lugocorp.kingdom.ui.ColorScheme;
import net.lugocorp.kingdom.utils.logic.Colors;
import net.lugocorp.kingdom.utils.math.Coords;
import net.lugocorp.kingdom.utils.math.Point;
import net.lugocorp.kingdom.utils.math.Rect;
import java.util.Optional;

/**
 * A node that displays some Artifact
 */
public class ArtifactNode implements MenuNode {
    private static final int MARGIN = 15;
    public static final int HEIGHT = 400;
    public static final int WIDTH = 300;
    private final Optional<Runnable> click;
    private final Artifact artifact;
    private final Drawable image;
    private final Drawable claimedMask;
    private final Drawable hoveredMask;
    private final Drawable mask;
    private final TextNode name;
    private final TextNode desc;
    private Optional<BadgeNode> ownership = Optional.empty();
    private boolean hovered = false;

    public ArtifactNode(AudioVideo av, Artifact artifact, Optional<Runnable> click) {
        String cost = String.format("%s chip", artifact.chips);
        if (artifact.chips > 1) {
            cost += "s";
        }
        this.click = click;
        this.artifact = artifact;
        this.desc = new TextNode(av, String.format("%s (costs %s)", artifact.desc, cost));
        // TODO make the sprites loader (or some other layer above it) generate/cache
        // Drawables for optimization
        this.image = new Drawable(av.loaders.sprites, artifact.image.orElse("placeholder"));
        this.claimedMask = new Drawable(av.loaders.sprites, "artifact-claimed-mask");
        this.hoveredMask = new Drawable(av.loaders.sprites, "artifact-hovered-mask");
        this.mask = new Drawable(av.loaders.sprites, "artifact-mask");
        if (artifact.isClaimed()) {
            this.ownership = Optional.of(new BadgeNode(av, Colors.asInt(this.artifact.getOwner().get().color),
                    ColorScheme.WHITE.hex, this.artifact.getOwner().get().name));
        }
        this.name = new SubheaderNode(av, artifact.name);
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
        this.ownership.ifPresent((BadgeNode b) -> b.pack(menu, w));
        this.desc.pack(menu, w);
        this.name.pack(menu, w);
    }

    /** {@inheritdoc} */
    @Override
    public void draw(AudioVideo av, Rect bounds) {
        // Draw background image and any necessary masks
        Rect flip = Coords.screen.flip(bounds);
        av.sprites.begin();
        this.image.render(av.sprites, flip.x, flip.y);
        this.mask.render(av.sprites, flip.x, flip.y);
        if (this.artifact.isClaimed()) {
            this.claimedMask.render(av.sprites, flip.x, flip.y);
        } else if (this.hovered) {
            this.hoveredMask.render(av.sprites, flip.x, flip.y);
        }
        av.sprites.end();

        // Draw foreground text
        final int h1 = this.desc.getHeight();
        this.desc.draw(av,
                new Rect(bounds.x + ArtifactNode.MARGIN, bounds.y + bounds.h - h1 - ArtifactNode.MARGIN, bounds.w, h1));
        final int h2 = this.name.getHeight();
        this.name.draw(av, new Rect(bounds.x + ArtifactNode.MARGIN, bounds.y + bounds.h - h1 - h2 - ArtifactNode.MARGIN,
                bounds.w, h2));
        this.ownership.ifPresent((BadgeNode b) -> {
            final int h3 = b.getHeight();
            b.draw(av, new Rect(bounds.x + ArtifactNode.MARGIN + 5,
                    bounds.y + bounds.h - h1 - h2 - h3 - ArtifactNode.MARGIN, bounds.w, h3));
        });
    }

    /** {@inheritdoc} */
    @Override
    public void click(Rect bounds, Point p) {
        if (bounds.contains(p)) {
            this.click.ifPresent((Runnable r) -> r.run());
        }
    }

    /** {@inheritdoc} */
    @Override
    public void mouseMoved(Rect bounds, Point prev, Point curr) {
        final boolean currIn = bounds.contains(curr);
        if (currIn && !this.hovered) {
            this.hovered = true;
        }
        if (!currIn && this.hovered) {
            this.hovered = false;
        }
    }
}
