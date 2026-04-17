package net.lugocorp.kingdom.ai.wishlist;
import net.lugocorp.kingdom.ai.Actor;
import net.lugocorp.kingdom.ai.action.Goal;
import net.lugocorp.kingdom.game.glyph.Glyph;

/**
 * Helps a CompPlayer decide which Glyph to recruit a Unit from
 */
public class GlyphWishlist extends Wishlist<Glyph> {

    public GlyphWishlist(Actor actor) {
        super(actor);
        this.setOptions(Glyph.values());
    }

    // This constructor is only used for Kryo
    public GlyphWishlist() {
        super(null);
    }

    /** {@inheritdoc} */
    @Override
    protected int getScoreForGoal(Glyph option, Goal g) {
        return g.likesGlyph(option) ? 1 : 0;
    }
}
