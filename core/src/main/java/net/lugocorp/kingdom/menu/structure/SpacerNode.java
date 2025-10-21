package net.lugocorp.kingdom.menu.structure;
import net.lugocorp.kingdom.engine.AudioVideo;
import net.lugocorp.kingdom.menu.MenuNode;
import net.lugocorp.kingdom.ui.ColorScheme;
import net.lugocorp.kingdom.utils.math.Coords;
import net.lugocorp.kingdom.utils.math.Rect;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

/**
 * This MenuNode separates different sections of a Menu
 */
public class SpacerNode implements MenuNode {
    private final boolean visibleLine;
    private int margin = 25;

    public SpacerNode(boolean visibleLine) {
        this.visibleLine = visibleLine;
    }

    public SpacerNode() {
        this(true);
    }

    /**
     * Cuts this SpacerNode in half vertically
     */
    public SpacerNode half() {
        this.margin = 12;
        return this;
    }

    /** {@inheritdoc} */
    @Override
    public int getHeight() {
        return (this.margin * 2) + 1;
    }

    /** {@inheritdoc} */
    @Override
    public void draw(AudioVideo av, Rect bounds) {
        if (!this.visibleLine) {
            return;
        }
        int y = Coords.SIZE.y - bounds.y - this.margin - 1;
        av.shapes.begin(ShapeType.Filled);
        av.shapes.setColor(ColorScheme.OUTLINE.color);
        av.shapes.rect(bounds.x, y, bounds.w, 1);
        av.shapes.end();
    }
}
