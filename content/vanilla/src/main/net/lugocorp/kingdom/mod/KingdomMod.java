package net.lugocorp.kingdom.mod;
import net.lugocorp.kingdom.game.combat.Damage;
import net.lugocorp.kingdom.game.combat.Damage.DamageType;
import net.lugocorp.kingdom.game.core.AbilityLogic;
import net.lugocorp.kingdom.game.core.Events;
import net.lugocorp.kingdom.game.events.AllEventHandlers;
import net.lugocorp.kingdom.game.events.Event;
import net.lugocorp.kingdom.game.model.Ability;
import net.lugocorp.kingdom.game.model.Artifact;
import net.lugocorp.kingdom.game.model.Building;
import net.lugocorp.kingdom.game.model.Inventory;
import net.lugocorp.kingdom.game.model.Inventory.InventoryType;
import net.lugocorp.kingdom.game.model.Item;
import net.lugocorp.kingdom.game.model.Tile;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.ui.views.GameView;
import java.util.Optional;

/**
 * This mod defines all the content for version 1.0 of the base game
 */
public class KingdomMod {

    /**
     * This function is called when the mod is successfully loaded
     */
    public void load(AllEventHandlers events) {

        /**
         * Tiles
         */
        events.tile.addEventHandler("Grassland", "GenerateTileEvent", (GameView view, Tile receiver, Event event) -> {
            Events.GenerateTileEvent e = (Events.GenerateTileEvent) event;
            e.blob.setModelInstance(view.game.graphics.loaders.assets, "tile");
        });

        /**
         * Buildings
         */
        events.building.addEventHandler("Mine", "GenerateBuildingEvent",
                (GameView view, Building receiver, Event event) -> {
                    Events.GenerateBuildingEvent e = (Events.GenerateBuildingEvent) event;
                    e.blob.setModelInstance(view.game.graphics.loaders.assets, "mine");
                });
        events.building.addEventHandler("Vault", "GenerateBuildingEvent",
                (GameView view, Building receiver, Event event) -> {
                    Events.GenerateBuildingEvent e = (Events.GenerateBuildingEvent) event;
                    e.blob.setModelInstance(view.game.graphics.loaders.assets, "vault");
                    e.blob.items = Optional.of(new Inventory(InventoryType.BUILDING, 24));
                });
        events.building.addEventHandler("Forest", "GenerateBuildingEvent",
                (GameView view, Building receiver, Event event) -> {
                    Events.GenerateBuildingEvent e = (Events.GenerateBuildingEvent) event;
                    e.blob.setModelInstance(view.game.graphics.loaders.assets, "forest");
                });

        /**
         * Units
         */
        events.unit.addEventHandler("Crystal", "GenerateUnitEvent", (GameView view, Unit receiver, Event event) -> {
            Events.GenerateUnitEvent e = (Events.GenerateUnitEvent) event;
            e.blob.setModelInstance(view.game.graphics.loaders.assets, "crystal");
            e.blob.active1 = Optional.of(view.game.generator.ability("Slap"));
        });
        events.unit.addEventHandler("Axolotl", "GenerateUnitEvent", (GameView view, Unit receiver, Event event) -> {
            Events.GenerateUnitEvent e = (Events.GenerateUnitEvent) event;
            e.blob.setModelInstance(view.game.graphics.loaders.assets, "axolotl");
            e.blob.active1 = Optional.of(view.game.generator.ability("Slap"));
        });
        events.unit.addEventHandler("Frog Gnome", "GenerateUnitEvent", (GameView view, Unit receiver, Event event) -> {
            Events.GenerateUnitEvent e = (Events.GenerateUnitEvent) event;
            e.blob.setModelInstance(view.game.graphics.loaders.assets, "frog-gnome");
        });

        /**
         * Abilities
         */
        events.ability.addEventHandler("Slap", "GenerateAbilityEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Events.GenerateAbilityEvent e = (Events.GenerateAbilityEvent) event;
                    Damage dmg = new Damage(DamageType.IMPACT, 1);
                    e.blob.desc = String.format("Deals %s", dmg);
                });
        events.ability.addEventHandler("Slap", "AbilityActivatedEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Events.AbilityActivatedEvent e = (Events.AbilityActivatedEvent) event;
                    AbilityLogic.attack(view, e.wielder, new Damage(DamageType.IMPACT, 1));
                });

        /**
         * Artifacts
         */
        events.artifact.addEventHandler("Golden Feather", "GenerateArtifactEvent",
                (GameView view, Artifact receiver, Event event) -> {
                    Events.GenerateArtifactEvent e = (Events.GenerateArtifactEvent) event;
                    e.blob.desc = "All your units have +1 movement";
                    e.blob.image = Optional.of("golden feather");
                });
        events.artifact.addEventHandler("Golden Feather", "UnitMoveDistanceEvent",
                (GameView view, Artifact receiver, Event event) -> {
                    Events.UnitMoveDistanceEvent e = (Events.UnitMoveDistanceEvent) event;
                    if (e.unit.leader.equals(receiver.getOwner())) {
                        e.distance++;
                    }
                });
        events.artifact.addEventHandler("Golden Feather", "ArtifactClaimedEvent",
                (GameView view, Artifact receiver, Event event) -> {
                    Events.ArtifactClaimedEvent e = (Events.ArtifactClaimedEvent) event;
                    view.game.events.signals.addListener("UnitMoveDistanceEvent", e.artifact);
                });

        /**
         * Items
         */
        events.item.addEventHandler("Potion", "GenerateItemEvent", (GameView view, Item receiver, Event event) -> {
            Events.GenerateItemEvent e = (Events.GenerateItemEvent) event;
            e.blob.desc = "Consume to restore a unit's health";
            e.blob.icon = Optional.of("potion");
        });
    }
}
