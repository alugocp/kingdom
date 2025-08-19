package net.lugocorp.kingdom.game.combat;
import net.lugocorp.kingdom.game.events.EventReceiver;
import net.lugocorp.kingdom.game.model.Building;
import net.lugocorp.kingdom.game.model.Tile;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.game.player.Player;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.code.SideEffect;
import java.util.Optional;

/**
 * This class handles special combat rules for Buildings
 */
public class BuildingCombat extends Combat<Building> {

    public BuildingCombat(Building bearer) {
        super(bearer);
    }

    /**
     * This should only be used in conjunction with Kryo rehydration
     */
    public BuildingCombat() {
        super();
    }

    /** {@inheritdoc} */
    @Override
    protected <A extends EventReceiver> SideEffect onDeath(GameView view, A attacker) {
        // Restore the Building's health and place under the attacking Player's control
        // if it was destroyed by another player
        Optional<Player> destroyer = this.getCombatantLeader(view, attacker);
        if (this.bearer.isActive() && !this.getBuildingLeader(view, this.bearer).equals(destroyer)) {
            return () -> {
                this.health.set(this.health.getMax());
                view.game.world.getTile(this.bearer.getPoint())
                        .ifPresent((Tile t) -> view.game.setLeader(t, destroyer));
            };
        }
        return super.onDeath(view, attacker);
    }

    /**
     * Returns the Player that commands the given Building, if there is one
     */
    private Optional<Player> getBuildingLeader(GameView view, Building b) {
        return view.game.world.getTile(b.getPoint()).flatMap((Tile t) -> t.leader);
    }

    /**
     * Returns the Player that commands the given combatant, if there is one
     */
    private <C extends EventReceiver> Optional<Player> getCombatantLeader(GameView view, C combatant) {
        if (combatant instanceof Building) {
            return this.getBuildingLeader(view, (Building) combatant);
        }
        if (combatant instanceof Unit) {
            Unit u = (Unit) combatant;
            return u.getLeader();
        }
        return Optional.empty();
    }
}
