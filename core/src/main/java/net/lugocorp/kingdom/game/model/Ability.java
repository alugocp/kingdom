package net.lugocorp.kingdom.game.model;
import net.lugocorp.kingdom.builtin.Events;
import net.lugocorp.kingdom.color.Colors;
import net.lugocorp.kingdom.engine.controllers.Shortcut;
import net.lugocorp.kingdom.gameplay.actions.ActionType;
import net.lugocorp.kingdom.gameplay.events.Event;
import net.lugocorp.kingdom.gameplay.events.EventReceiver;
import net.lugocorp.kingdom.math.Point;
import net.lugocorp.kingdom.menu.MenuNode;
import net.lugocorp.kingdom.menu.MenuSubject;
import net.lugocorp.kingdom.menu.icon.ActionNode;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.SideEffect;
import com.badlogic.gdx.math.Matrix4;
import java.util.Optional;

/**
 * A passive or active effect that Units, Buildings and Tiles can use
 */
public class Ability implements EventReceiver, MenuSubject {
    private Optional<Shortcut> shortcut = Optional.empty();
    private Optional<Matrix4> recolor = Optional.empty();
    private String icon = "apple";
    public final Unit wielder;
    public final String name;
    public String desc = "";

    Ability(Unit wielder, String name) {
        this.wielder = wielder;
        this.name = name;
    }

    /**
     * This should only be used in conjunction with Kryo rehydration
     */
    public Ability() {
        this.wielder = null;
        this.name = null;
    }

    /**
     * Sets this Ability's icon
     */
    public void setIcon(String icon) {
        this.icon = icon;
    }

    /**
     * Sets this Ability's icon and its recolor matrix
     */
    public void setIcon(String icon, int hex1, int hex2) {
        this.recolor = Optional.of(Colors.getRecolorMatrix(Colors.fromHex(hex1), Colors.fromHex(hex2)));
        this.setIcon(icon);
    }

    /**
     * Sets the Shortcut associated with this Ability
     */
    public void setShortcut(Shortcut shortcut) {
        this.shortcut = Optional.of(shortcut);
    }

    /**
     * Activates this Ability (only does something if it's active)
     */
    public SideEffect activate(GameView view) {
        return this.handleEvent(view, new Events.AbilityActivatedEvent(this));
    }

    /**
     * Returns true fi this Ability can be activated
     */
    public boolean isActive(GameView view) {
        return this.hasEventHandler(view, Events.AbilityActivatedEvent.class);
    }

    /**
     * Returns true if this Ability has an EventHandler for the given channel
     */
    private <E extends Event> boolean hasEventHandler(GameView view, Class<E> eventClass) {
        return view.game.events.ability.hasEventHandler(this.getStratifier(), eventClass);
    }

    /**
     * Returns true if this Ability handles the Tick channel
     */
    public boolean hasTickHandler(GameView view) {
        return view.game.events.ability.hasEventHandler(this.getStratifier(), "Tick");
    }

    /** {@inheritdoc} */
    @Override
    public SideEffect handleEventWithoutSignalBooster(GameView view, Event e) {
        return view.game.events.ability.handle(view, this, e);
    }

    /** {@inheritdoc} */
    @Override
    public String getStratifier() {
        return this.name;
    }

    /** {@inheritdoc} */
    @Override
    public MenuNode getMenuContent(GameView view, Optional<Point> p) {
        final boolean canUnitDoThis = view.game.actions.canUnitDoThis(this.wielder, ActionType.ACTIVATE);
        int mode = ActionNode.MODE_NOTHING;
        if (this.isActive(view)) {
            mode = (this.wielder.leadership.belongsToHuman() && view.game.mechanics.turns.canHumanPlayerAct()
                    && canUnitDoThis) ? ActionNode.MODE_ACTIVE : ActionNode.MODE_DISABLED;
        }
        return new ActionNode(view.av, this.name, this.icon, this.shortcut,
                Optional.of(this.isActive(view) ? "Exhaustive action" : "Passive ability"), Optional.of(this.desc),
                () -> {
                    this.activate(view).execute();
                    view.hud.bot.tileMenu.refresh();
                }).setMode(mode).setRecolor(this.recolor);
    }
}
