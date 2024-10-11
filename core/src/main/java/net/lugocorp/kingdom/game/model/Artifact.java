package net.lugocorp.kingdom.game.model;
import net.lugocorp.kingdom.game.events.Event;
import net.lugocorp.kingdom.game.events.EventTarget;
import net.lugocorp.kingdom.game.Game;
import net.lugocorp.kingdom.ui.menu.ArtifactNode;
import net.lugocorp.kingdom.ui.menu.MenuNode;
import net.lugocorp.kingdom.ui.menu.MenuSubject;
import net.lugocorp.kingdom.ui.views.GameView;
import java.util.Optional;

/**
 * Artifacts are global buffs that Players can bid on
 */
public class Artifact implements EventTarget, MenuSubject {
    private boolean unlocked = false;
    private boolean claimed = false;
    public final String name;
    public Optional<String> image = Optional.empty();
    public String desc = "";

    Artifact(String name) {
        this.name = name;
    }

    /**
     * Marks this Artifact as claimed by the given Player
     */
    public void claim(Player player) {
        player.artifacts.add(this);
        this.claimed = false;
    }

    /**
     * Returns true if this Artifact should be included in the Auction popup Menu
     */
    public boolean shouldDisplay() {
        return this.unlocked && !this.claimed;
    }

    /** {@inheritdoc} */
    @Override
    public void handleEvent(Game g, Event e) {
        g.events.artifact.handle(g, this.name, e);
    }

    /** {@inheritdoc} */
    @Override
    public MenuNode getMenuContent(GameView view, int x, int y) {
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
