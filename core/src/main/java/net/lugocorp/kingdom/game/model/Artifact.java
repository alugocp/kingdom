package net.lugocorp.kingdom.game.model;
import net.lugocorp.kingdom.game.core.Events.ArtifactClaimedEvent;
import net.lugocorp.kingdom.game.events.Event;
import net.lugocorp.kingdom.game.events.EventReceiver;
import net.lugocorp.kingdom.ui.menu.ArtifactNode;
import net.lugocorp.kingdom.ui.menu.MenuNode;
import net.lugocorp.kingdom.ui.menu.MenuSubject;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.math.Point;
import java.util.Optional;

/**
 * Artifacts are global buffs that Players can bid on
 */
public class Artifact implements EventReceiver, MenuSubject {
    private Optional<Player> owner = Optional.empty();
    public final String name;
    public Optional<String> image = Optional.empty();
    public String desc = "";

    Artifact(String name) {
        this.name = name;
    }

    /**
     * Marks this Artifact as claimed by the given Player
     */
    public void claim(GameView view, Player player) {
        this.owner = Optional.of(player);
        player.artifacts.add(this);
        this.handleEvent(view, new ArtifactClaimedEvent(this, player));
    }

    /**
     * Returns the Player that has claimed this Artifact (if any)
     */
    public Optional<Player> getOwner() {
        return this.owner;
    }

    /**
     * Returns true if this Artifact should be included in the Auction popup Menu
     */
    public boolean shouldDisplay() {
        return !this.owner.isPresent();
    }

    /** {@inheritdoc} */
    @Override
    public void handleEventWithoutSignalBooster(GameView view, Event e) {
        view.game.events.artifact.handle(view, this, e);
    }

    /** {@inheritdoc} */
    @Override
    public String getStratifier() {
        return this.name;
    }

    /** {@inheritdoc} */
    @Override
    public MenuNode getMenuContent(GameView view, Optional<Point> p) {
        return new ArtifactNode(view.game.graphics, this);
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
