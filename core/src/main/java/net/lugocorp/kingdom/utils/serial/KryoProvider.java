package net.lugocorp.kingdom.utils.serial;
import com.esotericsoftware.kryo.Kryo;

/**
 * This class returns a Kryo instance
 */
class KryoProvider {

    /**
     * Returns a new instance of Kryo
     */
    static Kryo getKryo() {
        Kryo kryo = new Kryo();
        kryo.setReferences(true);

        // Register built-in Java classes
        kryo.register(java.time.OffsetTime.class);
        kryo.register(java.util.ArrayList.class);
        kryo.register(java.util.HashMap.class);
        kryo.register(java.util.HashSet.class);
        kryo.register(java.util.Optional.class);

        // Register LibGDX classes
        kryo.register(com.badlogic.gdx.graphics.Color.class);

        // Register Kingdom classes
        kryo.register(net.lugocorp.kingdom.game.events.AllEventHandlers.class);
        kryo.register(net.lugocorp.kingdom.game.events.EventHandlerBundle.class);
        kryo.register(net.lugocorp.kingdom.game.events.SignalBooster.class);
        kryo.register(net.lugocorp.kingdom.game.mechanics.Mechanics.class);
        kryo.register(net.lugocorp.kingdom.game.mechanics.ArtifactAuction.class);
        kryo.register(net.lugocorp.kingdom.game.mechanics.ArtifactAuction.Auction.class);
        kryo.register(net.lugocorp.kingdom.game.mechanics.DayNight.class);
        kryo.register(net.lugocorp.kingdom.game.mechanics.DayNight.State.class);
        kryo.register(net.lugocorp.kingdom.game.mechanics.Fates.class);
        kryo.register(net.lugocorp.kingdom.game.mechanics.GlyphPools.class);
        kryo.register(net.lugocorp.kingdom.game.mechanics.NewUnit.class);
        kryo.register(net.lugocorp.kingdom.game.mechanics.TurnStructure.class);
        kryo.register(net.lugocorp.kingdom.game.mechanics.TurnStructure.FutureTick.class);
        kryo.register(net.lugocorp.kingdom.game.model.Ability.class);
        kryo.register(net.lugocorp.kingdom.game.model.Building.class);
        kryo.register(net.lugocorp.kingdom.game.model.Artifact.class);
        kryo.register(net.lugocorp.kingdom.game.model.Inventory.class);
        kryo.register(net.lugocorp.kingdom.game.model.Inventory.InventoryType.class);
        kryo.register(net.lugocorp.kingdom.game.model.Fate.class);
        kryo.register(net.lugocorp.kingdom.game.model.Glyph.class);
        kryo.register(net.lugocorp.kingdom.game.model.Patron.class);
        kryo.register(net.lugocorp.kingdom.game.model.Player.class);
        kryo.register(net.lugocorp.kingdom.game.model.Tile.class);
        kryo.register(net.lugocorp.kingdom.game.model.Unit.class);
        kryo.register(net.lugocorp.kingdom.game.model.Unit.UnitGlyphs.class);
        kryo.register(net.lugocorp.kingdom.game.model.Unit.SleepState.class);
        kryo.register(net.lugocorp.kingdom.game.model.Item.class);
        kryo.register(net.lugocorp.kingdom.game.model.Tags.class);
        kryo.register(net.lugocorp.kingdom.game.model.GlyphCategory.class);
        kryo.register(net.lugocorp.kingdom.game.combat.HitPoints.class);
        kryo.register(net.lugocorp.kingdom.game.world.World.class);
        kryo.register(net.lugocorp.kingdom.game.Game.class);
        kryo.register(net.lugocorp.kingdom.utils.math.Point.class);
        return kryo;
    }
}
