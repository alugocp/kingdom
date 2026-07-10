package net.lugocorp.kingdom.gameplay.actions;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.game.player.Player;
import net.lugocorp.kingdom.math.Point;
import net.lugocorp.kingdom.ui.overlay.ActionOverlay;
import net.lugocorp.kingdom.ui.views.GameView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Contains all logic for a Unit's Action(s) taken each turn
 */
public class ActionManager {
    private final Map<Unit, ActionOverlay> overlays = new HashMap<>();
    private final Map<Unit, Action> actions = new HashMap<>();

    /**
     * Call this when we're reloading this instance from saved game state
     */
    public void rehydrateFromKryo(GameView view) {
        for (Action a : this.actions.values()) {
            if (a instanceof MoveAction) {
                ((MoveAction) a).rehydrateFromKryo(view);
            }
        }
    }

    /**
     * Run this function at the end of the turn (purges stale Actions)
     */
    public void turnTransition(Player ending, Player starting) {
        final Set<Unit> acted = new HashSet<>();
        acted.addAll(this.actions.keySet());
        for (Unit u : acted) {
            final boolean atEnd = u.leadership.belongsToPlayer(ending);
            final boolean atStart = u.leadership.belongsToPlayer(starting);
            if (atEnd || atStart) {
                if ((atStart && this.actions.get(u).startOfTurn()) || (atEnd && this.actions.get(u).endOfTurn())) {
                    this.overlays.get(u).setChar(this.getActionOverlayChar(u));
                } else {
                    this.removeUnitInfo(u);
                }
            }
        }
    }

    /**
     * Removes the given Unit's information from this instance
     */
    public void removeUnitInfo(Unit u) {
        this.removeActionOverlay(u);
        this.actions.remove(u);
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
        if (!this.unitHasAssignedAction(u)) {
            return max;
        }
        final int moved = this.unitMovedDistance(u);
        return moved >= 0 ? max - moved : 0;
    }

    /**
     * Returns true if the given Unit has an assigned action for this turn
     */
    public boolean unitHasAssignedAction(Unit u) {
        return this.actions.containsKey(u);
    }

    /**
     * Returns true if the given Unit can make an Action of the following type
     */
    public boolean canUnitDoThis(Unit u, ActionType type) {
        return (type == ActionType.SKIP || !u.sleep.isSleeping())
                && (!this.unitHasAssignedAction(u) || this.actions.get(u).canBeFollowedBy(type));
    }

    /**
     * Returns a list of ActionTypes representing what the given Unit can do
     */
    public List<ActionType> getAvailableActionTypes(Unit u) {
        final List<ActionType> types = new ArrayList<>();
        for (ActionType type : ActionType.values()) {
            if (this.canUnitDoThis(u, type)) {
                types.add(type);
            }
        }
        return types;
    }

    /**
     * Calculates and sets an ActionOverlay for the given Unit
     */
    public void setActionOverlay(GameView view, Unit u) {
        final ActionOverlay o = new ActionOverlay(view, u, this.getActionOverlayChar(u));
        this.overlays.put(u, o);
        view.overlays.entity(u).setAction(o);
    }

    /**
     * Removes the given Unit's ActionOverlay from this instance
     */
    public void removeActionOverlay(Unit u) {
        if (this.overlays.containsKey(u)) {
            this.overlays.get(u).dispel();
        }
        this.overlays.remove(u);
    }

    /**
     * Marks a Unit as having acted this turn
     */
    public void unitHasActed(GameView view, Unit u, Action a) {
        if (this.actions.containsKey(u)) {
            // The Unit is acting a second (or further) time this turn
            this.actions.put(u, this.actions.get(u).followedBy(a));
            this.overlays.get(u).setChar(this.getActionOverlayChar(u));
        } else {
            // The Unit is taking its first action this turn
            this.actions.put(u, a);
            a.addedFirst();

            // Add Action state Overlay
            this.setActionOverlay(view, u);

            // Handle UI / pan to next Unit / auto complete
            if (u.leadership.belongsToHuman() && !this.goToNextUnit(view)) {
                view.hud.bot.turnButton.update(true, true);
            }
        }
    }

    /**
     * Returns true if all of the given Player's Units have acted this turn
     */
    public boolean allUnitsHaveActions(Player player) {
        int counter = 0;
        for (Unit u : this.actions.keySet()) {
            if (u.leadership.belongsToPlayer(player)) {
                counter++;
            }
        }
        return counter == player.units.size();
    }

    /**
     * Calls down into unitHasActed() when the Unit has cast a spell (syntactic
     * sugar)
     */
    public void unitHasCastSpell(GameView view, Unit u) {
        this.unitHasActed(view, u, new ActivateAction(view, u));
    }

    /**
     * Returns a character to display in the given Unit's ActionOverlay
     */
    private char getActionOverlayChar(Unit u) {
        return this.canUnitDoThis(u, ActionType.MOVE) ? '.' : 'z';
    }

    /**
     * Returns the ActionType of the Action associated with the given Unit (if any)
     */
    public Optional<ActionType> getUnitActionType(Unit u) {
        return this.actions.containsKey(u) ? Optional.of(this.actions.get(u).getType()) : Optional.empty();
    }

    /**
     * Opens a TileMenu on the next Unit who must act this turn (returns false if
     * there is no next Unit)
     */
    public boolean goToNextUnit(GameView view) {
        final Optional<Unit> next = this.getNextUnitToAct(view.game.human);
        if (next.isPresent()) {
            final Point p = next.get().getPoint();
            view.centerOnPoint(p, false);
            view.selector.hover(p);
            view.hud.bot.tileMenu.set(p);
            return true;
        }
        return false;
    }

    /**
     * Returns the next Unit that must act this turn
     */
    private Optional<Unit> getNextUnitToAct(Player player) {
        for (Unit u : player.units) {
            if (!this.unitHasAssignedAction(u) && !u.sleep.isSleeping()) {
                return Optional.of(u);
            }
        }
        return Optional.empty();
    }

    /**
     * Returns a string representing the given Unit's action state
     */
    public String getUnitActionLabel(Unit u) {
        if (this.actions.containsKey(u) || u.sleep.isSleeping()) {
            final char c = this.getActionOverlayChar(u);
            return String.format("%c%c%c", c, c, c);
        }
        return "Ready to act";
    }

    /**
     * Returns a string describing the given Unit's Action for this turn
     */
    public String getUnitActionDescription(Unit u) {
        if (this.actions.containsKey(u)) {
            return this.actions.get(u).getDescription();
        } else if (u.sleep.isSleeping()) {
            return "This unit cannot act due to a status condition";
        }
        return "This unit has not acted this turn";
    }
}
