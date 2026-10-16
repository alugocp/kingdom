package net.lugocorp.kingdom.gameplay.events;

public abstract class Context {
    final String name;

    public Context(String name) {
        this.name = name;
    }

    public boolean canHandle(Event e) {
        return e.contextClass.isAssignableFrom(this.getClass());
    }
}
