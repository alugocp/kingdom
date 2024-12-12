package net.lugocorp.kingdom.game.mechanics;
import net.lugocorp.kingdom.game.Game;
import net.lugocorp.kingdom.engine.GameGraphics;

/**
 * This class bundles game mechanics classes for easy access
 */
public class Mechanics {
    public final ArtifactAuction auction = new ArtifactAuction();
    public final GlyphPools pools = new GlyphPools();
    public final DayNight dayNight = new DayNight();
    public final NewUnit newUnits = new NewUnit();
    public final TurnStructure turns;

    public Mechanics(Game game, GameGraphics graphics) {
        this.turns = new TurnStructure(game, graphics);
    }
}
