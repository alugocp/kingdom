package net.lugocorp.kingdom.gameplay.events;
import net.lugocorp.kingdom.game.model.Generator;
import net.lugocorp.kingdom.gameplay.events.contexts.CreationContext;

/**
 * TODO move this class (and all subclasses) into net.lugocorp.kingdom.context and merge in StartMenuView.Params. Then update PROJECT.md
 */

/**
 * This Context splits our Event system into different stages of the
 * application's lifecycle
 */
public abstract class Context {
    private static CreationContext instance = new CreationContext(new AllEventHandlers(), new Generator());

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
