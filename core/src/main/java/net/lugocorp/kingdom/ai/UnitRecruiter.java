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
        // TODO AI implement me, I have no logic!
        return Optional.of(Glyph.BATTLE);
    }

    /**
     * Returns the Point where the CompPlayer should spawn its new Unit
     */
    public Optional<Point> getSpawnPoint(GameView view, CompPlayer player, Glyph glyph) {
        // TODO AI implement me, I have no logic!
        return Optional.empty();
    }
}
