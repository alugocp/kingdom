package net.lugocorp.kingdom.gameplay.future;
import net.lugocorp.kingdom.builtin.Events;
import net.lugocorp.kingdom.game.Game;
import net.lugocorp.kingdom.game.player.Player;
import net.lugocorp.kingdom.gameplay.events.Event;
import net.lugocorp.kingdom.gameplay.events.EventReceiver;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.BatchCounter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * This class stores and processes future Events
 */
public class FutureEventManager {
    private final Map<Integer, BatchCounter<FutureTick>> futures = new HashMap<>();
    private final Game game;

    public FutureEventManager(Game game) {
        this.game = game;
    }

    // This constructor is only used for Kryo
    public FutureEventManager() {
        this.game = null;
    }

    /**
     * Sets up an Event that will trigger on the given EventReceiver in the given
     * number of turns
     */
    public void addFutureTick(String channel, EventReceiver receiver, int interval, boolean repeat,
            Optional<Player> playersTurn) {
        if (interval < 1) {
            throw new RuntimeException("You cannot set up a tick for the current or any past turns");
        }
        final int diff = playersTurn
                .map((Player p) -> this.game.mechanics.turns.getTurn().hasPlayerHadTurnYet(p) ? 1 : 0).orElse(0);
        final int turn = this.game.mechanics.turns.getTurn().getCounter() + interval + diff;
        if (!this.futures.containsKey(turn)) {
            this.futures.put(turn, new BatchCounter<FutureTick>(20));
        }
        this.futures.get(turn).list().add(new FutureTick(receiver, channel, turn, interval, repeat, playersTurn));
    }

    /**
     * Calls into addFutureTick() with an Event class
     */
    public <E extends Event> void addFutureTick(Class<E> channel, EventReceiver receiver, int interval, boolean repeat,
            Optional<Player> playersTurn) {
        this.addFutureTick(channel.getSimpleName(), receiver, interval, repeat, playersTurn);
    }

    /**
     * Returns a list of future Events by their receiver and maybe a channel
     */
    private List<FutureTick> getFutureTicks(EventReceiver receiver, Optional<String> channel) {
        List<FutureTick> lst = new ArrayList<>();
        for (BatchCounter<FutureTick> ticks : this.futures.values()) {
            for (FutureTick ft : ticks.list()) {
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
        for (BatchCounter<FutureTick> ticks : this.futures.values()) {
            int a = 0;
            while (a < ticks.list().size()) {
                if (ticks.list().get(a).receiver == receiver) {
                    ticks.processRemoval(a);
                    ticks.list().remove(a);
                } else {
                    a++;
                }
            }
        }
    }

    /**
     * Removes all upcoming FutureTicks registered for the given receiver on a
     * single channel
     */
    public void removeFutureTicks(EventReceiver receiver, String channel) {
        for (BatchCounter<FutureTick> ticks : this.futures.values()) {
            int a = 0;
            while (a < ticks.list().size()) {
                final FutureTick ft = ticks.list().get(a);
                if (ft.receiver == receiver && ft.channel.equals(channel)) {
                    ticks.processRemoval(a);
                    ticks.list().remove(a);
                } else {
                    a++;
                }
            }
        }
    }

    /**
     * Removes all upcoming FutureTicks registered to the given receiver and channel
     */
    public <E extends Event> void removeFutureTicks(EventReceiver receiver, Class<E> channel) {
        this.removeFutureTicks(receiver, channel.getSimpleName());
    }

    /**
     * Returns how many turns must pass before this Event triggers
     */
    public int getFutureEventRemainingTurns(EventReceiver receiver, String channel) {
        final List<FutureTick> ticks = this.getFutureTicks(receiver, Optional.of(channel));
        if (ticks.size() == 0) {
            return -1;
        }
        final int turn = this.game.mechanics.turns.getTurn().getCounter();
        return ticks.get(0).turn - turn;
    }

    /**
     * Removes a FutureTick from the queue and processes it
     */
    private void processFutureTick(GameView view, FutureTick ft) {
        this.futures.get(ft.turn).list().remove(ft);
        Events.RepeatedEvent e = new Events.RepeatedEvent(ft.channel, ft.interval, ft.repeat);
        ft.receiver.handleEvent(view, e).execute();
        if (e.repeat) {
            this.addFutureTick(ft.channel, ft.receiver, e.interval, true, ft.playersTurn);
        }
    }

    /**
     * Triggers an Event for every FutureTick that has reached its time (only a
     * certain amount per turn), and returns true if all FutureTicks for this turn
     * have been processed
     */
    public boolean checkFutureTicks(GameView view) {
        final int turn = this.game.mechanics.turns.getTurn().getCounter();
        if (this.futures.containsKey(turn)) {
            final BatchCounter<FutureTick> counter = this.futures.get(turn);
            final boolean isLast = counter.isLastBatch();
            for (FutureTick ft : counter.getBatch()) {
                this.processFutureTick(view, ft);
                counter.removed(1);
            }
            if (!isLast) {
                return false;
            }
        }
        return true;
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
