package net.lugocorp.kingdom.game.pools;
import net.lugocorp.kingdom.game.Game;
import net.lugocorp.kingdom.game.model.Artifact;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * This Pool contains all Artifacts available in the Game
 */
public class ArtifactPool implements Pool<Artifact> {
    private final Set<Artifact> artifacts = new HashSet<>();

    /** {@inheritdoc} */
    @Override
    public void init(Game g) {
        Set<String> stratifiers = g.events.artifact.getStratifiers();
        for (String name : stratifiers) {
            this.artifacts.add(g.generator.artifact(name));
        }
    }

    /** {@inheritdoc} */
    @Override
    public int size() {
        return this.artifacts.size();
    }

    /** {@inheritdoc} */
    @Override
    public Artifact retrieve() {
        Iterator<Artifact> it = this.artifacts.iterator();
        Artifact a = it.next();
        it.remove();
        return a;
    }

    /** {@inheritdoc} */
    @Override
    public void replace(Artifact e) {
        this.artifacts.add(e);
    }
}
