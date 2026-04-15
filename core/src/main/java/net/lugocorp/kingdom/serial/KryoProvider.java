package net.lugocorp.kingdom.serial;
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
        kryo.setRegistrationRequired(false);

        // Register built-in Java classes
        kryo.register(java.time.OffsetTime.class);
        kryo.register(java.util.ArrayList.class);
        kryo.register(java.util.HashMap.class);
        kryo.register(java.util.HashSet.class);
        kryo.register(java.util.Optional.class);

        // Register LibGDX classes
        kryo.register(com.badlogic.gdx.graphics.Color.class);

        // Register Kingdom classes
        kryo.register(net.lugocorp.kingdom.ai.Actor.class);
        kryo.register(net.lugocorp.kingdom.color.ColorPool.class);
        kryo.register(net.lugocorp.kingdom.game.glyph.Glyph.class);
        kryo.register(net.lugocorp.kingdom.game.glyph.GlyphCategory.class);
        kryo.register(net.lugocorp.kingdom.game.glyph.UnitGlyphs.class);
        kryo.register(net.lugocorp.kingdom.game.model.Ability.class);
        kryo.register(net.lugocorp.kingdom.game.model.Building.class);
        kryo.register(net.lugocorp.kingdom.game.model.Artifact.class);
        kryo.register(net.lugocorp.kingdom.game.model.Fate.class);
        kryo.register(net.lugocorp.kingdom.game.model.Patron.class);
        kryo.register(net.lugocorp.kingdom.game.model.Tile.class);
        kryo.register(net.lugocorp.kingdom.game.model.Unit.class);
        kryo.register(net.lugocorp.kingdom.game.model.Item.class);
        kryo.register(net.lugocorp.kingdom.game.player.HumanPlayer.class);
        kryo.register(net.lugocorp.kingdom.game.player.CompPlayer.class);
        kryo.register(net.lugocorp.kingdom.game.properties.Inventory.class);
        kryo.register(net.lugocorp.kingdom.game.properties.Inventory.InventoryType.class);
        kryo.register(net.lugocorp.kingdom.game.world.World.class);
        kryo.register(net.lugocorp.kingdom.game.Game.class);
        kryo.register(net.lugocorp.kingdom.gameplay.actions.ActionManager.class);
        kryo.register(net.lugocorp.kingdom.gameplay.combat.HitPoints.class);
        kryo.register(net.lugocorp.kingdom.gameplay.events.AllEventHandlers.class);
        kryo.register(net.lugocorp.kingdom.gameplay.events.EventHandlerBundle.class);
        kryo.register(net.lugocorp.kingdom.gameplay.events.SignalBooster.class);
        kryo.register(net.lugocorp.kingdom.gameplay.mechanics.Mechanics.class);
        kryo.register(net.lugocorp.kingdom.gameplay.mechanics.ArtifactAuction.class);
        kryo.register(net.lugocorp.kingdom.gameplay.mechanics.Auction.class);
        kryo.register(net.lugocorp.kingdom.gameplay.mechanics.DayNight.class);
        kryo.register(net.lugocorp.kingdom.gameplay.mechanics.DayNightState.class);
        kryo.register(net.lugocorp.kingdom.gameplay.mechanics.Fates.class);
        kryo.register(net.lugocorp.kingdom.gameplay.mechanics.GlyphPools.class);
        kryo.register(net.lugocorp.kingdom.gameplay.mechanics.NewUnit.class);
        kryo.register(net.lugocorp.kingdom.gameplay.mechanics.TurnStructure.class);
        kryo.register(net.lugocorp.kingdom.math.Point.class);
        return kryo;
    }
}
