package net.lugocorp.kingdom.builtin;
import net.lugocorp.kingdom.engine.render.Modellable;
import net.lugocorp.kingdom.game.combat.Damage;
import net.lugocorp.kingdom.game.events.Event;
import net.lugocorp.kingdom.game.glyph.Glyph;
import net.lugocorp.kingdom.game.layers.Entity;
import net.lugocorp.kingdom.game.model.Ability;
import net.lugocorp.kingdom.game.model.Artifact;
import net.lugocorp.kingdom.game.model.Building;
import net.lugocorp.kingdom.game.model.Fate;
import net.lugocorp.kingdom.game.model.Item;
import net.lugocorp.kingdom.game.model.Patron;
import net.lugocorp.kingdom.game.model.Tile;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.game.player.Player;
import net.lugocorp.kingdom.math.Point;
import java.util.List;
import java.util.Optional;

/**
 * Contains a collection of Events that are integral to the game's core
 * mechanics
 */
public class Events {

    /**
     * Template for the generator Events
     */
    private static abstract class GenerateBlobEventTemplate<T> extends Event {
        public final T blob;

        private GenerateBlobEventTemplate(T blob) {
            super(false, true);
            this.blob = blob;
        }
    }

    /**
     * Generator Event for new Tiles
     */
    public static class GenerateTileEvent extends GenerateBlobEventTemplate<Tile> {
        public GenerateTileEvent(Tile blob) {
            super(blob);
        }
    }

    /**
     * Generator Event for new Units
     */
    public static class GenerateUnitEvent extends GenerateBlobEventTemplate<Unit> {
        public GenerateUnitEvent(Unit blob) {
            super(blob);
        }
    }

    /**
     * Generator Event for new Buildings
     */
    public static class GenerateBuildingEvent extends GenerateBlobEventTemplate<Building> {
        public GenerateBuildingEvent(Building blob) {
            super(blob);
        }
    }

    /**
     * Generator Event for new Patrons
     */
    public static class GeneratePatronEvent extends GenerateBlobEventTemplate<Patron> {
        public GeneratePatronEvent(Patron blob) {
            super(blob);
        }
    }

    /**
     * Generator Event for new Items
     */
    public static class GenerateItemEvent extends GenerateBlobEventTemplate<Item> {
        public GenerateItemEvent(Item blob) {
            super(blob);
        }
    }

    /**
     * Generator Event for new Abilities
     */
    public static class GenerateAbilityEvent extends GenerateBlobEventTemplate<Ability> {
        public GenerateAbilityEvent(Ability blob) {
            super(blob);
        }
    }

    /**
     * Generator Event for new Artifacts
     */
    public static class GenerateArtifactEvent extends GenerateBlobEventTemplate<Artifact> {
        public GenerateArtifactEvent(Artifact blob) {
            super(blob);
        }
    }

    /**
     * Generator Event for new Fates
     */
    public static class GenerateFateEvent extends GenerateBlobEventTemplate<Fate> {
        public GenerateFateEvent(Fate blob) {
            super(blob);
        }
    }

    /**
     * Generic repeatable Event with customizable channel name
     */
    public static class RepeatedEvent extends Event {
        public int interval;
        public boolean repeat;

        public RepeatedEvent(String channel, int interval, boolean repeat) {
            super(channel, false, false);
            this.interval = interval;
            this.repeat = repeat;
        }

        public <E extends Event> RepeatedEvent(Class<E> channel, int interval, boolean repeat) {
            this(channel.getSimpleName(), interval, repeat);
        }
    }

    /**
     * Collects how far a Unit or Building can see
     */
    public static class GetVisionEvent extends Event {
        public final Player player;
        public boolean canSeeAtNight = false;
        public int radius = 1;

        public GetVisionEvent(Player player) {
            super();
            this.player = player;
        }

        /**
         * Accounts for low vision at night
         */
        public int cumulative(boolean isNight) {
            return Math.max(0, this.radius - (isNight && canSeeAtNight ? 0 : 1));
        }
    }

