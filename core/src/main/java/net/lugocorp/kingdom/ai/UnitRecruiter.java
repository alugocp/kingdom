package net.lugocorp.kingdom.ai;
import net.lugocorp.kingdom.game.model.glyph.Glyph;
import net.lugocorp.kingdom.game.player.CompPlayer;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.math.Point;
import java.util.Optional;

/**
 * Makes decisions for a CompPlayer when they need to recruit more Units
 */
public class UnitRecruiter {

    /**
     * Returns the Glyph that the CompPlayer should recruit from
     */
    public Optional<Glyph> glyphToSelect(CompPlayer player) {
        if (player.stats.naturalHarvest.getMean() < 1.5) {
            return Optional.of(Glyph.NATURE);
        }
        if (player.stats.otherHarvest.getMean() < 1.5) {
            return Optional.of(Glyph.MINING);
        }
        if (player.stats.enemiesKilled.getMean() <= 0.15) {
            return Optional.of(Glyph.BATTLE);
        }
        if (player.stats.unitsLost.getMean() >= 0.5 && player.stats.enemiesKilled.getMean() >= 0.25) {
            return Optional.of(Glyph.DEFENSE);
        }
        if (player.stats.unitsLost.getMean() >= 0.5) {
            return Optional.of(Glyph.HEALING);
        }
        if (player.stats.income.getAverage() < 10) {
            return Optional.of(Glyph.TRADE);
        }
        return Optional.empty();
    }

    /**
     * Returns the Point where the CompPlayer should spawn its new Unit
     */
    public Optional<Point> getSpawnPoint(GameView view, CompPlayer player, Glyph glyph) {
        // TODO AI implement me, I have no logic!
        return Optional.empty();
    }
}
