package net.lugocorp.kingdom.context;

/**
 * Contexts split our Event system into different stages of the application's
 * lifecycle
 */
public abstract class Context {
    private static BaseContext instance = null;

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
