package net.lugocorp.kingdom.game.mechanics;
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
    public final Fates fates = new Fates();
    public final TurnStructure turns;

    public Mechanics(Game game) {
        this.turns = new TurnStructure(game);
    }

    /**
     * Initializes mechanics that require GameView to be initialized
     */
    public void init(Game game) {
        this.auction.init(game);
        this.fates.init(game);
        this.pools.init(game);
    }
}
