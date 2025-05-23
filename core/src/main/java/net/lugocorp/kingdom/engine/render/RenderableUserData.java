package net.lugocorp.kingdom.engine.render;
import net.lugocorp.kingdom.game.model.GlyphCategory;
import java.util.Optional;

/**
 * Standard data class that gets tagged into any Renderable's userData field
 */
public class RenderableUserData {
    public Optional<GlyphCategory> glyph = Optional.empty();
}
