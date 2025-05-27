package net.lugocorp.kingdom.engine.render.userdata;
import net.lugocorp.kingdom.game.model.GlyphCategory;
import java.util.Optional;

/**
 * Data for Tile's userData field
 */
public class TileUserData {
    public Optional<GlyphCategory> glyph = Optional.empty();
    // TODO 3 states, "undiscovered", "unseen", and "seen"
    public boolean visible = true;
    public boolean wave = false;
}
