package net.lugocorp.kingdom.ui.menu;
import net.lugocorp.kingdom.engine.AudioVideo;
import net.lugocorp.kingdom.utils.math.Point;
import net.lugocorp.kingdom.utils.math.Rect;

/**
 * Interface representing anything visible in a Menu
 */
public interface MenuNode {
    public int getHeight();
    public void pack(int width);
    public void draw(AudioVideo av, Rect bounds);
    public void click(Menu menu, Rect bounds, Point p);
}
