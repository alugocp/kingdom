package net.lugocorp.kingdom.events;

/**
 * A neat package to contain all of our StratifiedEventReceivers
 */
public class EventHandlerBundle {
    public final StratifiedEventReceiver building = new StratifiedEventReceiver();
    public final StratifiedEventReceiver artifact = new StratifiedEventReceiver();
    public final StratifiedEventReceiver ability = new StratifiedEventReceiver();
    public final StratifiedEventReceiver item = new StratifiedEventReceiver();
    public final StratifiedEventReceiver unit = new StratifiedEventReceiver();
    public final StratifiedEventReceiver tile = new StratifiedEventReceiver();
}
