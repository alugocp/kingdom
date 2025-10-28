package net.lugocorp.kingdom.game.mechanics;
import net.lugocorp.kingdom.game.Game;

/**
 * This class bundles game mechanics classes for easy access
 */
public class Mechanics {
    public static final int MENU_MARGIN = 150;
    public final ArtifactAuction auction = new ArtifactAuction();
    public final TurnStructure turns = new TurnStructure();
    public final Patronage patronage = new Patronage();
    public final GlyphPools pools = new GlyphPools();
    public final DayNight dayNight = new DayNight();
    public final NewUnit newUnits = new NewUnit();
    public final LootTable loot = new LootTable();
    public final Fates fates = new Fates();

    /**
     * Initializes mechanics that require GameView to be initialized
     */
    public void init(Game game) {
        this.auction.init(game);
        this.pools.init(game);
        this.loot.init(game);
    }
}
