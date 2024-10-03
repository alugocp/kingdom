package net.lugocorp.kingdom.game;
import net.lugocorp.kingdom.engine.GameGraphics;
import net.lugocorp.kingdom.events.Event;
import net.lugocorp.kingdom.events.EventTarget;
import net.lugocorp.kingdom.menu.ListNode;
import net.lugocorp.kingdom.menu.MenuNode;
import net.lugocorp.kingdom.menu.TextNode;

/**
 * A passive or active effect that Units, Buildings and Tiles can use
 */
public class Ability implements EventTarget {
    public final String name;
    public String desc = "";

    Ability(String name) {
        this.name = name;
    }

    /** {@inheritdoc} */
    @Override
    public void handleEvent(Game g, Event e) {
        g.events.ability.handle(g, this.name, e);
    }

    /**
     * Returns some nodes for a Menu
     */
    public MenuNode getMenuContent(GameGraphics graphics) {
        ListNode node = new ListNode();
        // TODO return name as a ButtonNode if the ability has an activation event
        node.add(new TextNode(graphics, this.name));
        node.add(new TextNode(graphics, this.desc));
        return node;
    }
}
