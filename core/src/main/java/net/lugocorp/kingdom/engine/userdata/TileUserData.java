package net.lugocorp.kingdom.engine.userdata;
import net.lugocorp.kingdom.game.glyph.GlyphCategory;
import com.badlogic.gdx.graphics.Color;
import java.util.Optional;

/**
 * Data for Tile's userData field
 */
public class TileUserData {

    // Displays a faint glowing Glyph on the top face
    public Optional<GlyphCategory> glyph = Optional.empty();

    // Adds a wave effect to the top face texture
    public boolean wave = false;

    // This int tracks which sides should display a border
    public Color borderColor = Color.BLACK;
    public int borders = 0;

    // This int tracks which sides should display a Patron domain border
    public int domainBorders = 0;

    // Fog of war system
    public boolean hasBeenSeen = false;
    public int vision = 0;

    // Renders the Tile selector
    public int selection = 0;

    /**
     * Returns an integer for the shader vision input
     */
    public int collapseVision() {
        return this.hasBeenSeen ? (this.vision == 0 ? 1 : 2) : 0;
    }
}
