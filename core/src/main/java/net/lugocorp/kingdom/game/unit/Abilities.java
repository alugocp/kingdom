package net.lugocorp.kingdom.game.unit;
import net.lugocorp.kingdom.builtin.Events;
import net.lugocorp.kingdom.engine.controllers.Shortcut;
import net.lugocorp.kingdom.game.model.Ability;
import net.lugocorp.kingdom.game.model.Generator;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.Lambda;
import net.lugocorp.kingdom.utils.SideEffect;
import com.badlogic.gdx.Input.Keys;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * This class contains all of a Unit's Abilities
 */
public class Abilities {
    private final Unit unit;
    private final List<Ability> passives = new ArrayList<>();
    private Optional<Ability> active1 = Optional.empty();
    private Optional<Ability> active2 = Optional.empty();

    public Abilities(Unit unit) {
        this.unit = unit;
    }

    /**
     * Sets the active Abilities on this instance
     */
    public void setActive(Generator g, String a1, String a2) {
        this.active1 = Optional.of(g.ability(this.unit, a1));
        this.active2 = Optional.of(g.ability(this.unit, a2));
        this.active1.get().setShortcut(new Shortcut("1", Keys.NUM_1, Keys.NUMPAD_1));
        this.active2.get().setShortcut(new Shortcut("2", Keys.NUM_2, Keys.NUMPAD_2));
    }

    /**
     * Sets an active Ability on this instance
     */
    public void setActive(Generator g, String a) {
        this.active1 = Optional.of(g.ability(this.unit, a));
        this.active2 = Optional.empty();
        this.active1.get().setShortcut(new Shortcut("1", Keys.NUM_1, Keys.NUMPAD_1));
    }

    /**
     * Returns a List of this instance's active Abilities
     */
    public List<Ability> getActives() {
        final List<Ability> list = new ArrayList<>();
        this.active1.ifPresent((Ability a) -> list.add(a));
        this.active2.ifPresent((Ability a) -> list.add(a));
        return list;
    }

    /**
     * Sets the passive Abilities on this instance
     */
    public void setPassive(Generator g, String... passives) {
        this.passives.clear();
        for (String p : passives) {
            this.passives.add(g.ability(this.unit, p));
        }
    }

    /**
     * Returns a List of this instance's passive Abilities
     */
    public List<Ability> getPassives() {
        return this.passives;
    }

    /**
     * Returns true if this instance has a passive Ability of the given name
     */
    public boolean hasPassive(String name) {
        for (Ability a : this.passives) {
            if (a.name.equals(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true if this instance has the given status effect by name
     */
    public boolean hasStatusEffect(String status) {
        // TODO move status effects to their own logic one day
        return Lambda.some((Ability s) -> s.name.equals(status), this.passives);
    }

    /**
     * Adds a status effect (Ability under the hood) to this instance. Also triggers
     * a special Event on the Ability so it can kick off tick events
     */
    public SideEffect addStatusEffect(GameView view, String name) {
        // TODO move status effects to their own logic one day
        final Ability status = view.game.generator.ability(this.unit, name);
        return SideEffect.all(() -> this.passives.add(status),
                status.handleEvent(view, new Events.StatusEffectAddedEvent(status, this.unit)));
    }

    /**
     * Removes a status effect (Ability under the hood) from this instance
     */
    public void removeStatusEffect(Ability status) {
        // TODO move status effects to their own logic one day
        this.passives.remove(status);
    }

    /**
     * Removes a status effect from this instance by the given name
     */
    public void removeStatusEffect(String status) {
        // TODO move status effects to their own logic one day
        Optional<Ability> remove = Optional.empty();
        for (Ability s : this.passives) {
            if (s.name.equals(status)) {
                remove = Optional.of(s);
                break;
            }
        }
        remove.ifPresent((Ability s) -> this.removeStatusEffect(s));
    }
}
