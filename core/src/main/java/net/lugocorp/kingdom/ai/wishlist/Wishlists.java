package net.lugocorp.kingdom.ai.wishlist;
import net.lugocorp.kingdom.game.player.CompPlayer;
import net.lugocorp.kingdom.ui.views.GameView;

/**
 * This class organizes all wishlists for an AI Player
 */
public class Wishlists {
    public final ArtifactWishlist artifacts;
    public final PatronWishlist patrons;
    public final GlyphWishlist glyphs;
    public final UnitWishlist units;

    public Wishlists(GameView view, CompPlayer player) {
        this.artifacts = new ArtifactWishlist(view, player);
        this.patrons = new PatronWishlist(view, player.getActor());
        this.units = new UnitWishlist(view, player.getActor());
        this.glyphs = new GlyphWishlist(player.getActor());
    }
}
