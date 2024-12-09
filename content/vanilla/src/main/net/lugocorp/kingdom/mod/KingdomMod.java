package net.lugocorp.kingdom.mod;
import net.lugocorp.kingdom.game.combat.Damage;
import net.lugocorp.kingdom.game.combat.Damage.DamageType;
import net.lugocorp.kingdom.game.core.AbilityLogic;
import net.lugocorp.kingdom.game.core.Events;
import net.lugocorp.kingdom.game.core.ItemLogic;
import net.lugocorp.kingdom.game.events.AllEventHandlers;
import net.lugocorp.kingdom.game.events.Event;
import net.lugocorp.kingdom.game.model.Ability;
import net.lugocorp.kingdom.game.model.Artifact;
import net.lugocorp.kingdom.game.model.Building;
import net.lugocorp.kingdom.game.model.Glyph;
import net.lugocorp.kingdom.game.model.Inventory;
import net.lugocorp.kingdom.game.model.Inventory.InventoryType;
import net.lugocorp.kingdom.game.model.Item;
import net.lugocorp.kingdom.game.model.Patron;
import net.lugocorp.kingdom.game.model.Player;
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
            e.blob.setModelInstance(view.game.graphics.loaders.assets, "grass");
        });
        events.tile.addEventHandler("Rock", "GenerateTileEvent", (GameView view, Tile receiver, Event event) -> {
            Events.GenerateTileEvent e = (Events.GenerateTileEvent) event;
            e.blob.setModelInstance(view.game.graphics.loaders.assets, "rock");
        });
        events.tile.addEventHandler("Water", "GenerateTileEvent", (GameView view, Tile receiver, Event event) -> {
            Events.GenerateTileEvent e = (Events.GenerateTileEvent) event;
            e.blob.setModelInstance(view.game.graphics.loaders.assets, "water");
            e.blob.setObstacle(true);
        });

        /**
         * Buildings
         */
        events.building.addEventHandler("Mine", "GenerateBuildingEvent",
                (GameView view, Building receiver, Event event) -> {
                    Events.GenerateBuildingEvent e = (Events.GenerateBuildingEvent) event;
                    e.blob.setModelInstance(view.game.graphics.loaders.assets, "mine");
                    e.blob.desc = "Mines provide valuables like gold coins";
                });
        events.building.addEventHandler("Vault", "GenerateBuildingEvent",
                (GameView view, Building receiver, Event event) -> {
                    Events.GenerateBuildingEvent e = (Events.GenerateBuildingEvent) event;
                    e.blob.setModelInstance(view.game.graphics.loaders.assets, "vault");
                    e.blob.desc = "Vaults can store excess items and be used in auctions";
                    e.blob.items = Optional.of(new Inventory(InventoryType.BUILDING, 24));
                });
        events.building.addEventHandler("Forest", "GenerateBuildingEvent",
                (GameView view, Building receiver, Event event) -> {
                    Events.GenerateBuildingEvent e = (Events.GenerateBuildingEvent) event;
                    e.blob.setModelInstance(view.game.graphics.loaders.assets, "forest");
                    e.blob.desc = "Don't miss the forest for the trees";
                });
        events.building.addEventHandler("Mountain", "GenerateBuildingEvent",
                (GameView view, Building receiver, Event event) -> {
                    Events.GenerateBuildingEvent e = (Events.GenerateBuildingEvent) event;
                    e.blob.setModelInstance(view.game.graphics.loaders.assets, "placeholder2");
                    e.blob.desc = "An immovable object";
                    e.blob.setObstacle(true);
                });

        /**
         * Patrons
         */
        events.patron.addEventHandler("Test Patron", "GeneratePatronEvent",
                (GameView view, Patron receiver, Event event) -> {
                    Events.GeneratePatronEvent e = (Events.GeneratePatronEvent) event;
                    e.blob.setModelInstance(view.game.graphics.loaders.assets, "placeholder1");
                    e.blob.desc = "Just a patron for testing";
                });

        /**
         * Playable units
         */
        events.unit.addEventHandler("Tlatec", "GenerateUnitEvent", (GameView view, Unit receiver, Event event) -> {
            Events.GenerateUnitEvent e = (Events.GenerateUnitEvent) event;
            e.blob.setModelInstance(view.game.graphics.loaders.assets, "axolotl");
            e.blob.desc = "Tlatec the Axolotl-man has travelled far from his home in search of worthy opponents";
            e.blob.setActiveAbilities(view.game.generator, Optional.of("Slap"), Optional.empty());
            e.blob.setPassiveAbilities(view.game.generator, "Swim");
            e.blob.glyphs.set(Glyph.BATTLE);
        });
        events.unit.addEventHandler("Gloop the Adventurer", "GenerateUnitEvent",
                (GameView view, Unit receiver, Event event) -> {
                    Events.GenerateUnitEvent e = (Events.GenerateUnitEvent) event;
                    e.blob.setModelInstance(view.game.graphics.loaders.assets, "placeholder1");
                    e.blob.desc = "This Plasmoid adventurer is eager to prove himself in the dungeons";
                    e.blob.setActiveAbilities(view.game.generator, Optional.of("Slap"), Optional.empty());
                    e.blob.glyphs.set(Glyph.BATTLE, Glyph.TRAVEL);
                });
        events.unit.addEventHandler("Geomancer", "GenerateUnitEvent", (GameView view, Unit receiver, Event event) -> {
            Events.GenerateUnitEvent e = (Events.GenerateUnitEvent) event;
            e.blob.desc = "This Raksha speaks to the stones";
            e.blob.setModelInstance(view.game.graphics.loaders.assets, "placeholder2");
            e.blob.setActiveAbilities(view.game.generator, Optional.of("Slap"), Optional.empty());
            e.blob.setPassiveAbilities(view.game.generator, "Mine Coins");
            e.blob.glyphs.set(Glyph.MINING);
        });
        events.unit.addEventHandler("The Druid", "GenerateUnitEvent", (GameView view, Unit receiver, Event event) -> {
            Events.GenerateUnitEvent e = (Events.GenerateUnitEvent) event;
            e.blob.desc = "A mysterious druid who rarely speaks";
            e.blob.setModelInstance(view.game.graphics.loaders.assets, "druid");
            e.blob.setActiveAbilities(view.game.generator, Optional.of("Plant Forest"), Optional.of("Slap"));
            e.blob.setPassiveAbilities(view.game.generator, "Pick Apples");
            e.blob.glyphs.set(Glyph.NATURE);
        });
        events.unit.addEventHandler("Frog Gnome", "GenerateUnitEvent", (GameView view, Unit receiver, Event event) -> {
            Events.GenerateUnitEvent e = (Events.GenerateUnitEvent) event;
            e.blob.desc = "Just a little gnome and his frog";
            e.blob.setModelInstance(view.game.graphics.loaders.assets, "frog-gnome");
            e.blob.setActiveAbilities(view.game.generator, Optional.of("Heal"), Optional.empty());
            e.blob.setPassiveAbilities(view.game.generator, "Shrewd");
            e.blob.glyphs.set(Glyph.HEALING, Glyph.TRAVEL);
        });
        events.unit.addEventHandler("Pickaxe Goblin", "GenerateUnitEvent",
                (GameView view, Unit receiver, Event event) -> {
                    Events.GenerateUnitEvent e = (Events.GenerateUnitEvent) event;
                    e.blob.desc = "This Goblin loves his pickaxe";
                    e.blob.setModelInstance(view.game.graphics.loaders.assets, "placeholder1");
                    e.blob.setActiveAbilities(view.game.generator, Optional.of("Dig Mine"), Optional.of("Repair Mine"));
                    e.blob.setPassiveAbilities(view.game.generator, "Make Money", "Mine Coins");
                    e.blob.glyphs.set(Glyph.MINING);
                });
        events.unit.addEventHandler("Stalagmite Golem", "GenerateUnitEvent",
                (GameView view, Unit receiver, Event event) -> {
                    Events.GenerateUnitEvent e = (Events.GenerateUnitEvent) event;
                    e.blob.desc = "A very pointy golem";
                    e.blob.setModelInstance(view.game.graphics.loaders.assets, "placeholder1");
                    e.blob.setActiveAbilities(view.game.generator, Optional.of("Dig Mine"), Optional.of("Slap"));
                    e.blob.setPassiveAbilities(view.game.generator, "Make Money", "Mine Coins");
                    e.blob.glyphs.set(Glyph.MINING);
                });
        events.unit.addEventHandler("Satyr", "GenerateUnitEvent", (GameView view, Unit receiver, Event event) -> {
            Events.GenerateUnitEvent e = (Events.GenerateUnitEvent) event;
            e.blob.desc = "He makes terrariums :)";
            e.blob.setModelInstance(view.game.graphics.loaders.assets, "placeholder1");
            e.blob.setActiveAbilities(view.game.generator, Optional.of("Build Vault"), Optional.empty());
            e.blob.setPassiveAbilities(view.game.generator, "Pick Apples");
            e.blob.glyphs.set(Glyph.NATURE, Glyph.TRADE);
        });
        events.unit.addEventHandler("Frog Druid", "GenerateUnitEvent", (GameView view, Unit receiver, Event event) -> {
            Events.GenerateUnitEvent e = (Events.GenerateUnitEvent) event;
            e.blob.desc = "A Frog-man that plants trees!";
            e.blob.setModelInstance(view.game.graphics.loaders.assets, "placeholder2");
            e.blob.setActiveAbilities(view.game.generator, Optional.of("Plant Forest"), Optional.empty());
            e.blob.setPassiveAbilities(view.game.generator, "Pick Apples");
            e.blob.glyphs.set(Glyph.NATURE);
        });
        events.unit.addEventHandler("Crocodile Man", "GenerateUnitEvent",
                (GameView view, Unit receiver, Event event) -> {
                    Events.GenerateUnitEvent e = (Events.GenerateUnitEvent) event;
                    e.blob.desc = "He has a green thumb";
                    e.blob.setModelInstance(view.game.graphics.loaders.assets, "placeholder2");
                    e.blob.setActiveAbilities(view.game.generator, Optional.of("Plant Forest"), Optional.empty());
                    e.blob.setPassiveAbilities(view.game.generator, "Pick Apples");
                    e.blob.glyphs.set(Glyph.NATURE);
                });
        events.unit.addEventHandler("Crocodile Girl", "GenerateUnitEvent",
                (GameView view, Unit receiver, Event event) -> {
                    Events.GenerateUnitEvent e = (Events.GenerateUnitEvent) event;
                    e.blob.desc = "A younger Crocodile-person";
                    e.blob.setModelInstance(view.game.graphics.loaders.assets, "placeholder1");
                    e.blob.setActiveAbilities(view.game.generator, Optional.of("Plant Forest"), Optional.of("Slap"));
                    e.blob.setPassiveAbilities(view.game.generator, "Pick Apples");
                    e.blob.glyphs.set(Glyph.NATURE);
                });
        events.unit.addEventHandler("Pottery Ogre", "GenerateUnitEvent",
                (GameView view, Unit receiver, Event event) -> {
                    Events.GenerateUnitEvent e = (Events.GenerateUnitEvent) event;
                    e.blob.desc = "He likes working with clay";
                    e.blob.setModelInstance(view.game.graphics.loaders.assets, "placeholder2");
                    e.blob.setActiveAbilities(view.game.generator, Optional.empty(), Optional.empty());
                    e.blob.setPassiveAbilities(view.game.generator, "Make Money");
                    e.blob.glyphs.set(Glyph.TRADE);
                });

        /**
         * Non-playable units
         */
        events.unit.addEventHandler("Crystal", "GenerateUnitEvent", (GameView view, Unit receiver, Event event) -> {
            Events.GenerateUnitEvent e = (Events.GenerateUnitEvent) event;
            e.blob.setModelInstance(view.game.graphics.loaders.assets, "crystal");
            e.blob.desc = "Mysterious floating sentient crystal being";
            e.blob.setActiveAbilities(view.game.generator, Optional.of("Slap"), Optional.empty());
            e.blob.setPassiveAbilities(view.game.generator, "Mine Coins");
            e.blob.playable = false;
        });
        events.unit.addEventHandler("Blob", "GenerateUnitEvent", (GameView view, Unit receiver, Event event) -> {
            Events.GenerateUnitEvent e = (Events.GenerateUnitEvent) event;
            e.blob.desc = "A classic slime enemy";
            e.blob.setModelInstance(view.game.graphics.loaders.assets, "blob");
            e.blob.playable = false;
        });

        /**
         * Abilities
         */
        // Slap
        final String ability_slap = "Slap";
        events.ability.addEventHandler(ability_slap, "GenerateAbilityEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Events.GenerateAbilityEvent e = (Events.GenerateAbilityEvent) event;
                    Damage dmg = new Damage(DamageType.IMPACT, 1);
                    e.blob.desc = String.format("Deals %s", dmg);
                });
        events.ability.addEventHandler(ability_slap, "AbilityActivatedEvent", (GameView view, Ability receiver,
                Event event) -> AbilityLogic.attack(view, receiver.wielder, new Damage(DamageType.IMPACT, 1)));

        // Heal
        final String ability_heal = "Heal";
        events.ability.addEventHandler(ability_heal, "GenerateAbilityEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Events.GenerateAbilityEvent e = (Events.GenerateAbilityEvent) event;
                    e.blob.desc = "Heals 5 damage";
                });
        events.ability.addEventHandler(ability_heal, "AbilityActivatedEvent",
                (GameView view, Ability receiver, Event event) -> AbilityLogic.healUnit(view, receiver.wielder, 5));

        // Repair Mine
        final String ability_repair_mine = "Repair Mine";
        events.ability.addEventHandler(ability_repair_mine, "GenerateAbilityEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Events.GenerateAbilityEvent e = (Events.GenerateAbilityEvent) event;
                    e.blob.desc = "Heals a mine for 5 damage";
                });
        events.ability.addEventHandler(ability_repair_mine, "AbilityActivatedEvent",
                (GameView view, Ability receiver, Event event) -> AbilityLogic.healBuilding(view, receiver.wielder, 5,
                        (Building b) -> b.name.equals("Mine")));

        // Plant Forest
        final String ability_plant_forest = "Plant Forest";
        events.ability.addEventHandler(ability_plant_forest, "GenerateAbilityEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Events.GenerateAbilityEvent e = (Events.GenerateAbilityEvent) event;
                    e.blob.desc = "Plants a forest";
                });
        events.ability.addEventHandler(ability_plant_forest, "AbilityActivatedEvent",
                (GameView view, Ability receiver, Event event) -> AbilityLogic.build(view, receiver.wielder, "Forest",
                        (Tile t) -> t.name.equals("Grassland")));

        // Dig Mine
        final String ability_dig_mine = "Dig Mine";
        events.ability.addEventHandler(ability_dig_mine, "GenerateAbilityEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Events.GenerateAbilityEvent e = (Events.GenerateAbilityEvent) event;
                    e.blob.desc = "Digs a mine";
                });
        events.ability.addEventHandler(ability_dig_mine, "AbilityActivatedEvent", (GameView view, Ability receiver,
                Event event) -> AbilityLogic.build(view, receiver.wielder, "Mine", (Tile t) -> t.name.equals("Rock")));

        // Build Vault
        final String ability_build_vault = "Build Vault";
        events.ability.addEventHandler(ability_build_vault, "GenerateAbilityEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Events.GenerateAbilityEvent e = (Events.GenerateAbilityEvent) event;
                    e.blob.desc = "Builds a vault";
                });
        events.ability.addEventHandler(ability_build_vault, "AbilityActivatedEvent", (GameView view, Ability receiver,
                Event event) -> AbilityLogic.build(view, receiver.wielder, "Vault", (Tile t) -> true));

        // Mine Coins
        final String ability_mine_coins = "Mine Coins";
        events.ability.addEventHandler(ability_mine_coins, "GenerateAbilityEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Events.GenerateAbilityEvent e = (Events.GenerateAbilityEvent) event;
                    e.blob.desc = String.format("Harvests gold coins from mines every 4 turns");
                });
        events.ability.addEventHandler(ability_mine_coins, "SpawnEvent", (GameView view, Ability receiver,
                Event event) -> view.game.mechanics.turns.addFutureTick(receiver, 4, true));
        events.ability.addEventHandler(ability_mine_coins, "TickEvent",
                (GameView view, Ability receiver, Event event) -> AbilityLogic.harvest(view, receiver.wielder,
                        "Gold Coin", (Building b) -> b.name.equals("Mine")));

        // Pick Apples
        final String ability_pick_apples = "Pick Apples";
        events.ability.addEventHandler(ability_pick_apples, "GenerateAbilityEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Events.GenerateAbilityEvent e = (Events.GenerateAbilityEvent) event;
                    e.blob.desc = String.format("Harvests apples from forests every 4 turns");
                });
        events.ability.addEventHandler(ability_pick_apples, "SpawnEvent", (GameView view, Ability receiver,
                Event event) -> view.game.mechanics.turns.addFutureTick(receiver, 4, true));
        events.ability.addEventHandler(ability_pick_apples, "TickEvent",
                (GameView view, Ability receiver, Event event) -> AbilityLogic.harvest(view, receiver.wielder, "Apple",
                        (Building b) -> b.name.equals("Forest")));

        // Shrewd
        final String ability_shrewd = "Shrewd";
        events.ability.addEventHandler(ability_shrewd, "GenerateAbilityEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Events.GenerateAbilityEvent e = (Events.GenerateAbilityEvent) event;
                    e.blob.desc = String.format("+100 auction points from vaults every 4 turns");
                });
        events.ability.addEventHandler(ability_shrewd, "SpawnEvent", (GameView view, Ability receiver,
                Event event) -> view.game.mechanics.turns.addFutureTick(receiver, 4, true));
        events.ability.addEventHandler(ability_shrewd, "TickEvent",
                (GameView view, Ability receiver, Event event) -> AbilityLogic.doOnBuilding(view, receiver.wielder,
                        (Building b) -> b.name.equals("Vault"), () -> {
                            view.game.auctionPoints += 100;
                        }));

        // Swim
        final String ability_swim = "Swim";
        events.ability.addEventHandler(ability_swim, "GenerateAbilityEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Events.GenerateAbilityEvent e = (Events.GenerateAbilityEvent) event;
                    e.blob.desc = String.format("This unit can swim on water tiles");
                });
        events.ability.addEventHandler(ability_swim, "CanUnitMoveEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Events.CanUnitMoveEvent e = (Events.CanUnitMoveEvent) event;
                    if (!e.canWalkOnTile && e.tile.name.equals("Water")) {
                        e.canWalkOnTile = true;
                    }
                });

        // Make Money
        final String ability_make_money = "Make Money";
        events.ability.addEventHandler(ability_make_money, "GenerateAbilityEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Events.GenerateAbilityEvent e = (Events.GenerateAbilityEvent) event;
                    e.blob.desc = String.format("+5 gold from mines every 4 turns");
                });
        events.ability.addEventHandler(ability_make_money, "SpawnEvent", (GameView view, Ability receiver,
                Event event) -> view.game.mechanics.turns.addFutureTick(receiver, 4, true));
        events.ability.addEventHandler(ability_make_money, "TickEvent",
                (GameView view, Ability receiver, Event event) -> AbilityLogic.doOnBuilding(view, receiver.wielder,
                        (Building b) -> b.name.equals("Mine"), () -> receiver.wielder.leader.ifPresent((Player p) -> {
                            p.gold += 5;
                        })));

        /**
         * Artifacts
         */
        // Cho's Sigil of Haste
        final String artifact_chos_sigil_of_haste = "Cho's Sigil of Haste";
        events.artifact.addEventHandler(artifact_chos_sigil_of_haste, "GenerateArtifactEvent",
                (GameView view, Artifact receiver, Event event) -> {
                    Events.GenerateArtifactEvent e = (Events.GenerateArtifactEvent) event;
                    e.blob.desc = "Your healing glyph units get +1 movement speed";
                    e.blob.image = Optional.of("golden feather");
                });
        events.artifact.addEventHandler(artifact_chos_sigil_of_haste, "ArtifactClaimedEvent",
                (GameView view, Artifact receiver, Event event) -> {
                    Events.ArtifactClaimedEvent e = (Events.ArtifactClaimedEvent) event;
                    view.game.events.signals.addListener("UnitMoveDistanceEvent", e.artifact);
                });
        events.artifact.addEventHandler(artifact_chos_sigil_of_haste, "UnitMoveDistanceEvent",
                (GameView view, Artifact receiver, Event event) -> {
                    Events.UnitMoveDistanceEvent e = (Events.UnitMoveDistanceEvent) event;
                    if (e.unit.leader.equals(receiver.getOwner()) && e.unit.glyphs.has(Glyph.HEALING)) {
                        e.distance++;
                    }
                });

        // Urdin's Scroll of Agility
        final String artifact_urdins_scroll_of_agility = "Urdin's Scroll of Agility";
        events.artifact.addEventHandler(artifact_urdins_scroll_of_agility, "GenerateArtifactEvent",
                (GameView view, Artifact receiver, Event event) -> {
                    Events.GenerateArtifactEvent e = (Events.GenerateArtifactEvent) event;
                    e.blob.desc = "Your defense glyph units get +1 movement speed";
                    e.blob.image = Optional.of("golden feather");
                });
        events.artifact.addEventHandler(artifact_urdins_scroll_of_agility, "ArtifactClaimedEvent",
                (GameView view, Artifact receiver, Event event) -> {
                    Events.ArtifactClaimedEvent e = (Events.ArtifactClaimedEvent) event;
                    view.game.events.signals.addListener("UnitMoveDistanceEvent", e.artifact);
                });
        events.artifact.addEventHandler(artifact_urdins_scroll_of_agility, "UnitMoveDistanceEvent",
                (GameView view, Artifact receiver, Event event) -> {
                    Events.UnitMoveDistanceEvent e = (Events.UnitMoveDistanceEvent) event;
                    if (e.unit.leader.equals(receiver.getOwner()) && e.unit.glyphs.has(Glyph.DEFENSE)) {
                        e.distance++;
                    }
                });

        events.artifact.addEventHandler("Sword of Aesethos", "GenerateArtifactEvent",
                (GameView view, Artifact receiver, Event event) -> {
                    Events.GenerateArtifactEvent e = (Events.GenerateArtifactEvent) event;
                    e.blob.desc = "Your units have additional critical hit chance";
                    e.blob.image = Optional.of("golden feather");
                });
        events.artifact.addEventHandler("Kauna's Amulet", "GenerateArtifactEvent",
                (GameView view, Artifact receiver, Event event) -> {
                    Events.GenerateArtifactEvent e = (Events.GenerateArtifactEvent) event;
                    e.blob.desc = "Critical hits or heals don't remove any extra favor from your patrons";
                    e.blob.image = Optional.of("golden feather");
                });
        events.artifact.addEventHandler("Staff of Wurmdel", "GenerateArtifactEvent",
                (GameView view, Artifact receiver, Event event) -> {
                    Events.GenerateArtifactEvent e = (Events.GenerateArtifactEvent) event;
                    e.blob.desc = "Your healing spells restore more health";
                    e.blob.image = Optional.of("golden feather");
                });
        events.artifact.addEventHandler("Aerog's Anvil", "GenerateArtifactEvent",
                (GameView view, Artifact receiver, Event event) -> {
                    Events.GenerateArtifactEvent e = (Events.GenerateArtifactEvent) event;
                    e.blob.desc = "Get some unit points when one of your units crafts an item";
                    e.blob.image = Optional.of("golden feather");
                });
        events.artifact.addEventHandler("Orb of Nerketo", "GenerateArtifactEvent",
                (GameView view, Artifact receiver, Event event) -> {
                    Events.GenerateArtifactEvent e = (Events.GenerateArtifactEvent) event;
                    e.blob.desc = "Your units have additional visibility";
                    e.blob.image = Optional.of("golden feather");
                });
        events.artifact.addEventHandler("Shada's Flute", "GenerateArtifactEvent",
                (GameView view, Artifact receiver, Event event) -> {
                    Events.GenerateArtifactEvent e = (Events.GenerateArtifactEvent) event;
                    e.blob.desc = "Your patrons generate unit points";
                    e.blob.image = Optional.of("golden feather");
                });
        events.artifact.addEventHandler("Stones of Thudin", "GenerateArtifactEvent",
                (GameView view, Artifact receiver, Event event) -> {
                    Events.GenerateArtifactEvent e = (Events.GenerateArtifactEvent) event;
                    e.blob.desc = "Your vaults take less damage";
                    e.blob.image = Optional.of("golden feather");
                });
        events.artifact.addEventHandler("The Chasi Bones", "GenerateArtifactEvent",
                (GameView view, Artifact receiver, Event event) -> {
                    Events.GenerateArtifactEvent e = (Events.GenerateArtifactEvent) event;
                    e.blob.desc = "Your nature glyph units have a chance to harvest an additional item";
                    e.blob.image = Optional.of("golden feather");
                });

        // Ucha's Bowl of Plenty
        final String artifact_uchas_bowl_of_plenty = "Ucha's Bowl of Plenty";
        events.artifact.addEventHandler(artifact_uchas_bowl_of_plenty, "GenerateArtifactEvent",
                (GameView view, Artifact receiver, Event event) -> {
                    Events.GenerateArtifactEvent e = (Events.GenerateArtifactEvent) event;
                    e.blob.desc = "+1 option when selecting a new unit";
                    e.blob.image = Optional.of("golden feather");
                    e.blob.chips = 2;
                });
        events.artifact.addEventHandler(artifact_uchas_bowl_of_plenty, "ArtifactClaimedEvent",
                (GameView view, Artifact receiver, Event event) -> {
                    Events.ArtifactClaimedEvent e = (Events.ArtifactClaimedEvent) event;
                    e.player.numRecruitmentOptions++;
                });

        events.artifact.addEventHandler("Nerketo's Helm", "GenerateArtifactEvent",
                (GameView view, Artifact receiver, Event event) -> {
                    Events.GenerateArtifactEvent e = (Events.GenerateArtifactEvent) event;
                    e.blob.desc = "Critical hits against your units are less effective (e.g. 1.1x damage rather than 1.5x)";
                    e.blob.image = Optional.of("golden feather");
                    e.blob.chips = 2;
                });
        events.artifact.addEventHandler("Bounty of Ahn-June", "GenerateArtifactEvent",
                (GameView view, Artifact receiver, Event event) -> {
                    Events.GenerateArtifactEvent e = (Events.GenerateArtifactEvent) event;
                    e.blob.desc = "Trade glyph units on your vaults generate more auction points";
                    e.blob.image = Optional.of("golden feather");
                    e.blob.chips = 2;
                });
        events.artifact.addEventHandler("Mark of Kung", "GenerateArtifactEvent",
                (GameView view, Artifact receiver, Event event) -> {
                    Events.GenerateArtifactEvent e = (Events.GenerateArtifactEvent) event;
                    e.blob.desc = "Your battle glyph units get +1 movement speed";
                    e.blob.image = Optional.of("golden feather");
                    e.blob.chips = 2;
                });
        events.artifact.addEventHandler("Chalco's Seal of Protection", "GenerateArtifactEvent",
                (GameView view, Artifact receiver, Event event) -> {
                    Events.GenerateArtifactEvent e = (Events.GenerateArtifactEvent) event;
                    e.blob.desc = "Your travel glyph units take less damage";
                    e.blob.image = Optional.of("golden feather");
                    e.blob.chips = 2;
                });
        events.artifact.addEventHandler("Poda's Elixir", "GenerateArtifactEvent",
                (GameView view, Artifact receiver, Event event) -> {
                    Events.GenerateArtifactEvent e = (Events.GenerateArtifactEvent) event;
                    e.blob.desc = "Some chance to not spend the glyph when you recruit a unit";
                    e.blob.image = Optional.of("golden feather");
                    e.blob.chips = 2;
                });
        events.artifact.addEventHandler("Gaia's Effigy", "GenerateArtifactEvent",
                (GameView view, Artifact receiver, Event event) -> {
                    Events.GenerateArtifactEvent e = (Events.GenerateArtifactEvent) event;
                    e.blob.desc = "Extra unit points each turn";
                    e.blob.image = Optional.of("golden feather");
                    e.blob.chips = 3;
                });
        events.artifact.addEventHandler("Rod of Adelon", "GenerateArtifactEvent",
                (GameView view, Artifact receiver, Event event) -> {
                    Events.GenerateArtifactEvent e = (Events.GenerateArtifactEvent) event;
                    e.blob.desc = "Chance to immediately recruit an enemy unit when you kill it (fresh copy with reset level/inventory)";
                    e.blob.image = Optional.of("golden feather");
                    e.blob.chips = 3;
                });
        events.artifact.addEventHandler("Blade of Sanguinor", "GenerateArtifactEvent",
                (GameView view, Artifact receiver, Event event) -> {
                    Events.GenerateArtifactEvent e = (Events.GenerateArtifactEvent) event;
                    e.blob.desc = "Your battle glyph units deal extra damage";
                    e.blob.image = Optional.of("golden feather");
                    e.blob.chips = 3;
                });
        events.artifact.addEventHandler("Cask of Amontior", "GenerateArtifactEvent",
                (GameView view, Artifact receiver, Event event) -> {
                    Events.GenerateArtifactEvent e = (Events.GenerateArtifactEvent) event;
                    e.blob.desc = "When your units generate favor it also decreases other players' favor with that patron";
                    e.blob.image = Optional.of("golden feather");
                    e.blob.chips = 3;
                });

        /**
         * Items
         */
        // Gold Coin
        final String item_gold_coin = "Gold Coin";
        events.item.addEventHandler(item_gold_coin, "GenerateItemEvent",
                (GameView view, Item receiver, Event event) -> {
                    Events.GenerateItemEvent e = (Events.GenerateItemEvent) event;
                    e.blob.desc = "Consume to increase your gold";
                    e.blob.icon = Optional.of("coin");
                    e.blob.gold = 1;
                });
        events.item.addEventHandler(item_gold_coin, "ItemConsumedEvent",
                (GameView view, Item receiver, Event event) -> ItemLogic.valuable(event));

        // Emerald
        final String item_emerald = "Emerald";
        events.item.addEventHandler(item_emerald, "GenerateItemEvent", (GameView view, Item receiver, Event event) -> {
            Events.GenerateItemEvent e = (Events.GenerateItemEvent) event;
            e.blob.desc = "Consume to increase your gold";
            e.blob.icon = Optional.of("emerald");
            e.blob.gold = 10;
        });
        events.item.addEventHandler(item_emerald, "ItemConsumedEvent",
                (GameView view, Item receiver, Event event) -> ItemLogic.valuable(event));

        // Apple
        final String item_apple = "Apple";
        events.item.addEventHandler(item_apple, "GenerateItemEvent", (GameView view, Item receiver, Event event) -> {
            Events.GenerateItemEvent e = (Events.GenerateItemEvent) event;
            e.blob.desc = "Consume to heal by 2 hit points";
            e.blob.icon = Optional.of("apple");
            e.blob.gold = 1;
        });
        events.item.addEventHandler(item_apple, "ItemConsumedEvent",
                (GameView view, Item receiver, Event event) -> ItemLogic.potion(event, 2));

        // Health Potion
        final String item_health_potion = "Health Potion";
        events.item.addEventHandler(item_health_potion, "GenerateItemEvent",
                (GameView view, Item receiver, Event event) -> {
                    Events.GenerateItemEvent e = (Events.GenerateItemEvent) event;
                    e.blob.desc = "Consume to heal by 10 hit points";
                    e.blob.icon = Optional.of("potion");
                    e.blob.gold = 1;
                });
        events.item.addEventHandler(item_health_potion, "ItemConsumedEvent",
                (GameView view, Item receiver, Event event) -> ItemLogic.potion(event, 10));
    }
}
