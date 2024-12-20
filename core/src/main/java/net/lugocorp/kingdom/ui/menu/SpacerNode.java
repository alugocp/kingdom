package net.lugocorp.kingdom.ui.menu;
import net.lugocorp.kingdom.engine.AudioVideo;
import net.lugocorp.kingdom.utils.math.Coords;
import net.lugocorp.kingdom.utils.math.Point;
import net.lugocorp.kingdom.utils.math.Rect;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

/**
 * This MenuNode separates different sections of a Menu
 */
public class SpacerNode implements MenuNode {
    private static int MARGIN = 25;

    /** {@inheritdoc} */
    @Override
    public int getHeight() {
        return (SpacerNode.MARGIN * 2) + 1;
    }

    /** {@inheritdoc} */
    @Override
    public void pack(int width) {
    }

    /** {@inheritdoc} */
    @Override
    public void draw(AudioVideo av, Rect bounds) {
        int y = Coords.SIZE.y - bounds.y - SpacerNode.MARGIN - 1;
        av.shapes.begin(ShapeType.Filled);
        av.shapes.setColor(Color.WHITE);
        av.shapes.rect(bounds.x, y, bounds.w, 1);
        av.shapes.end();
    }

    /** {@inheritdoc} */
    @Override
    public void click(Menu menu, Rect bounds, Point p) {
    }
}
