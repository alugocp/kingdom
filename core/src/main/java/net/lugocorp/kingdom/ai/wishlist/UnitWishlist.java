package net.lugocorp.kingdom.ai.wishlist;
import net.lugocorp.kingdom.ai.Actor;
import net.lugocorp.kingdom.ai.action.Goal;
import net.lugocorp.kingdom.game.Game;
import net.lugocorp.kingdom.game.glyph.Glyph;
import net.lugocorp.kingdom.game.glyph.GlyphCategory;
import net.lugocorp.kingdom.game.model.Ability;
import net.lugocorp.kingdom.game.model.Tile;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.game.player.CompPlayer;
import net.lugocorp.kingdom.game.player.Player;
import net.lugocorp.kingdom.math.Point;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.Lambda;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Helps a CompPlayer decide which Unit to recruit
 */
public class UnitWishlist extends Wishlist<Unit> {
    private final Game game;

    public UnitWishlist(Game game, Actor actor) {
        super(actor);
        this.game = game;
    }

    // This constructor is only used for Kryo
    public UnitWishlist() {
        super(null);
        this.game = null;
    }

    /** {@inheritdoc} */
    @Override
    protected int getScoreForGoal(Unit option, Goal g) {
        int score = 0;
        for (Glyph glyph : option.glyphs.get()) {
            score += g.likesGlyph(glyph) ? 1 : 0;
        }
        for (Ability a : option.abilities.getPassives()) {
            for (String channel : this.getAbilityChannels(a)) {
                score += g.likesEventChannel(channel) ? 1 : 0;
            }
        }
        // TODO dry run active Ability click events and check against the resulting
        // events
        return score;
    }

    /**
     * Returns the Event channels associated with the given Ability
     */
    private Set<String> getAbilityChannels(Ability a) {
        return this.game.events.ability.getChannels(a.getStratifier());
    }

    /**
     * Returns the Point where the CompPlayer should spawn its new Unit
     */
    public Optional<Point> getSpawnPoint(GameView view, CompPlayer player, Glyph glyph) {
        // TODO make smarter spawn code based on what the AI will do with this unit
        // TODO will be optimized by the upcomin towers mechanic
        final Set<Point> options = new HashSet<>();
        for (Tile t : view.game.world) {
            if (t.leader.map((Player l) -> l.equals(player)).orElse(false) && !t.unit.isPresent()
                    && t.getGlyph().map((GlyphCategory gc) -> gc.contains(glyph)).orElse(false)) {
                options.add(t.getPoint());
            }
        }
        return options.size() > 0 ? Optional.of(Lambda.random(options)) : Optional.empty();
    }
}
