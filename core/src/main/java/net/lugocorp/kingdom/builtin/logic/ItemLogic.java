package net.lugocorp.kingdom.builtin.logic;
import net.lugocorp.kingdom.builtin.Events;
import net.lugocorp.kingdom.game.events.Event;
import net.lugocorp.kingdom.game.player.CompPlayer;
import net.lugocorp.kingdom.game.player.Player;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.code.SideEffect;

/**
 * This class contains utility functions for writing new Item effects
 */
public class ItemLogic {

    /**
     * Item that can be consumed to increase the Player's gold
     */
    public static SideEffect valuable(Event event) {
        Events.ItemConsumedEvent e = (Events.ItemConsumedEvent) event;
        return () -> e.consumer.getLeader().ifPresent((Player p) -> {
            if (p instanceof CompPlayer) {
                ((CompPlayer) p).stats.income.add(e.item.gold);
            }
            p.gold += e.item.gold;
        });
    }

    /**
     * Item that can be consumed to heal the consumer
     */
    public static SideEffect potion(GameView view, Event event, int points) {
        Events.ItemConsumedEvent e = (Events.ItemConsumedEvent) event;
        return () -> e.consumer.combat.heal(view, points);
    }

    /**
     * Item that can be consumed to stave off hunger
     */
    public static SideEffect food(GameView view, Event event) {
        Events.ItemConsumedEvent e = (Events.ItemConsumedEvent) event;
        if (e.consumer.hunger.canEat(e.item)) {
            return () -> e.consumer.hunger.eat(view, true);
        }
        e.consumed = false;
        return () -> view.logger.error("Item is not edible for this unit");
    }
}
