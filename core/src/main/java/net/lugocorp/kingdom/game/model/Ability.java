package net.lugocorp.kingdom.game.model;
import net.lugocorp.kingdom.game.events.Event;
import net.lugocorp.kingdom.game.events.EventReceiver;
import net.lugocorp.kingdom.ui.menu.ListNode;
import net.lugocorp.kingdom.ui.menu.MenuNode;
import net.lugocorp.kingdom.ui.menu.MenuSubject;
import net.lugocorp.kingdom.ui.menu.TextNode;
import net.lugocorp.kingdom.ui.views.GameView;

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
    public void handleEvent(GameView view, Event e) {
        view.game.events.ability.handle(view, this, e);
    }

    /** {@inheritdoc} */
    @Override
    public String getStratifier() {
        return this.name;
    }

    /** {@inheritdoc} */
    @Override
    public MenuNode getMenuContent(GameView view, int x, int y) {
        ListNode node = new ListNode();
        // TODO return name as a ButtonNode if the ability has an activation event
        node.add(new TextNode(view.game.graphics, this.name));
        node.add(new TextNode(view.game.graphics, this.desc));
        return node;
    }
}
