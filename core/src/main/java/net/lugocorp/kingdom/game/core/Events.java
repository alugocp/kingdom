package net.lugocorp.kingdom.game.core;
import net.lugocorp.kingdom.game.events.Event;
import net.lugocorp.kingdom.game.model.Ability;
import net.lugocorp.kingdom.game.model.Artifact;
import net.lugocorp.kingdom.game.model.Building;
import net.lugocorp.kingdom.game.model.Item;
import net.lugocorp.kingdom.game.model.Player;
import net.lugocorp.kingdom.game.model.Tile;
import net.lugocorp.kingdom.game.model.Unit;

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

        private GenerateBlobEventTemplate(String channel, T blob) {
            super(channel);
            this.blob = blob;
        }
    }

    /**
     * Generator Event for new Tiles
     */
    public static class GenerateTileEvent extends GenerateBlobEventTemplate<Tile> {
        public GenerateTileEvent(Tile blob) {
            super("GenerateTileEvent", blob);
        }
    }

    /**
     * Generator Event for new Units
     */
    public static class GenerateUnitEvent extends GenerateBlobEventTemplate<Unit> {
        public GenerateUnitEvent(Unit blob) {
            super("GenerateUnitEvent", blob);
        }
    }

    /**
     * Generator Event for new Buildings
     */
    public static class GenerateBuildingEvent extends GenerateBlobEventTemplate<Building> {
        public GenerateBuildingEvent(Building blob) {
            super("GenerateBuildingEvent", blob);
        }
    }

    /**
     * Generator Event for new Items
     */
    public static class GenerateItemEvent extends GenerateBlobEventTemplate<Item> {
        public GenerateItemEvent(Item blob) {
            super("GenerateItemEvent", blob);
        }
    }

    /**
     * Generator Event for new Abilities
     */
    public static class GenerateAbilityEvent extends GenerateBlobEventTemplate<Ability> {
        public GenerateAbilityEvent(Ability blob) {
            super("GenerateAbilityEvent", blob);
        }
    }

    /**
     * Generator Event for new Artifacts
     */
    public static class GenerateArtifactEvent extends GenerateBlobEventTemplate<Artifact> {
        public GenerateArtifactEvent(Artifact blob) {
            super("GenerateArtifactEvent", blob);
        }
    }

    /**
     * This event is fired whenever a Unit calculates whether it can move onto some
     * Tile
     */
    public static class CanUnitMoveEvent extends Event {
        public final Unit unit;
        public final Tile tile;
        public boolean possible = true;

        public CanUnitMoveEvent(Unit unit, Tile tile) {
            super("CanUnitMoveEvent");
            this.unit = unit;
            this.tile = tile;
        }
    }

    /**
     * Calculates how far a Unit can move
     */
    public static class UnitMoveDistanceEvent extends Event {
        public final Unit unit;
        public int distance = 1;

        public UnitMoveDistanceEvent(Unit unit) {
            super("UnitMoveDistanceEvent");
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
            super("ArtifactClaimedEvent");
            this.artifact = artifact;
            this.player = player;
        }
    }
}
