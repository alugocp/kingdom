package net.lugocorp.kingdom.game.pools;
import net.lugocorp.kingdom.game.Game;

/**
 * This object aggregates all our Pools
 */
public class Content {
    public final ArtifactPool artifacts = new ArtifactPool();

    /**
     * Runs all the other Pools' init() methods
     */
    public void init(Game g) {
        this.artifacts.init(g);
    }
}
