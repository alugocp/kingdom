package net.lugocorp.kingdom.ui;
import net.lugocorp.kingdom.utils.logic.Colors;
import com.badlogic.gdx.graphics.Color;

/**
 * This class contains a LibGDX Color and its hex value in one spot
 */
public class ColorPoint {
    public final Color color;
    public final int hex;

    ColorPoint(int hex) {
        this.color = Colors.fromHex(hex);
        this.hex = hex;
    }
}
