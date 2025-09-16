package net.lugocorp.kingdom.game.future;
import net.lugocorp.kingdom.builtin.Events;
import net.lugocorp.kingdom.game.Game;
import net.lugocorp.kingdom.game.events.EventReceiver;
import net.lugocorp.kingdom.ui.views.GameView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * This class stores and processes future Events
 */
public class FutureEventManager {
    // TODO optimize the futures field with a different data structure
    private final Map<Integer, List<FutureTick>> futures = new HashMap<>();
    private final Game game;

    public FutureEventManager(Game game) {
        this.game = game;
    }

    /**
     * Sets up an Event that will trigger on the given EventReceiver in the given
     * number of turns
     */
    public void addFutureTick(String channel, EventReceiver receiver, int interval, boolean repeat) {
        if (interval < 1) {
            throw new RuntimeException("You cannot set up a tick for the current or any past turns");
        }
        final int turn = this.game.mechanics.turns.getTurn() + interval;
        if (!this.futures.containsKey(turn)) {
            this.futures.put(turn, new ArrayList<FutureTick>());
        }
        this.futures.get(turn).add(new FutureTick(receiver, channel, turn, interval, repeat));
    }

    /**
     * Returns a list of future Events by their receiver and maybe a channel
     */
    private List<FutureTick> getFutureTicks(EventReceiver receiver, Optional<String> channel) {
        List<FutureTick> lst = new ArrayList<>();
        for (List<FutureTick> ticks : this.futures.values()) {
            for (FutureTick ft : ticks) {
                if (ft.receiver == receiver && channel.map((String ch) -> ch == ft.channel).orElse(true)) {
                    lst.add(ft);
                }
            }
        }
        return lst;
    }

    /**
     * Removes all upcoming FutureTicks associated with the given EventReceiver
     */
    public void removeFutureTicks(EventReceiver receiver) {
        for (List<FutureTick> ticks : this.futures.values()) {
            int a = 0;
            while (a < ticks.size()) {
                if (ticks.get(a).receiver == receiver) {
                    ticks.remove(a);
                } else {
                    a++;
                }
            }
        }
    }

    /**
     * Removes all future Events registered for the given receiver on a single
     * channel
     */
    public void removeFutureEvents(EventReceiver receiver, String channel) {
        for (List<FutureTick> ticks : this.futures.values()) {
            int a = 0;
            while (a < ticks.size()) {
                FutureTick ft = ticks.get(a);
                if (ft.receiver == receiver && ft.channel.equals(channel)) {
                    ticks.remove(a);
                } else {
                    a++;
                }
            }
        }
    }

    /**
     * Returns how many turns must pass before this Event triggers
     */
    public int getFutureEventRemainingTurns(EventReceiver receiver, String channel) {
        final List<FutureTick> ticks = this.getFutureTicks(receiver, Optional.of(channel));
        if (ticks.size() == 0) {
            return -1;
        }
        final int turn = this.game.mechanics.turns.getTurn();
        return ticks.get(0).turn - turn;
    }

    /**
     * Removes a FutureTick from the queue and processes it
     */
    private void processFutureTick(GameView view, FutureTick ft) {
        this.futures.get(ft.turn).remove(ft);
        Events.RepeatedEvent e = new Events.RepeatedEvent(ft.channel, ft.interval, ft.repeat);
        ft.receiver.handleEvent(view, e).execute();
        if (e.repeat) {
            this.addFutureTick(ft.channel, ft.receiver, e.interval, true);
        }
    }

    /**
     * Triggers an Event for every FutureTick that has reached its time
     */
    public void checkFutureTicks(GameView view) {
        final int turn = this.game.mechanics.turns.getTurn();
        if (this.futures.containsKey(turn)) {
            final List<FutureTick> ls = new ArrayList<>();
            ls.addAll(this.futures.get(turn));
            for (FutureTick ft : ls) {
                this.processFutureTick(view, ft);
            }
        }
    }

    /**
     * Pulls out all FutureTicks as described and processes them right now
     */
    public void handleFutureTicksEarly(GameView view, EventReceiver receiver, String channel) {
        List<FutureTick> ticks = this.getFutureTicks(receiver, Optional.of(channel));
        for (FutureTick ft : ticks) {
            this.processFutureTick(view, ft);
        }
    }
}
