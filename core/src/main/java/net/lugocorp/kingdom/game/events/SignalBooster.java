package net.lugocorp.kingdom.game.events;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.code.SideEffect;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class handles "Signals" (Events that are processed by EventReceivers who
 * are not directly affected by that Event)
 */
public class SignalBooster {
    private final Map<String, List<EventReceiver>> receivers = new HashMap<>();

    /**
     * Broadcasts an Event with all listening EventReceivers
     */
    public SideEffect propagate(GameView view, EventReceiver original, Event e) {
        if (this.receivers.containsKey(e.channel)) {
            List<SideEffect> effects = new ArrayList<>();
            for (EventReceiver r : receivers.get(e.channel)) {
                if (r == original) {
                    // Prevent infinite loops
                    continue;
                }
                effects.add(r.handleEventWithoutSignalBooster(view, e));
            }
            return SideEffect.all(effects);
        }
        return SideEffect.none;
    }

    /**
     * Registers an EventReceiver that can listen on the entirety of the given
     * channel
     */
    public void addListener(String channel, EventReceiver r) {
        if (!this.receivers.containsKey(channel)) {
            this.receivers.put(channel, new ArrayList<EventReceiver>());
        }
        this.receivers.get(channel).add(r);
    }

    /**
     * Removes an EventReceiver from the channel listener group
     */
    public void removeListener(String channel, EventReceiver r) {
        if (this.receivers.containsKey(channel)) {
            this.receivers.get(channel).remove(r);
        }
    }

    /**
     * Removes an EventReceiver from all channel listener groups
     */
    public void deactivateListener(EventReceiver r) {
        for (String channel : this.receivers.keySet()) {
            this.removeListener(channel, r);
        }
    }
}
