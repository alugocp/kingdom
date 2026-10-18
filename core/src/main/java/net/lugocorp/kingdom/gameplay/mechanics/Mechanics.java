package net.lugocorp.kingdom.gameplay.mechanics;

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
    public final NewUnit recruitUnits = new NewUnit();
    public final LootTable loot = new LootTable();
    public final Fates fates = new Fates();

    public Mechanics() {
        this.auction.init();
        this.pools.init();
        this.loot.init();
    }
}
