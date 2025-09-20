package net.lugocorp.kingdom.game.actions;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.game.player.Player;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.math.Point;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Contains all logic for a Unit's Action(s) taken each turn
 */
public class ActionManager {
    private final Map<Unit, Action> actions = new HashMap<>();

    /**
     * Run this function at the end of the turn (purges stale Actions)
     */
    public void endOfTurn(Player p) {
        final Set<Unit> acted = new HashSet<>();
        acted.addAll(this.actions.keySet());
        for (Unit u : acted) {
            if (u.leadership.belongsToPlayer(p) && !this.actions.get(u).endOfTurn()) {
                this.actions.remove(u);
            }
        }
    }

    /**
     * Returns how far the given Unit has moved this turn (returns -1 if the unit
     * has done something else this turn)
     */
    private int unitMovedDistance(Unit u) {
        if (this.actions.containsKey(u)) {
            Action a = this.actions.get(u);
            if (a.getType() == ActionType.MOVE) {
                return ((MoveAction) a).getDistance();
            }
            if (a.getType() == ActionType.ACTIVATE) {
                return -1;
            }
        }
        return 0;
    }

    /**
     * Returns the remaining number of spaces the given Unit can move
     */
    public int getRemainingMoveDistance(GameView view, Unit u) {
        final int max = u.movement.getMaxDistance(view);
        if (!this.hasUnitActed(u)) {
            return max;
        }
        final int moved = this.unitMovedDistance(u);
        return moved >= 0 ? max - moved : 0;
    }

    /**
     * Returns true if the given Unit has acted yet this turn
     */
    public boolean hasUnitActed(Unit u) {
        return this.actions.containsKey(u);
    }

    /**
     * Returns true if thie given Unit can make an Action of the following type
     */
    public boolean canUnitDoThis(Unit u, ActionType type) {
        return !this.hasUnitActed(u) || this.actions.get(u).canBeFollowedBy(type);
    }

    /**
     * Marks a Unit as having acted this turn
     */
    public void unitHasActed(GameView view, Unit u, Action a) {
        if (this.actions.containsKey(u)) {
            this.actions.put(u, this.actions.get(u).followedBy(a));
        } else {
            this.actions.put(u, a);
            a.addedFirst();
            if (u.leadership.belongsToHuman()) {
                this.goToNextUnit(view);
            }
        }
    }

    /**
     * Returns the ActionType of the Action associated with the given Unit (if any)
     */
    public Optional<ActionType> getUnitActionType(Unit u) {
        return this.actions.containsKey(u) ? Optional.of(this.actions.get(u).getType()) : Optional.empty();
    }

    /**
     * Opens a TileMenu on the next Unit who must act this turn
     */
    public boolean goToNextUnit(GameView view) {
        Optional<Unit> next = this.getNextUnitToAct(view.game.human);
        if (next.isPresent()) {
            final Point p = next.get().getPoint();
            view.centerOnPoint(p, false);
            view.selector.hover(p);
            view.menu.open(p);
            return true;
        }
        return false;
    }

    /**
     * Returns the next Unit that must act this turn
     */
    private Optional<Unit> getNextUnitToAct(Player player) {
        // TODO optimize this
        for (Unit u : player.units) {
            if (!this.hasUnitActed(u) && !u.sleep.isSleeping()) {
                return Optional.of(u);
            }
        }
        return Optional.empty();
    }

    /**
     * Returns a string describing the given Unit's Action for this turn
     */
    public String getUnitActionLabel(Unit u) {
        if (this.actions.containsKey(u)) {
            return this.actions.get(u).getDescription();
        }
        return "This unit has not acted this turn";
    }
}