    /**
     * This event is fired whenever a Unit calculates whether it can move onto some
     * Tile
     */
    public static class CanUnitMoveEvent extends Event {
        public final Unit unit;
        public final Tile tile;
        public boolean canWalkOnTile;
        public boolean canWalkOnBuilding;

        public CanUnitMoveEvent(Unit unit, Tile tile) {
            super();
            this.unit = unit;
            this.tile = tile;
            this.canWalkOnTile = !tile.getObstacle();
            this.canWalkOnBuilding = tile.building
                    .map((Building b) -> !b.getObstacle() && (!b.isActive() || tile.leader.equals(unit.getLeader())))
                    .orElse(true);
        }

        /**
         * Returns true if the Unit can walk on this Tile
         */
        public boolean possible() {
            return this.canWalkOnTile && this.canWalkOnBuilding;
        }
    }

    /**
     * Calculates how far a Unit can move
     */
    public static class UnitMoveDistanceEvent extends Event {
        public final Unit unit;
        public int distance = 1;

        public UnitMoveDistanceEvent(Unit unit) {
            super();
            this.unit = unit;
        }
    }

    /**
     * Triggered whenever a Unit moves
     */
    public static class UnitMovedEvent extends Event {
        public final List<Point> previous;
        public final Point current;
        public final Unit unit;

        public UnitMovedEvent(Unit unit, Point current, List<Point> previous) {
            super();
            this.previous = previous;
            this.current = current;
            this.unit = unit;
        }
    }

    /**
     * Triggered when a Player claims an Artifact
     */
    public static class ArtifactClaimedEvent extends Event {
        public final Artifact artifact;
        public final Player player;

        public ArtifactClaimedEvent(Artifact artifact, Player player) {
            super();
            this.artifact = artifact;
            this.player = player;
        }
    }

    /**
     * Triggered when an Item is consumed
     */
    public static class ItemConsumedEvent extends Event {
        public final Item item;
        public final Unit consumer;
        public boolean consumed = true;

        public ItemConsumedEvent(Item item, Unit consumer) {
            super();
            this.consumer = consumer;
            this.item = item;
        }
    }

    /**
     * Triggered when an Ability is activated
     */
    public static class AbilityActivatedEvent extends Event {
        public final Ability ability;

        public AbilityActivatedEvent(Ability ability) {
            super();
            this.ability = ability;
        }
    }

    /**
     * Triggered when some target takes Damage
     */
    public static class TakeDamageEvent extends Event {
        public final Entity target;
        public final Damage dmg;

        public TakeDamageEvent(Entity target, Damage dmg) {
            super();
            this.target = target;
            this.dmg = dmg;
        }
    }

    /**
     * Triggered when some object attacks another
     */
    public static class AttackEvent extends Event {
        public final Entity attacker;
        public final Entity target;
        public final Damage dmg;

        public AttackEvent(Entity attacker, Entity target, Damage dmg) {
            super();
            this.attacker = attacker;
            this.target = target;
            this.dmg = dmg;
        }
    }

    /**
     * Triggered when some object attacks another
     */
    public static class AttackedEvent extends Event {
        public final Entity attacker;
        public final Entity target;
        public final Damage dmg;

        public AttackedEvent(Entity target, Entity attacker, Damage dmg) {
            super();
            this.attacker = attacker;
            this.target = target;
            this.dmg = dmg;
        }
    }

    /**
     * Triggered when we need to calculate a Unit's critical hit chance
     */
    public static class CheckCriticalHitEvent extends Event {
        public final Entity entity;
        public float multiplier = 1.25f;
        public int chance = 5;

        public CheckCriticalHitEvent(Entity entity) {
            super();
            this.entity = entity;
        }
    }

    /**
     * Triggered when an Entity dies
     */
    public static class EntityDiedEvent extends Event {
        public final Entity target;
        public final Entity killer;

        public EntityDiedEvent(Entity target, Entity killer) {
            super();
            this.killer = killer;
            this.target = target;
        }
    }

