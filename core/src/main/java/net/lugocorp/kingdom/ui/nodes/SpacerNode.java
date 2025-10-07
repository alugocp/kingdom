package net.lugocorp.kingdom.ui.nodes;
import net.lugocorp.kingdom.engine.AudioVideo;
import net.lugocorp.kingdom.ui.ColorScheme;
import net.lugocorp.kingdom.ui.MenuNode;
import net.lugocorp.kingdom.utils.math.Coords;
import net.lugocorp.kingdom.utils.math.Rect;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

/**
 * This MenuNode separates different sections of a Menu
 */
public class SpacerNode implements MenuNode {
    private static int MARGIN = 25;
    private final boolean visibleLine;

    public SpacerNode(boolean visibleLine) {
        this.visibleLine = visibleLine;
    }

    public SpacerNode() {
        this(true);
    }

    /** {@inheritdoc} */
    @Override
    public int getHeight() {
        return (SpacerNode.MARGIN * 2) + 1;
    }

    /** {@inheritdoc} */
    @Override
    public void draw(AudioVideo av, Rect bounds) {
        if (!this.visibleLine) {
            return;
        }
        int y = Coords.SIZE.y - bounds.y - SpacerNode.MARGIN - 1;
        av.shapes.begin(ShapeType.Filled);
        av.shapes.setColor(ColorScheme.OUTLINE.color);
        av.shapes.rect(bounds.x, y, bounds.w, 1);
        av.shapes.end();
    }
}
