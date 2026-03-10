package net.lugocorp.kingdom.gameplay.future;
import net.lugocorp.kingdom.gameplay.events.EventReceiver;

/**
 * Represents an Event trigger that should happen during some future turn
 */
public class FutureTick {
    // TODO this doesn't rehydrate quite right, units load hungry from save file
    final EventReceiver receiver;
    final String channel;
    final boolean repeat;
    final int interval;
    final int turn;

    FutureTick(EventReceiver receiver, String channel, int turn, int interval, boolean repeat) {
        this.receiver = receiver;
        this.interval = interval;
        this.channel = channel;
        this.repeat = repeat;
        this.turn = turn;
    }

    /**
     * This should only be used in conjunction with Kryo rehydration
     */
    public FutureTick() {
        this.receiver = null;
        this.interval = 0;
        this.channel = null;
        this.repeat = false;
        this.turn = 0;
    }
}
