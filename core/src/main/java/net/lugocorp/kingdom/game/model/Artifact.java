package net.lugocorp.kingdom.game.model;
import net.lugocorp.kingdom.builtin.Events;
import net.lugocorp.kingdom.gameplay.events.Event;
import net.lugocorp.kingdom.gameplay.events.EventReceiver;
import net.lugocorp.kingdom.game.layers.Entity;
import net.lugocorp.kingdom.game.player.Player;
import net.lugocorp.kingdom.math.Point;
import net.lugocorp.kingdom.menu.MenuNode;
import net.lugocorp.kingdom.menu.MenuSubject;
import net.lugocorp.kingdom.menu.game.ArtifactNode;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.SideEffect;
import java.util.Optional;

/**
 * Artifacts are global buffs that Players can bid on
 */
public class Artifact implements EventReceiver, MenuSubject {
    private Optional<Player> owner = Optional.empty();
    public final String name;
    public Optional<String> image = Optional.empty();
    public String desc = "";
    public int chips = 1;

    Artifact(String name) {
        this.name = name;
    }

    /**
     * This should only be used in conjunction with Kryo rehydration
     */
    public Artifact() {
        this.name = null;
    }

    /**
     * Marks this Artifact as claimed by the given Player
     */
    public void claim(GameView view, Player player) {
        this.owner = Optional.of(player);
        player.artifacts.add(this);
        this.handleEvent(view, new Events.ArtifactClaimedEvent(this, player)).execute();
    }

    /**
     * Returns the Player that has claimed this Artifact (if any)
     */
    public Optional<Player> getOwner() {
        return this.owner;
    }

    /**
     * Returns true if this Artifact has been claimed by anyone
     */
    public boolean isClaimed() {
        return this.owner.isPresent();
    }

    /**
     * Returns true if this Artifact has been claimed by the given Entity's leader
     */
    public boolean isClaimedByLeader(Entity e) {
        return this.owner.equals(e.getLeader());
    }

    /**
     * Returns true if this Artifact has been claimed by the given Player
     */
    public boolean isClaimedByPlayer(Player p) {
        return this.owner.map((Player p1) -> p.equals(p1)).orElse(false);
    }

    /** {@inheritdoc} */
    @Override
    public SideEffect handleEventWithoutSignalBooster(GameView view, Event e) {
        return view.game.events.artifact.handle(view, this, e);
    }

    /** {@inheritdoc} */
    @Override
    public String getStratifier() {
        return this.name;
    }

    /** {@inheritdoc} */
    @Override
    public MenuNode getMenuContent(GameView view, Optional<Point> p) {
        return new ArtifactNode(view.av, this, Optional.empty());
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
