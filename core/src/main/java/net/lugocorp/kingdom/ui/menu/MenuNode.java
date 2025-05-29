package net.lugocorp.kingdom.ui.menu;
import net.lugocorp.kingdom.engine.AudioVideo;
import net.lugocorp.kingdom.utils.math.Point;
import net.lugocorp.kingdom.utils.math.Rect;

/**
 * Interface representing anything visible in a Menu
 */
public interface MenuNode {
    /**
     * Returns a height value for this MenuNode
     */
    public int getHeight();

    /**
     * Called when we know how much width can be allotted to this MenuNode
     */
    public void pack(int width);

    /**
     * Renders this MenuNode
     */
    public void draw(AudioVideo av, Rect bounds);

    /**
     * Handles a click on this MenuNode
     */
    public void click(Menu menu, Rect bounds, Point p);

    /**
     * Handles a click on any MenuNode that is not this MenuNode (default no-op)
     */
    public default void unclick() {
        // No-op
    };

    /**
     * Handles a key press (default no-op)
     */
    public default void keyPressed(int keycode) {
        // No-op
    };
}
