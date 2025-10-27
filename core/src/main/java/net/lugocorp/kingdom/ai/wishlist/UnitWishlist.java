package net.lugocorp.kingdom.ai.wishlist;
import net.lugocorp.kingdom.ai.Actor;
import net.lugocorp.kingdom.ai.action.Goal;
import net.lugocorp.kingdom.game.glyph.Glyph;
import net.lugocorp.kingdom.game.model.Ability;
import net.lugocorp.kingdom.game.model.Building;
import net.lugocorp.kingdom.game.model.Tile;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.game.player.CompPlayer;
import net.lugocorp.kingdom.game.properties.Inventory;
import net.lugocorp.kingdom.math.Point;
import net.lugocorp.kingdom.ui.views.GameView;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Helps a CompPlayer decide which Unit to recruit
 */
public class UnitWishlist extends Wishlist<Unit> {
    private final GameView view;

    public UnitWishlist(GameView view, Actor actor) {
        super(actor);
        this.view = view;
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
        return this.view.game.events.ability.getChannels(a.getStratifier());
    }

    /**
     * Returns the Point where the CompPlayer should spawn its new Unit
     */
    public Optional<Point> getSpawnPoint(CompPlayer player, Glyph glyph) {
        // Get the average value from the CompPlayer's vaults
        final Set<Point> vaults = this.view.game.getVaultBuildings(player);
        final Map<Point, Integer> vaultValues = new HashMap<>();
        float average = 0f;
        for (Point p : vaults) {
            int value = this.view.game.world.getTile(p).flatMap((Tile t) -> t.building).flatMap((Building b) -> b.items)
                    .map((Inventory i) -> i.getTotalGold()).orElse(0);
            vaultValues.put(p, value);
            average += value;
        }
        average /= (float) vaults.size();

        // Find the wealthiest vault with max 15% value over the average
        Optional<Point> chosen = Optional.empty();
        final float max = average * 1.15f;
        for (Point p : vaults) {
            float value = vaultValues.get(p);
            if (!chosen.isPresent() || (value <= max && value > vaultValues.get(chosen.get()))) {
                chosen = Optional.of(p);
            }
        }
        // TODO wow this is just broken, erase this function and completely rewrite when
        // we have SQL lookup for a Player's Tiles
        return Optional.empty();
    }
}
