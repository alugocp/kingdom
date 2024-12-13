package net.lugocorp.kingdom.game.mechanics;
import net.lugocorp.kingdom.engine.Graphics;
import net.lugocorp.kingdom.game.Game;

/**
 * This class bundles game mechanics classes for easy access
 */
public class Mechanics {
    public static final int MENU_MARGIN = 150;
    public final ArtifactAuction auction = new ArtifactAuction();
    public final GlyphPools pools = new GlyphPools();
    public final DayNight dayNight = new DayNight();
    public final NewUnit newUnits = new NewUnit();
    public final TurnStructure turns;

    public Mechanics(Game game, Graphics graphics) {
        this.turns = new TurnStructure(game, graphics);
    }
}
