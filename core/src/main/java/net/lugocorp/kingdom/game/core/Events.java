package net.lugocorp.kingdom.game.core;
import net.lugocorp.kingdom.engine.render.Modellable;
import net.lugocorp.kingdom.game.combat.Damage;
import net.lugocorp.kingdom.game.events.Event;
import net.lugocorp.kingdom.game.model.Ability;
import net.lugocorp.kingdom.game.model.Artifact;
import net.lugocorp.kingdom.game.model.Building;
import net.lugocorp.kingdom.game.model.Fate;
import net.lugocorp.kingdom.game.model.Item;
import net.lugocorp.kingdom.game.model.Patron;
import net.lugocorp.kingdom.game.model.Tile;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.game.player.Player;

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
    public static class TakeDamageEvent<T> extends Event {
        public final Damage dmg;
        public final T target;

        public TakeDamageEvent(T target, Damage dmg) {
            super();
            this.target = target;
            this.dmg = dmg;
        }
    }

    /**
     * Triggered when some object attacks another
     */
    public static class AttackEvent<A, T> extends Event {
        public final Damage dmg;
        public final A attacker;
        public final T target;

        public AttackEvent(A attacker, T target, Damage dmg) {
            super();
            this.attacker = attacker;
            this.target = target;
            this.dmg = dmg;
        }
    }

    /**
     * Triggered when some object attacks another
     */
    public static class AttackedEvent<T, A> extends Event {
        public final Damage dmg;
        public final A attacker;
        public final T target;

        public AttackedEvent(T target, A attacker, Damage dmg) {
            super();
            this.attacker = attacker;
            this.target = target;
            this.dmg = dmg;
        }
    }

    /**
     * Calculates how far a Unit can attack
     */
    public static class UnitAttackRangeEvent extends Event {
        public final Unit unit;
        public int range = 1;

        public UnitAttackRangeEvent(Unit unit) {
            super();
            this.unit = unit;
        }
    }

    /**
     * Triggered when a Unit dies
     */
    public static class UnitDiedEvent extends Event {
        public final Unit unit;
        public final Unit killer;

        public UnitDiedEvent(Unit unit, Unit killer) {
            super();
            this.killer = killer;
            this.unit = unit;
        }
    }

    /**
     * Triggered when a Unit kills another Unit
     */
    public static class KilledUnitEvent extends Event {
        public final Unit unit;
        public final Unit victim;

        public KilledUnitEvent(Unit unit, Unit victim) {
            super();
            this.victim = victim;
            this.unit = unit;
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
     * Triggered when we need to check if a Unit can eat the given Item to stave off
     * hunger
     */
    public static class CanEatEvent extends Event {
        public final Unit unit;
        public final Item item;
        public boolean edible = false;

        public CanEatEvent(Unit unit, Item item) {
            super();
            this.unit = unit;
            this.item = item;
        }
    }

    /**
     * Triggered when a Unit generates favor for a Patron
     */
    public static class GenerateFavorEvent extends Event {
        public final Patron patron;
        public int favor;

        public GenerateFavorEvent(Patron patron, int favor) {
            super();
            this.patron = patron;
            this.favor = favor;
        }
    }
}
