package net.lugocorp.kingdom.builtin.logic;
import net.lugocorp.kingdom.builtin.Events;
import net.lugocorp.kingdom.game.model.Building;
import net.lugocorp.kingdom.game.model.Item;
import net.lugocorp.kingdom.game.model.Tile;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.gameplay.events.Event;
import net.lugocorp.kingdom.gameplay.events.StratifiedPayload;
import net.lugocorp.kingdom.math.Point;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.SideEffect;
import java.util.function.Function;

/**
 * This class contains utility functions for writing new Item effects
 */
public class ItemLogic {

    /**
     * Item that can be consumed to increase the Player's gold
     */
    public static StratifiedPayload<Item, Events.ItemConsumedEvent> valuable() {
        return new StratifiedPayload<>(Events.ItemConsumedEvent.class,
                (GameView view, Item receiver, Events.ItemConsumedEvent e) -> new SideEffect().add(() -> {
                    e.consumer.getLeader().get().gold += e.item.gold;
                    view.hud.top.update(view.game);
                }).add(e.consumer.handleEvent(view, new Events.YieldGoldEvent(e.consumer, e.item.gold))));
    }

    /**
     * Item that can be consumed to heal the consumer
     */
    public static SideEffect potion(GameView view, Event event, int points) {
        Events.ItemConsumedEvent e = (Events.ItemConsumedEvent) event;
        return e.consumer.combat.heal(view, points);
    }

    /**
     * Item that can be consumed to stave off hunger
     */
    public static SideEffect food(GameView view, Event event) {
        Events.ItemConsumedEvent e = (Events.ItemConsumedEvent) event;
        if (e.consumer.hunger.canEat(view, e.item)) {
            return new SideEffect().add(() -> e.consumer.hunger.eat(view, true));
        }
        e.consumed = false;
        return new SideEffect().add(() -> view.hud.logger.error("Item is not edible for this unit"));
    }

    /**
     * Item boosts damage given the following criteria
     */
    public static void boostDamage(Events.AttackEvent e, int boost, boolean criteria) {
        e.dmg.base += criteria ? boost : 0;
    }

    /**
     * Item boosts armor given the following criteria
     */
    public static void boostArmor(Events.TakeDamageEvent e, int boost, boolean criteria) {
        e.dmg.base -= criteria ? boost : 0;
    }

    /**
     * Item boosts healing given the following criteria
     */
    public static void boostHealing(Events.HealEntityEvent e, int boost, boolean criteria) {
        e.amount += criteria ? boost : 0;
    }

    /**
     * Item that spawns a building at the user's location
     */
    public static SideEffect build(GameView view, Unit caster, String building, Function<Tile, Boolean> criteria) {
        final Point p = caster.getPoint();
        if (view.game.world.getTile(p).isPresent()) {
            final Tile t = view.game.world.getTile(p).get();

            if (t.building.isPresent()) {
                return new SideEffect().add(() -> view.hud.logger.error("Cannot place another building here"));
            }
            if (criteria.apply(t)) {
                final Building b = view.game.generator.building(building, p.x, p.y);
                return new SideEffect().add(() -> b.spawn(view));
            }
            return new SideEffect().add(() -> view.hud.logger.error("Invalid tile for this item"));
        }
        return new SideEffect();
    }
}
