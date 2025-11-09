package net.lugocorp.kingdom.color;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
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

    /**
     * Interpolates between two color hex values
     */
    public static Color interpolate(int h1, int h2, float progress) {
        final int r1 = (h1 & 0xff0000) >> 16;
        final int g1 = (h1 & 0x00ff00) >> 8;
        final int b1 = (h1 & 0x0000ff);
        final int r2 = (h2 & 0xff0000) >> 16;
        final int g2 = (h2 & 0x00ff00) >> 8;
        final int b2 = (h2 & 0x0000ff);
        final int r = (int) ((r2 - r1) * progress) + r1;
        final int g = (int) ((g2 - g1) * progress) + g1;
        final int b = (int) ((b2 - b1) * progress) + b1;
        return Colors.fromHex((r << 16) + (g << 8) + b);
    }

    /**
     * Returns a transformation matrix that converts the origin color into the other
     * one
     */
    public static Matrix4 getRecolorMatrix(Color origin, Color dest) {
        final Quaternion q = new Quaternion().setFromCross(new Vector3(origin.r, origin.g, origin.b).nor(),
                new Vector3(dest.r, dest.g, dest.b).nor());
        final Vector3 v1 = new Vector3(origin.r, origin.g, origin.b).mul(q);
        final Vector3 v2 = new Vector3(dest.r, dest.g, dest.b);
        final float s = v2.len() / v1.len();
        return new Matrix4().set(new Vector3(), q, new Vector3(s, s, s));
    }
}
