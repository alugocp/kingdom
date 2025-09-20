package net.lugocorp.kingdom.game.model;
import net.lugocorp.kingdom.game.events.Event;
import net.lugocorp.kingdom.game.events.EventReceiver;
import net.lugocorp.kingdom.game.player.Player;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.code.SideEffect;
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
     * Reads the EventHandlers on an Artifact and returns a score based on how many
     * align with this Fate's playstyle
     */
    public int evaluateArtifact(Artifact a) {
        // TODO implement me, I'm just random right now
        return (int) Math.floor(Math.random() * 10.0);
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
