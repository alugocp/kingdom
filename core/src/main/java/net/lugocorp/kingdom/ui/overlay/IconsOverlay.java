package net.lugocorp.kingdom.ui.overlay;
import net.lugocorp.kingdom.engine.render.Drawable;
import net.lugocorp.kingdom.game.layers.Entity;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.game.player.Player;
import net.lugocorp.kingdom.game.properties.EntityType;
import net.lugocorp.kingdom.ui.views.GameView;
import com.badlogic.gdx.math.Vector3;
import java.util.ArrayList;
import java.util.List;

/**
 * An Overlay that represents a row of icons
 */
public class IconsOverlay extends Overlay {
    private final List<Drawable> icons = new ArrayList<>();
    private boolean alive = true;

    IconsOverlay(GameView view, Entity e) {
        super(e.getPoint(), new Vector3(0f, view.av.loaders.models.getModelHeight(e.getModelName()), 0f));
        if (e.getLeader().map((Player p) -> !p.isHumanPlayer()).orElse(false)) {
            this.icons.add(new Drawable(view.av.loaders.sprites, "glyph-icon-battle"));
            System.out.println("Enemy");
        }
        if (e.isEntityType(EntityType.UNIT) && ((Unit) e).hunger.getTurnsUntilGetsHungry(view) < 3) {
            this.icons.add(new Drawable(view.av.loaders.sprites, "glyph-icon-nature"));
            System.out.println("Hungry");
        }
        System.out.println(this.icons.size());
    }

    /**
     * Ends this Overlay's lifetime
     */
    public void dispel() {
        this.alive = false;
    }

    /** {@inheritdoc} */
    public boolean isDone() {
        return !this.alive || this.icons.size() == 0;
    }

    /** {@inheritdoc} */
    public void update(int dt) {
        // No-op
    }

    /** {@inheritdoc} */
    public void render(GameView view) {
        // TODO implement recolors
        // TODO fix the positioning
        // TODO correctly utilize the hunger flag
        // TODO draw finalized icons for this
        final float[] pos = this.getPosition(view);
        for (int a = 0; a < this.icons.size(); a++) {
            view.av.special.begin();
            // this.recolor.ifPresent((Matrix4 m) -> av.shaders.element.recolor(m));
            this.icons.get(a).render(view.av.special, (int) (pos[0] + (0.3f * a)), (int) pos[1]);
            view.av.special.end();
        }
    }
}
