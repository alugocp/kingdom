package net.lugocorp.kingdom.menu;
import net.lugocorp.kingdom.engine.Graphics;
import net.lugocorp.kingdom.math.Point;
import net.lugocorp.kingdom.math.Rect;

/**
 * Interface representing anything visible in a Menu
 */
public interface MenuNode {
    public int getHeight();
    public void pack(int width);
    public void draw(Graphics graphics, Rect bounds);
    public void click(Rect bounds, Point p);
}
