package net.lugocorp.kingdom.game.model;
import net.lugocorp.kingdom.engine.AudioVideo;
import net.lugocorp.kingdom.game.player.Player;
import net.lugocorp.kingdom.gameplay.events.Event;
import net.lugocorp.kingdom.gameplay.events.EventReceiver;
import net.lugocorp.kingdom.menu.structure.ListNode;
import net.lugocorp.kingdom.menu.text.SubheaderNode;
import net.lugocorp.kingdom.menu.text.TextNode;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.SideEffect;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A Fate is a general path the players can choose at the start of the game.
 * They provide bonuses that reward certain playstyles.
 */
public class Fate implements EventReceiver {
    public final String name;
    public final List<String> desc = new ArrayList<>();
    public Optional<String> image = Optional.empty();
    private Player player = null;

    Fate(String name) {
        this.name = name;
    }

    /**
     * This should only be used in conjunction with Kryo rehydration
     */
    public Fate() {
        this.name = null;
    }

    /**
     * Returns the Player that this Fate is associated with
     */
    public Player getPlayer() {
        return this.player;
    }

    /**
     * Associated this Fate with a Player
     */
    public void setPlayer(Player player) {
        this.player = player;
    }

    /**
     * Adds this Fate's description to the given ListNode
     */
    public ListNode addToListNode(AudioVideo av, ListNode node) {
        boolean first = true;
        for (String s : this.desc) {
            node.add(first ? new SubheaderNode(av, s) : new TextNode(av, s));
            first = false;
        }
        return node;
    }

    /** {@inheritdoc} */
    @Override
    public SideEffect handleEventWithoutSignalBooster(GameView view, Event e) {
        return view.game.events.fate.handle(view, this, e);
    }

    /** {@inheritdoc} */
    @Override
    public String getStratifier() {
        return this.name;
    }

    /** {@inheritdoc} */
    @Override
    public String toString() {
        return this.name;
    }

    /** {@inheritdoc} */
    @Override
    public int hashCode() {
        return this.name.hashCode();
    }
}