    /**
     * Triggered when an Entity kills another Entity
     */
    public static class KilledEntityEvent extends Event {
        public final Entity killer;
        public final Entity target;

        public KilledEntityEvent(Entity killer, Entity target) {
            super();
            this.target = target;
            this.killer = killer;
        }
    }

    /**
     * Triggered when an Entity heals another Enttiy
     */
    public static class HealEntityEvent extends Event {
        public final Entity healer;
        public final Entity target;
        public int amount;

        public HealEntityEvent(Entity healer, Entity target, int amount) {
            super();
            this.healer = healer;
            this.target = target;
            this.amount = amount;
        }
    }

    /**
     * Triggered whenever a Modellable is spawned into the World
     */
    public static class SpawnEvent<T extends Modellable> extends Event {
        public final T spawned;

        public SpawnEvent(T spawned) {
            super();
            this.spawned = spawned;
        }
    }

    /**
     * Triggered when we need to check if the Unit is stunned (they cannot act in
     * such a case)
     */
    public static class IsStunnedEvent extends Event {
        public final Unit unit;
        public boolean isStunned = false;

        public IsStunnedEvent(Unit unit) {
            super();
            this.unit = unit;
        }
    }

    /**
     * Triggered when a status effect (Ability) is added to a Unit
     */
    public static class StatusEffectAddedEvent extends Event {
        public final Ability status;
        public final Unit unit;

        public StatusEffectAddedEvent(Ability status, Unit unit) {
            super();
            this.status = status;
            this.unit = unit;
        }
    }

    /**
     * Triggered after a Patron calculates their favor for a given Player
     */
    public static class CalculateFavorEvent extends Event {
        public final Patron patron;
        public final Player player;
        public int favor;

        public CalculateFavorEvent(Patron patron, Player player, int favor) {
            super();
            this.patron = patron;
            this.player = player;
            this.favor = favor;
        }
    }

    /**
     * Triggered when a Unit generates favor for a Patron
     */
    public static class GenerateFavorEvent extends Event {
        public final Patron patron;
        public final Unit unit;
        public int favor;

        public GenerateFavorEvent(Patron patron, Unit unit, int favor) {
            super();
            this.patron = patron;
            this.favor = favor;
            this.unit = unit;
        }
    }

    /**
     * Triggered when a Unit generates auction points
     */
    public static class GenerateAuctionPointsEvent extends Event {
        public final Unit unit;
        public int points;

        public GenerateAuctionPointsEvent(Unit unit, int points) {
            super();
            this.points = points;
            this.unit = unit;
        }
    }

    /**
     * Triggers whenever a Unit harvests an Item
     */
    public static class HarvestEvent extends Event {
        public final Unit unit;
        public final Item item;

        public HarvestEvent(Unit unit, Item item) {
            super();
            this.unit = unit;
            this.item = item;
        }
    }

    /**
     * Triggers when a Player is spawning its initial Unit, so we can control which
     * Glyph it comes from
     */
    public static class GetInitialGlyphEvent extends Event {
        public Optional<Glyph> glyph = Optional.empty();
    }

    /**
     * Triggers when the Game starts
     */
    public static class GameStartEvent extends Event {
        public final Player player;

        public GameStartEvent(Player player) {
            this.player = player;
        }
    }

    /**
     * Triggers when the given Player wins an auction
     */
    public static class WonAuctionEvent extends Event {
        public final Player player;

        public WonAuctionEvent(Player player) {
            this.player = player;
        }
    }

    /**
     * Triggers when the given Player loses an auction
     */
    public static class LostAuctionEvent extends Event {
        public final Player player;

        public LostAuctionEvent(Player player) {
            this.player = player;
        }
    }

    /**
     * Triggers when a Player recruits a Unit using unit points
     */
    public static class RecruitNewUnitEvent extends Event {
        public final Unit unit;

        public RecruitNewUnitEvent(Unit unit) {
            this.unit = unit;
        }
    }

    /**
     * Triggers when a Player's turn ends
     */
    public static class EndOfTurnEvent extends Event {
    }
}
