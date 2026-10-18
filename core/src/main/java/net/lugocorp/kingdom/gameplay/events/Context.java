package net.lugocorp.kingdom.gameplay.events;
import net.lugocorp.kingdom.gameplay.events.contexts.CreationContext;

/**
 * This Context splits our Event system into different stages of the
 * application's lifecycle
 */
public abstract class Context {
    private static CreationContext instance = new CreationContext();

    public static CreationContext getContext() {
        return Context.instance;
    }

    public static void setContext(CreationContext instance) {
        Context.instance = instance;
    }

    /**
     * Returns true if you can process the given Event within this Context
     */
    public boolean canHandle(Event e) {
        return e.contextClass.isAssignableFrom(this.getClass());
    }
}
