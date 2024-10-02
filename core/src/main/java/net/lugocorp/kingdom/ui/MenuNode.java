package net.lugocorp.kingdom.ui;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import net.lugocorp.kingdom.math.Point;
import net.lugocorp.kingdom.math.Rect;

/**
 * Interface representing anything visible in a Menu
 */
interface MenuNode {
    public int getHeight();
    public void pack(int width);
    public void draw(SpriteBatch batch, ShapeRenderer shapes, Rect bounds);
    public void click(Rect bounds, Point p);
}
