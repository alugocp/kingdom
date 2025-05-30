package net.lugocorp.kingdom.utils;
import com.badlogic.gdx.graphics.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility to handle Color values
 */
public class Colors {
    static private final List<Color> pool = new ArrayList<>();

    static {
        Colors.pool.add(Colors.fromHex(0x00ff00)); // Green
        Colors.pool.add(Colors.fromHex(0xff0000)); // Red
        Colors.pool.add(Colors.fromHex(0x0000ff)); // Blue
        Colors.pool.add(Colors.fromHex(0x880088)); // Purple
        Colors.pool.add(Colors.fromHex(0xffff00)); // Yellow
        Colors.pool.add(Colors.fromHex(0xff7d00)); // Orange
        Colors.pool.add(Colors.fromHex(0x00ffff)); // Cyan
        Colors.pool.add(Colors.fromHex(0xff00ff)); // Pink
    }

    /**
     * Converts a hex value into a LibGDX Color instance
     */
    public static Color fromHex(int hexcode) {
        float r = ((hexcode & 0xff0000) >> 16) / 255f;
        float g = ((hexcode & 0x00ff00) >> 8) / 255f;
        float b = (hexcode & 0x0000ff) / 255f;
        return new Color(r, g, b, 1f);
    }

    /**
     * Retrieves a Color from the pool
     */
    public static Color getFromPool() {
        if (pool.size() == 0) {
            System.err.println("Warning: The color pool has run dry");
            return Color.BLACK;
        }
        return Colors.pool.remove(0);
    }

    /**
     * Returns a Color back to the pool
     */
    public static void releaseToPool(Color c) {
        Colors.pool.add(c);
    }
}
