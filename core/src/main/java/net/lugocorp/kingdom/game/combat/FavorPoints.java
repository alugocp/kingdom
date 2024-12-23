package net.lugocorp.kingdom.game.combat;
import net.lugocorp.kingdom.game.events.EventReceiver;
import net.lugocorp.kingdom.game.model.Building;
import net.lugocorp.kingdom.game.model.Patron;
import net.lugocorp.kingdom.game.model.Player;
import net.lugocorp.kingdom.ui.views.GameView;

/**
 * This class implements differences in how Patrons handle attacks/heals than
 * other Buildings
 */
public class FavorPoints extends HitPoints<Building> {
    private final Patron patron;

    public FavorPoints(Patron patron) {
        super(patron);
        this.patron = patron;
    }

    /**
     * This should only be used in conjunction with Kryo rehydration
     */
    public FavorPoints() {
        this.patron = null;
    }

    /**
     * Causes this object's Patron bearer to lose favor from its favorite Player
     */
    private void loseFavoritePlayerFavor(int points) {
        this.patron.getFavoritePlayer().ifPresent((Player p) -> this.patron.addFavor(p, -points));
    }

    /** {@inheritdoc} */
    @Override
    public void heal(int points) {
        this.loseFavoritePlayerFavor(points);
    }

    /** {@inheritdoc} */
    @Override
    public void takeDamage(GameView view, Damage dmg) {
        this.loseFavoritePlayerFavor(dmg.amount);
    }

    /** {@inheritdoc} */
    @Override
    public <T extends EventReceiver> void attack(GameView view, HitPoints<T> target, Damage dmg) {
        throw new RuntimeException("Patrons cannot attack");
    }

    /** {@inheritdoc} */
    @Override
    public boolean isDead() {
        return false;
    }
}
