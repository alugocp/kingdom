package net.lugocorp.kingdom.ai.analysis;
import net.lugocorp.kingdom.builtin.Events;
import net.lugocorp.kingdom.game.glyph.Glyph;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.game.model.UnitDefaults;
import net.lugocorp.kingdom.gameplay.combat.Damage;
import net.lugocorp.kingdom.ui.views.GameView;

/**
 * Contains logic for the AI to analyze Units
 */
public class UnitAnalysis {

    /**
     * Returns true if the Unit is fast
     */
    public static boolean isFast(GameView view, Unit u) {
        return u.movement.getMaxDistance(view) > UnitDefaults.SPEED;
    }

    /**
     * Returns true if the Unit has defensive capabilities
     */
    public static boolean isDefensive(GameView view, Unit u) {
        if (u.glyphs.has(Glyph.DEFENSE) || u.combat.health.getMax() > UnitDefaults.HEALTH) {
            return true;
        }

        // Check for an armor ability
        final int test = 10;
        Events.TakeDamageEvent evt = new Events.TakeDamageEvent(u, new Damage(test));
        u.handleEvent(view, evt);
        return evt.dmg.base < test;
    }
}
