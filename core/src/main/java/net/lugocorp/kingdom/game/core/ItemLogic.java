package net.lugocorp.kingdom.game.core;
import net.lugocorp.kingdom.game.events.Event;
import net.lugocorp.kingdom.game.model.Player;
import net.lugocorp.kingdom.ui.views.GameView;

/**
 * This class contains utility functions for writing new Item effects
 */
public class ItemLogic {

    /**
     * Item that can be consumed to increase the Player's gold
     */
    public static void valuable(Event event) {
        Events.ItemConsumedEvent e = (Events.ItemConsumedEvent) event;
        e.consumer.getLeader().ifPresent((Player p) -> {
            p.gold += e.item.gold;
        });
    }

    /**
     * Item that can be consumed to heal the consumer
     */
    public static void potion(Event event, int points) {
        Events.ItemConsumedEvent e = (Events.ItemConsumedEvent) event;
        e.consumer.combat.health.heal(points);
    }

    /**
     * Item that can be consumed to stave off hunger
     */
    public static void food(GameView view, Event event) {
        Events.ItemConsumedEvent e = (Events.ItemConsumedEvent) event;
        Events.CanEatEvent e1 = new Events.CanEatEvent(e.consumer, e.item);
        e.consumer.handleEvent(view, e1);
        if (e1.edible) {
            e.consumer.eat(view.game);
        } else {
            view.logger.log("Item is not edible for this unit");
            e.consumed = false;
        }
    }
}
