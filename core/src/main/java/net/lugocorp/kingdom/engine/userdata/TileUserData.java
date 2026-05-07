package net.lugocorp.kingdom.engine.userdata;
import com.badlogic.gdx.graphics.Color;

/**
 * Data for Tile's userData field
 */
public class TileUserData {

    // Adds a wave effect to the top face texture
    public boolean wave = false;

    // Renders Domain borders
    public Color borderColor = Color.WHITE;
    public int borders = 0;

    // This int tracks which sides of the domain border should be extended
    public int domainExtensionBorders = 0;

    // Renders a Unit's projected movement path
    public int movePath = 0;
    public int pathLabel = 0;

    // Fog of war system
    public boolean hasBeenSeen = false;
    public int vision = 0;

    // Renders the Tile hover selector
    public int hovered = 0;

    // Renders the Tile option selector
    public boolean option = false;

    /**
     * Returns an integer for the shader vision input
     */
    public int collapseVision() {
        return this.hasBeenSeen ? (this.vision == 0 ? 1 : 2) : 0;
    }
}
