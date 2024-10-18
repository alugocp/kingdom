package net.lugocorp.kingdom.game.model;
import net.lugocorp.kingdom.game.core.Events.AbilityActivatedEvent;
import net.lugocorp.kingdom.game.events.Event;
import net.lugocorp.kingdom.game.events.EventReceiver;
import net.lugocorp.kingdom.ui.menu.ButtonNode;
import net.lugocorp.kingdom.ui.menu.ListNode;
import net.lugocorp.kingdom.ui.menu.MenuNode;
import net.lugocorp.kingdom.ui.menu.MenuSubject;
import net.lugocorp.kingdom.ui.menu.TextNode;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.math.Point;
import java.util.Optional;

/**
 * A passive or active effect that Units, Buildings and Tiles can use
 */
public class Ability implements EventReceiver, MenuSubject {
    public final String name;
    public String desc = "";

    Ability(String name) {
        this.name = name;
    }

    /** {@inheritdoc} */
    @Override
    public void handleEventWithoutSignalBooster(GameView view, Event e) {
        view.game.events.ability.handle(view, this, e);
    }

    /** {@inheritdoc} */
    @Override
    public String getStratifier() {
        return this.name;
    }

    /** {@inheritdoc} */
    @Override
    public MenuNode getMenuContent(GameView view, Optional<Point> p) {
        ListNode node = new ListNode();
        Optional<Unit> wielder = p.flatMap((Point p1) -> view.game.world.getTile(p1.x, p1.y))
                .flatMap((Tile t) -> t.unit);
        if (wielder.isPresent() && wielder.get().leader.map((Player p1) -> p1.isHumanPlayer()).orElse(false)
                && view.game.events.ability.hasEventHandler(this.getStratifier(), "AbilityActivatedEvent")
                && !view.game.mechanics.turns.hasUnitActed(wielder.get())) {
            node.add(new ButtonNode(view.game.graphics, this.name, () -> {
                this.handleEvent(view, new AbilityActivatedEvent(this, wielder.get()));
                view.menu.refresh(true);
            }));
        } else {
            node.add(new TextNode(view.game.graphics, this.name));
        }
        node.add(new TextNode(view.game.graphics, this.desc));
        return node;
    }
}
