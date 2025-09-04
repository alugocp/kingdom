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
     * Renders this MenuNode
     */
    public void draw(AudioVideo av, Rect bounds);

    /**
     * Called when we know how much width can be allotted to this MenuNode
     */
    public default void pack(int width) {
        // No-op
    }

    /**
     * Handles a click on this MenuNode
     */
    public default void click(Menu menu, Rect bounds, Point p) {
        // No-op
    }

    /**
     * Handles a click on any MenuNode that is not this MenuNode (default no-op)
     */
    public default void unclick() {
        // No-op
    }

    /**
     * Handles logic whenever the user moves their mouse
     */
    public default void mouseMoved(Rect bounds, Point prev, Point curr) {
        // No-op
    }

    /**
     * Handles a key press (default no-op)
     */
    public default void keyPressed(int keycode) {
        // No-op
    }
}
