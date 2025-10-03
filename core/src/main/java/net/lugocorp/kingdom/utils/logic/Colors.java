package net.lugocorp.kingdom.utils.logic;
import com.badlogic.gdx.graphics.Color;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility to convert between Colors and hexadecimal integers
 */
public class Colors {
    private static Map<Integer, Color> cache = new HashMap<>();

    /**
     * Converts a hex value into a LibGDX Color instance
     */
    public static Color fromHex(int hexcode) {
        if (!Colors.cache.containsKey(hexcode)) {
            final float r = ((hexcode & 0xff0000) >> 16) / 255f;
            final float g = ((hexcode & 0x00ff00) >> 8) / 255f;
            final float b = (hexcode & 0x0000ff) / 255f;
            Colors.cache.put(hexcode, new Color(r, g, b, 1f));
        }
        return Colors.cache.get(hexcode);
    }

    /**
     * Converts some Color value into its integer equivalent
     */
    public static int asInt(Color c) {
        return (((int) (c.r * 255f)) << 16) + (((int) (c.g * 255f)) << 8) + (int) (c.b * 255);
    }
}
