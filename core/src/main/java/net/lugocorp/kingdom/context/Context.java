package net.lugocorp.kingdom.context;
import net.lugocorp.kingdom.game.model.Generator;
import net.lugocorp.kingdom.gameplay.events.AllEventHandlers;

/**
 * Contexts split our Event system into different stages of the application's
 * lifecycle
 */
public abstract class Context {
    private static BaseContext instance = new BaseContext(new AllEventHandlers(), new Generator());

    /**
     * Gets the global Context
     */
    public static BaseContext getContext() {
        return Context.instance;
    }

    /**
     * Sets the global Context
     */
    public static void setContext(BaseContext instance) {
        Context.instance = instance;
    }
}
