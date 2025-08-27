package net.lugocorp.kingdom.ai;
import net.lugocorp.kingdom.game.glyph.Glyph;
import net.lugocorp.kingdom.game.model.Building;
import net.lugocorp.kingdom.game.model.Tile;
import net.lugocorp.kingdom.game.player.CompPlayer;
import net.lugocorp.kingdom.game.properties.Inventory;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.math.Point;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

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
        // Get the average value from the CompPlayer's vaults
        final Set<Point> vaults = view.game.getVaultBuildings(player);
        final Map<Point, Integer> vaultValues = new HashMap<>();
        float average = 0f;
        for (Point p : vaults) {
            int value = view.game.world.getTile(p).flatMap((Tile t) -> t.building).flatMap((Building b) -> b.items)
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
        return Optional.empty();
    }
}
