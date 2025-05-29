package net.lugocorp.kingdom.engine.render.userdata;
import net.lugocorp.kingdom.game.model.GlyphCategory;
import java.util.Optional;

/**
 * Data for Tile's userData field
 */
public class TileUserData {
    public static final int BORDER_LEFT = 1;
    public static final int BORDER_RIGHT = 2;
    public static final int BORDER_TOP_LEFT = 4;
    public static final int BORDER_TOP_RIGHT = 8;
    public static final int BORDER_BOT_LEFT = 16;
    public static final int BORDER_BOT_RIGHT = 32;

    // Displays a faint glowing Glyph on the top face
    public Optional<GlyphCategory> glyph = Optional.empty();

    // Adds a wave effect to the top face texture
    public boolean wave = false;

    // This int tracks which sides should display a border
    public int borders = 0;
    // TODO add unique color for each Player

    // Fog of war system
    public boolean hasBeenSeen = false;
    public int visibility = 0;

    // Renders the Tile selector
    public int selection = 0;

    /**
     * Returns an integer for the shader visibility input
     */
    public int collapseVisibility() {
        return this.hasBeenSeen ? (this.visibility == 0 ? 1 : 2) : 0;
    }
}
