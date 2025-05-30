package net.lugocorp.kingdom.utils;
import com.badlogic.gdx.graphics.Color;

/**
 * Utility to handle Color values
 */
public class Colors {

    /**
     * Converts a hex value into a LibGDX Color instance
     */
    public static Color fromHex(int hexcode) {
        float r = ((hexcode & 0xff0000) >> 16) / 255f;
        float g = ((hexcode & 0x00ff00) >> 8) / 255f;
        float b = (hexcode & 0x0000ff) / 255f;
        return new Color(r, g, b, 1f);
    }
}
