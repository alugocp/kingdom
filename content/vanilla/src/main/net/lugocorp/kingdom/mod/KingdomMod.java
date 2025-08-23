package net.lugocorp.kingdom.mod;
import net.lugocorp.kingdom.engine.assets.SpriteLoader;
import net.lugocorp.kingdom.game.combat.Damage;
import net.lugocorp.kingdom.game.core.AbilityLogic;
import net.lugocorp.kingdom.game.core.Events;
import net.lugocorp.kingdom.game.core.ItemLogic;
import net.lugocorp.kingdom.game.core.UnitLogic;
import net.lugocorp.kingdom.game.events.AllEventHandlers;
import net.lugocorp.kingdom.game.events.Event;
import net.lugocorp.kingdom.game.model.Ability;
import net.lugocorp.kingdom.game.model.Artifact;
import net.lugocorp.kingdom.game.model.Building;
import net.lugocorp.kingdom.game.model.Fate;
import net.lugocorp.kingdom.game.model.Item;
import net.lugocorp.kingdom.game.model.Patron;
import net.lugocorp.kingdom.game.model.Tile;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.game.model.fields.Inventory;
import net.lugocorp.kingdom.game.model.fields.Inventory.InventoryType;
import net.lugocorp.kingdom.game.model.fields.Race;
import net.lugocorp.kingdom.game.model.glyph.Glyph;
import net.lugocorp.kingdom.ui.menu.ArtifactNode;
import net.lugocorp.kingdom.ui.menu.FateNode;
import net.lugocorp.kingdom.ui.menu.InventoryNode;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.code.SideEffect;
import net.lugocorp.kingdom.utils.mods.GameMod;
import java.util.Optional;

/**
 * This mod defines all the content for version 1.0 of the base game
 */
public class KingdomMod implements GameMod {

    /** {@inheritdoc} */
    @Override
    public String getKey() {
        return "vanilla";
    }

    /** {@inheritdoc} */
    @Override
    public String getName() {
        return "Vanilla";
    }

    /** {@inheritdoc} */
    @Override
    public String getDescription() {
        return "Contains all of the base content for this game.";
    }

    /** {@inheritdoc} */
    @Override
    public void registerSprites(SpriteLoader sprites) {
        sprites.register("placeholder", "icons", InventoryNode.SIDE, InventoryNode.SIDE, 0, 0);
        sprites.register("potion", "icons", InventoryNode.SIDE, InventoryNode.SIDE, 1, 0);
        sprites.register("apple", "icons", InventoryNode.SIDE, InventoryNode.SIDE, 2, 0);
        sprites.register("pouch", "icons", InventoryNode.SIDE, InventoryNode.SIDE, 3, 0);
        sprites.register("coin", "icons", InventoryNode.SIDE, InventoryNode.SIDE, 0, 1);
        sprites.register("sword", "icons", InventoryNode.SIDE, InventoryNode.SIDE, 1, 1);
        sprites.register("shield", "icons", InventoryNode.SIDE, InventoryNode.SIDE, 2, 1);
        sprites.register("leaf", "icons", InventoryNode.SIDE, InventoryNode.SIDE, 3, 1);
        sprites.register("mushroom", "icons", InventoryNode.SIDE, InventoryNode.SIDE, 0, 2);
        sprites.register("emerald", "icons", InventoryNode.SIDE, InventoryNode.SIDE, 1, 2);
        sprites.register("bone", "icons", InventoryNode.SIDE, InventoryNode.SIDE, 2, 2);
        sprites.register("golden feather", "artifacts", ArtifactNode.WIDTH, ArtifactNode.HEIGHT, 0, 0);
        sprites.register("raider", "fates", FateNode.WIDTH, FateNode.HEIGHT, 0, 0);
        sprites.register("merchant", "fates", FateNode.WIDTH, FateNode.HEIGHT, 1, 0);
        sprites.register("veteran", "fates", FateNode.WIDTH, FateNode.HEIGHT, 2, 0);
        sprites.register("devout", "fates", FateNode.WIDTH, FateNode.HEIGHT, 3, 0);
        sprites.register("sentinel", "fates", FateNode.WIDTH, FateNode.HEIGHT, 0, 1);
        sprites.register("usurper", "fates", FateNode.WIDTH, FateNode.HEIGHT, 1, 1);
        sprites.register("forager", "fates", FateNode.WIDTH, FateNode.HEIGHT, 2, 1);
    }

    /** {@inheritdoc} */
    @Override
    public void registerEvents(AllEventHandlers events) {
        /**
         * SECTION 01 Races
         */
        final Race HUMAN = new Race("Human");
        final Race ELF = new Race("Elf");
        final Race DWARF = new Race("Dwarf");
        final Race GNOME = new Race("Gnome");
        final Race ORC = new Race("Orc");
        final Race GOBLIN = new Race("Goblin");
        final Race GIANT = new Race("Giant");
        final Race GRUE = new Race("Grue", GIANT);
        final Race OGRE = new Race("Ogre", GIANT);
        final Race ETTIN = new Race("Ettin", GIANT);
        final Race TROLL = new Race("Troll", GIANT);
        final Race CENTAUR = new Race("Centaur");
        final Race SALAMANDER = new Race("Salamander");
        final Race TORTUGAN = new Race("Tortugan");
        final Race KAPPA = new Race("Kappa", TORTUGAN);
        final Race BROWNIE = new Race("Brownie");
        final Race KOBOLD = new Race("Kobold");
        final Race FIRBOLG = new Race("Firbolg", KOBOLD);
        final Race MERFOLK = new Race("Merfolk");
        final Race GARUDA = new Race("Garuda");
        final Race DRAGONKIN = new Race("Dragonkin");
        final Race TOADSTOOL = new Race("Toadstool");
        final Race SPRITE = new Race("Sprite");
        final Race DRYAD = new Race("Dryad", SPRITE);
        final Race DEMON = new Race("Demon");
        final Race TULPA = new Race("Tulpa");
        final Race GOLEM = new Race("Golem");
        final Race PLASMOID = new Race("Plasmoid");
        final Race UNDEAD = new Race("Undead");
        final Race ELEMENTAL = new Race("Elemental");
        final Race GEMSTONE = new Race("Gemstone", ELEMENTAL);

        /**
         * SECTION 02 Tags
         */
        final String tag_natural = "natural";
        final String tag_fruit = "fruit";

        /**
         * SECTION 03 Default handlers
         */

        // GetsHungry
        events.unit.setDefaultHandler("GetsHungry", (GameView view, Unit receiver,
                Event event) -> () -> view.game.mechanics.turns.addFutureTick("HungerStrikes", receiver, 1, true));

        // HungerStrikes
        events.unit.setDefaultHandler("HungerStrikes", (GameView view, Unit receiver, Event event) -> {
            if (receiver.getLeader().isPresent()) {
                return () -> receiver.loseLoyalty(view.game, 1);
            }
            ((Events.RepeatedEvent) event).repeat = false;
            return SideEffect.none;
        });

        // CanEatEvent
        events.unit.setDefaultHandler("CanEatEvent", (GameView view, Unit receiver, Event event) -> {
            Events.CanEatEvent e = (Events.CanEatEvent) event;
            e.edible = e.item.tags.has(tag_fruit);
            return SideEffect.none;
        });

        // UnitMoveDistanceEvent
        events.unit.setDefaultHandler("UnitMoveDistanceEvent", (GameView view, Unit receiver, Event event) -> {
            Events.UnitMoveDistanceEvent e = (Events.UnitMoveDistanceEvent) event;
            e.distance = 2;
            return SideEffect.none;
        });

        /**
         * SECTION 04 Tiles
         */

        // Grass
        final String tile_grass = "Grass";
        events.tile.addEventHandler(tile_grass, "GenerateTileEvent", (GameView view, Tile receiver, Event event) -> {
            Events.GenerateTileEvent e = (Events.GenerateTileEvent) event;
            e.blob.setModelInstance(view.av, "grass");
            e.blob.setMinimapColor(0x2c9965);
            return SideEffect.none;
        });

        // Rock
        events.tile.addEventHandler("Rock", "GenerateTileEvent", (GameView view, Tile receiver, Event event) -> {
            Events.GenerateTileEvent e = (Events.GenerateTileEvent) event;
            e.blob.setModelInstance(view.av, "grass");
            e.blob.setMinimapColor(0x666666);
            e.blob.setMaterial("rock");
            return SideEffect.none;
        });

        // Sand
        events.tile.addEventHandler("Sand", "GenerateTileEvent", (GameView view, Tile receiver, Event event) -> {
            Events.GenerateTileEvent e = (Events.GenerateTileEvent) event;
            e.blob.setModelInstance(view.av, "grass");
            e.blob.setMinimapColor(0xc7c567);
            e.blob.setMaterial("sand");
            return SideEffect.none;
        });

        // Snow
        events.tile.addEventHandler("Snow", "GenerateTileEvent", (GameView view, Tile receiver, Event event) -> {
            Events.GenerateTileEvent e = (Events.GenerateTileEvent) event;
            e.blob.setModelInstance(view.av, "grass");
            e.blob.setMinimapColor(0xffffff);
            e.blob.setMaterial("snow");
            return SideEffect.none;
        });

        // Water
        events.tile.addEventHandler("Water", "GenerateTileEvent", (GameView view, Tile receiver, Event event) -> {
            Events.GenerateTileEvent e = (Events.GenerateTileEvent) event;
            e.blob.setModelInstance(view.av, "water");
            e.blob.setMinimapColor(0x20c7f7);
            e.blob.setObstacle(true);
            e.blob.setWave(true);
            return SideEffect.none;
        });

        // Lava
        events.tile.addEventHandler("Lava", "GenerateTileEvent", (GameView view, Tile receiver, Event event) -> {
            Events.GenerateTileEvent e = (Events.GenerateTileEvent) event;
            e.blob.setModelInstance(view.av, "water");
            e.blob.setMinimapColor(0xcf3b23);
            e.blob.setMaterial("lava");
            e.blob.setObstacle(true);
            e.blob.setWave(true);
            return SideEffect.none;
        });

        /**
         * SECTION 05 Buildings
         */

        // Mine
        final String building_mine = "Mine";
        events.building.addEventHandler(building_mine, "GenerateBuildingEvent",
                (GameView view, Building receiver, Event event) -> {
                    Events.GenerateBuildingEvent e = (Events.GenerateBuildingEvent) event;
                    e.blob.setModelInstance(view.av, "mine");
                    e.blob.desc = "Mines provide valuables like gold coins";
                    e.blob.setActive();
                    return SideEffect.none;
                });

        // Vault
        final String building_vault = "Vault";
        events.building.addEventHandler(building_vault, "GenerateBuildingEvent",
                (GameView view, Building receiver, Event event) -> {
                    Events.GenerateBuildingEvent e = (Events.GenerateBuildingEvent) event;
                    e.blob.setModelInstance(view.av, "vault");
                    e.blob.desc = "Vaults can store excess items and be used in auctions";
                    e.blob.items = Optional.of(new Inventory(InventoryType.BUILDING, 24));
                    e.blob.setActive();
                    return SideEffect.none;
                });

        // Forest
        final String building_forest = "Forest";
        events.building.addEventHandler(building_forest, "GenerateBuildingEvent",
                (GameView view, Building receiver, Event event) -> {
                    Events.GenerateBuildingEvent e = (Events.GenerateBuildingEvent) event;
                    e.blob.setModelInstance(view.av, "forest");
                    e.blob.desc = "Don't miss the forest for the trees";
                    e.blob.setMinimapColor(0x257d53);
                    return SideEffect.none;
                });

        // Taiga
        final String building_taiga = "Taiga";
        events.building.addEventHandler(building_taiga, "GenerateBuildingEvent",
                (GameView view, Building receiver, Event event) -> {
                    Events.GenerateBuildingEvent e = (Events.GenerateBuildingEvent) event;
                    e.blob.setModelInstance(view.av, "forest");
                    e.blob.desc = "The trees are pretty this time of year";
                    e.blob.setMinimapColor(0xb4c3c7);
                    e.blob.setMaterial("taiga");
                    return SideEffect.none;
                });

        // Meadow
        final String building_meadow = "Meadow";
        events.building.addEventHandler(building_meadow, "GenerateBuildingEvent",
                (GameView view, Building receiver, Event event) -> {
                    Events.GenerateBuildingEvent e = (Events.GenerateBuildingEvent) event;
                    e.blob.setModelInstance(view.av, "meadow");
                    e.blob.desc = "Stay a while and smell the roses";
                    e.blob.setMinimapColor(0x4dd349);
                    return SideEffect.none;
                });

        // Oasis
        final String building_oasis = "Oasis";
        events.building.addEventHandler(building_oasis, "GenerateBuildingEvent",
                (GameView view, Building receiver, Event event) -> {
                    Events.GenerateBuildingEvent e = (Events.GenerateBuildingEvent) event;
                    e.blob.setModelInstance(view.av, "oasis");
                    e.blob.desc = "Moments of respite from the overbearing sun";
                    e.blob.setMinimapColor(0x2c9965);
                    return SideEffect.none;
                });

        // Shrubland
        final String building_shrubland = "Shrubland";
        events.building.addEventHandler(building_shrubland, "GenerateBuildingEvent",
                (GameView view, Building receiver, Event event) -> {
                    Events.GenerateBuildingEvent e = (Events.GenerateBuildingEvent) event;
                    e.blob.setModelInstance(view.av, "shrubland");
                    e.blob.desc = "Meadows in the middle of the desert";
                    e.blob.setMinimapColor(0x4dd349);
                    return SideEffect.none;
                });

        // Mountain
        final String building_mountain = "Mountain";
        events.building.addEventHandler(building_mountain, "GenerateBuildingEvent",
                (GameView view, Building receiver, Event event) -> {
                    Events.GenerateBuildingEvent e = (Events.GenerateBuildingEvent) event;
                    e.blob.setModelInstance(view.av, "mountain");
                    e.blob.desc = "An immovable object";
                    e.blob.setMinimapColor(0x875f9a);
                    e.blob.combat.health.invulnerable();
                    e.blob.setObstacle(true);
                    return SideEffect.none;
                });

        // Healing Fountain
        final String building_healing_fountain = "Healing Fountain";
        events.building.addEventHandler(building_healing_fountain, "GenerateBuildingEvent",
                (GameView view, Building receiver, Event event) -> {
                    Events.GenerateBuildingEvent e = (Events.GenerateBuildingEvent) event;
                    e.blob.setModelInstance(view.av, "fountain");
                    e.blob.desc = "Heals an occupying unit";
                    e.blob.setMinimapColor(0x875f9a);
                    e.blob.setActive();
                    return SideEffect.none;
                });

        /**
         * SECTION 06 Patrons
         */

        // Joyous Reaper
        // Great Corn Woman
        // Lord Shui, Guardian of the River
        // The Pond Troll
        final String patron_pond_troll = "The Pond Troll";
        events.patron.addEventHandler(patron_pond_troll, "GeneratePatronEvent",
                (GameView view, Patron receiver, Event event) -> {
                    Events.GeneratePatronEvent e = (Events.GeneratePatronEvent) event;
                    e.blob.setModelInstance(view.av, "pond-troll");
                    e.blob.desc = "The favorite player's units can traverse water tiles and have a chance to harvest items when they do";
                    e.blob.preference = "Units that cannot swim";
                    e.blob.isPreferredUnitType = (Unit u) -> !u.hasPassiveAbility("Swim");
                    // TODO implement me
                    return SideEffect.none;
                });

        // The Eternal Guardian
        // Flutterwing
        // Wise Mountain
        // Wise Oak
        // Ahn-Juné
        // The Shining Eyes
        final String patron_shining_eyes = "The Shining Eyes";
        events.patron.addEventHandler(patron_shining_eyes, "GeneratePatronEvent",
                (GameView view, Patron receiver, Event event) -> {
                    Events.GeneratePatronEvent e = (Events.GeneratePatronEvent) event;
                    e.blob.setModelInstance(view.av, "shining-eyes");
                    e.blob.desc = "A floating pyramid thing";
                    e.blob.preference = "Units with the healing glyph";
                    e.blob.isPreferredUnitType = (Unit u) -> u.glyphs.has(Glyph.HEALING);
                    // TODO implement me
                    return SideEffect.none;
                });

        /**
         * SECTION 07 Items
         */

        // Gold Coin
        final String item_gold_coin = "Gold Coin";
        events.item.addEventHandler(item_gold_coin, "GenerateItemEvent",
                (GameView view, Item receiver, Event event) -> {
                    Events.GenerateItemEvent e = (Events.GenerateItemEvent) event;
                    e.blob.desc = "Consume to increase your gold";
                    e.blob.icon = Optional.of("coin");
                    e.blob.gold = 1;
                    return SideEffect.none;
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
            return SideEffect.none;
        });
        events.item.addEventHandler(item_emerald, "ItemConsumedEvent",
                (GameView view, Item receiver, Event event) -> ItemLogic.valuable(event));

        // Apple
        final String item_apple = "Apple";
        events.item.addEventHandler(item_apple, "GenerateItemEvent", (GameView view, Item receiver, Event event) -> {
            Events.GenerateItemEvent e = (Events.GenerateItemEvent) event;
            e.blob.desc = "Consume to stave off hunger";
            e.blob.icon = Optional.of("apple");
            e.blob.gold = 1;
            e.blob.tags.add(tag_natural).add(tag_fruit);
            return SideEffect.none;
        });
        events.item.addEventHandler(item_apple, "ItemConsumedEvent",
                (GameView view, Item receiver, Event event) -> ItemLogic.food(view, event));

        // Health Potion
        final String item_health_potion = "Health Potion";
        events.item.addEventHandler(item_health_potion, "GenerateItemEvent",
                (GameView view, Item receiver, Event event) -> {
                    Events.GenerateItemEvent e = (Events.GenerateItemEvent) event;
                    e.blob.desc = "Consume to heal by 10 hit points";
                    e.blob.icon = Optional.of("potion");
                    e.blob.gold = 1;
                    return SideEffect.none;
                });
        events.item.addEventHandler(item_health_potion, "ItemConsumedEvent",
                (GameView view, Item receiver, Event event) -> ItemLogic.potion(event, 10));

        // Incense
        // Sack of Gold
        // Capital
        // Shellcap Armor
        // Stones
        // Sword
        // Shield
        // Staff
        // Prayer Beads
        // Rites of the Merchant
        // Rites of the Vendor
        // Pocket
        // Raider's Mail
        // Sentinel's Helm
        // Counselor's Ring
        // Matron's Sash
        // Wizard's Cap
        // Youth's Pendant
        // General's Trousers
        // Courier's Boots
        // Rogue's Gloves
        // Scion's Beltbuckle
        // Priest's Robes
        // Acidic Solute
        // Binding Solute
        // Life-Giving Solute
        // Blessed Solute
        // Feather of Bravery
        // Iron Beak Brace
        // Hollow Bone Rattle
        // Bag of Shiny Pebbles
        // Thorny Rose Staff
        // Overgrown Shield
        // Sap of Unbreaking
        // Sacred Pollen
        // Advanced Spear
        // Sentinel's Shield
        // Healing Incantation
        // Devout Incantation
        // Great Hammer
        // Battle Armaments
        // Bandage Kit
        // Dearly Held Idol
        // Stoneshell Mace
        // Shell Salve
        // Shell-Sealing Goo
        // Sacred Shell Rattle
        // Blessed Charm
        // Bloody Totem
        // Phoenix Blossom
        // Sling and Stone
        // Life-Giving Elixir
        // Blood-Thirsty Blade
        // Blood-Soaked Mail
        // Leather Armor
        // Net Bag
        // Iron Mace
        // Guardian's Axe
        // Grizzled Polearm
        // Avenger's Blade
        // Sorcerous Robes
        // Shiny Brooch
        // Rougish Sneaker
        // Swift Cape
        // Vagabond's Gloves
        // Noble Spearhead
        // Sacrificial Dagger
        // Heavy Iron Platemail
        // Keg of Strong Brew
        // Pendant of Protection
        // Charm of Connected Life
        // Guardian's Tome
        // Fine Mesh Chainmail
        // Black Market Gauntlet
        // Alloy Greaves
        // Advanced Chainmail
        // Saintly Helm
        // Idol of Lordly Favor
        // Sacrificial Club
        // Spritely Ale
        // Necklace of the Clear Mind
        // Life-Giver's Shawl
        // Ancient Tome of Healing
        // Decorated Urn
        // Devious Magic Wand
        // Plainswalker's Cloak
        // Gauntlet of Precious Light
        // Noble Ruby Ring
        // Priesthood Vestments
        // Dwarf's Pickaxe
        // Dragonkin's Helm
        // Merfolk's Net
        // Firbolg's Cloak
        // Sprite's Gloves
        // Brownie's Boots
        // Raksha's Pendant
        // Naga's Scepter
        // Well-Crafted Bow
        // Mycelium Ring
        // Cyclical Rune
        // Mercenary's Blade
        // Wizard's Staff
        // Floral seeds
        // Arboreal seeds
        // Arctic seeds
        // Cactus seeds
        // Pioneering seeds
        // Digging Kit
        // Telescope
        // Ornate Boots
        // Satchel
        // Expertly Crafted Blade
        // Queensguard Shield
        // Arcane Wand
        // Ancient Tome
        // Vendor's Scales
        // Merfolk Slippers
        // Stygian Eye
        // Ring of Life Eternal
        // Truffle
        // Scholarly Robes
        // Sanguine Blade
        // Necrotic Tome
        // Thoughtform Sword
        // Shield of the Subconscious
        // Staff of Inner Peace
        // Rod of Psychic Devotion
        // Tree Trunk Club
        // Castle Gate Aegis
        // Vase of Sacred Waters
        // Amulet of the Progenitors
        // Sharpened Quartz
        // Igneous Armaments
        // Moss-covered Stone
        // Glyphic Geode
        // Self-Sustaining Soulstone
        // Hero's Call

        /**
         * SECTION 08 Abilities
         */

        // Acid Skin
        final String ability_acid_skin = "Acid Skin";
        events.ability.addEventHandler(ability_acid_skin, "GenerateAbilityEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Events.GenerateAbilityEvent e = (Events.GenerateAbilityEvent) event;
                    e.blob.desc = String.format("Adjacent attackers take damage");
                    // TODO implement me
                    return SideEffect.none;
                });

        // Bite
        final String ability_bite = "Bite";
        events.ability.addEventHandler(ability_bite, "GenerateAbilityEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Events.GenerateAbilityEvent e = (Events.GenerateAbilityEvent) event;
                    e.blob.desc = "Basic attack";
                    return SideEffect.none;
                });
        events.ability.addEventHandler(ability_bite, "AbilityActivatedEvent", (GameView view, Ability receiver,
                Event event) -> AbilityLogic.attack(view, receiver.wielder, new Damage(4), 1));

        // Build Healing Fountain
        final String ability_build_healing_fountain = "Build Healing Fountain";
        events.ability.addEventHandler(ability_build_healing_fountain, "GenerateAbilityEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Events.GenerateAbilityEvent e = (Events.GenerateAbilityEvent) event;
                    e.blob.desc = "Constructs a healing fountain";
                    return SideEffect.none;
                });
        events.ability.addEventHandler(ability_build_healing_fountain, "AbilityActivatedEvent",
                (GameView view, Ability receiver, Event event) -> AbilityLogic.build(view, receiver.wielder,
                        building_healing_fountain, (Tile t) -> true));

        // Build Vault
        final String ability_build_vault = "Build Vault";
        events.ability.addEventHandler(ability_build_vault, "GenerateAbilityEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Events.GenerateAbilityEvent e = (Events.GenerateAbilityEvent) event;
                    e.blob.desc = "Builds a vault";
                    return SideEffect.none;
                });
        events.ability.addEventHandler(ability_build_vault, "AbilityActivatedEvent", (GameView view, Ability receiver,
                Event event) -> AbilityLogic.build(view, receiver.wielder, building_vault, (Tile t) -> true));

        // Collapse Mine
        final String ability_collapse_mine = "Collapse Mine";
        events.ability.addEventHandler(ability_collapse_mine, "GenerateAbilityEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Events.GenerateAbilityEvent e = (Events.GenerateAbilityEvent) event;
                    e.blob.desc = String.format(
                            "Target a mine occupied by an enemy unit. The unit, mine, and any adjacent enemy units all take damage.");
                    // TODO implement me
                    return SideEffect.none;
                });

        // Combat Loot
        final String ability_combat_loot = "Combat Loot";
        events.ability.addEventHandler(ability_combat_loot, "GenerateAbilityEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Events.GenerateAbilityEvent e = (Events.GenerateAbilityEvent) event;
                    e.blob.desc = String.format("Attacks do more damage if this unit has a hauled item");
                    // TODO implement me
                    return SideEffect.none;
                });

        // Crystal Skin
        final String ability_crystal_skin = "Crystal Skin";
        events.ability.addEventHandler(ability_crystal_skin, "GenerateAbilityEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Events.GenerateAbilityEvent e = (Events.GenerateAbilityEvent) event;
                    e.blob.desc = String.format("Extra defense");
                    // TODO implement me
                    return SideEffect.none;
                });
        events.ability.addEventHandler(ability_crystal_skin, "TakeDamageEvent",
                (GameView view, Ability receiver, Event event) -> AbilityLogic.defense(event, 2));

        // Deposit Seeds
        final String ability_deposit_seeds = "Deposit Seeds";
        events.ability.addEventHandler(ability_deposit_seeds, "GenerateAbilityEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Events.GenerateAbilityEvent e = (Events.GenerateAbilityEvent) event;
                    e.blob.desc = String.format("Chance to spawn a meadow when this unit moves");
                    // TODO implement me
                    return SideEffect.none;
                });

        // Dig Mine
        final String ability_dig_mine = "Dig Mine";
        events.ability.addEventHandler(ability_dig_mine, "GenerateAbilityEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Events.GenerateAbilityEvent e = (Events.GenerateAbilityEvent) event;
                    e.blob.desc = "Digs a mine";
                    return SideEffect.none;
                });
        events.ability.addEventHandler(ability_dig_mine, "AbilityActivatedEvent",
                (GameView view, Ability receiver, Event event) -> AbilityLogic.build(view, receiver.wielder,
                        building_mine, (Tile t) -> t.name.equals("Rock")));

        // Dungeon Delve
        final String ability_dungeon_delve = "Dungeon Delve";
        events.ability.addEventHandler(ability_dungeon_delve, "GenerateAbilityEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Events.GenerateAbilityEvent e = (Events.GenerateAbilityEvent) event;
                    e.blob.desc = String.format("Attack that generates items when used on an active building");
                    // TODO implement me
                    return SideEffect.none;
                });

        // Edible
        final String ability_edible = "Edible";
        events.ability.addEventHandler(ability_edible, "GenerateAbilityEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Events.GenerateAbilityEvent e = (Events.GenerateAbilityEvent) event;
                    e.blob.desc = String.format("Generates food");
                    // TODO implement me
                    return SideEffect.none;
                });
        events.ability.addEventHandler(ability_edible, "SpawnEvent", (GameView view, Ability receiver, Event event) -> {
            view.game.mechanics.turns.addFutureTick("TickEvent", receiver, 4, true);
            return SideEffect.none;
        });
        events.ability.addEventHandler(ability_edible, "TickEvent", (GameView view, Ability receiver,
                Event event) -> AbilityLogic.harvest(view, receiver.wielder, item_apple, (Building b) -> true));

        // Fire Cannon
        final String ability_fire_cannon = "Fire Cannon";
        events.ability.addEventHandler(ability_fire_cannon, "GenerateAbilityEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Events.GenerateAbilityEvent e = (Events.GenerateAbilityEvent) event;
                    e.blob.desc = String.format("Ranged attack which deals extra damage against buildings");
                    // TODO implement me
                    return SideEffect.none;
                });

        // Fire Laser
        final String ability_fire_laser = "Fire Laser";
        events.ability.addEventHandler(ability_fire_laser, "GenerateAbilityEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Events.GenerateAbilityEvent e = (Events.GenerateAbilityEvent) event;
                    e.blob.desc = String.format("Ranged attack which damages several units in a line");
                    // TODO implement me
                    return SideEffect.none;
                });

        // Green Fortress
        final String ability_green_fortress = "Green Fortress";
        events.ability.addEventHandler(ability_green_fortress, "GenerateAbilityEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Events.GenerateAbilityEvent e = (Events.GenerateAbilityEvent) event;
                    e.blob.desc = String.format("Extra defense on forests");
                    return SideEffect.none;
                });
        events.ability.addEventHandler(ability_green_fortress, "TakeDamageEvent",
                (GameView view, Ability receiver, Event event) -> {
                    boolean isForest = view.game.world.getTile(receiver.wielder.getPoint()).map((Tile t) -> t.building)
                            .map((Building b) -> b.name.equals(building_forest)).orElse(false);
                    return isForest ? AbilityLogic.defense(event, 2) : SideEffect.none;
                });

        // Heal Wounds
        final String ability_heal_wounds = "Heal Wounds";
        events.ability.addEventHandler(ability_heal_wounds, "GenerateAbilityEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Events.GenerateAbilityEvent e = (Events.GenerateAbilityEvent) event;
                    e.blob.desc = "Heals 5 damage";
                    return SideEffect.none;
                });
        events.ability.addEventHandler(ability_heal_wounds, "AbilityActivatedEvent",
                (GameView view, Ability receiver, Event event) -> AbilityLogic.healUnit(view, receiver.wielder, 5));

        // Hug
        final String ability_hug = "Hug";
        events.ability.addEventHandler(ability_hug, "GenerateAbilityEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Events.GenerateAbilityEvent e = (Events.GenerateAbilityEvent) event;
                    e.blob.desc = String.format("Heals the target adjacent unit for a few hit points");
                    return SideEffect.none;
                });
        events.ability.addEventHandler(ability_hug, "AbilityActivatedEvent",
                (GameView view, Ability receiver, Event event) -> AbilityLogic.healUnit(view, receiver.wielder, 2));

        // Hungry Frog Magic
        final String ability_hungry_frog_magic = "Hungry Frog Magic";
        events.ability.addEventHandler(ability_hungry_frog_magic, "GenerateAbilityEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Events.GenerateAbilityEvent e = (Events.GenerateAbilityEvent) event;
                    e.blob.desc = String.format("Consumes all hauled items and heals adjacent friendly units");
                    // TODO implement me
                    return SideEffect.none;
                });

        // Hunt Fish
        final String ability_hunt_fish = "Hunt Fish";
        events.ability.addEventHandler(ability_hunt_fish, "GenerateAbilityEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Events.GenerateAbilityEvent e = (Events.GenerateAbilityEvent) event;
                    e.blob.desc = String.format("Harvests fish from water tiles");
                    // TODO implement me
                    return SideEffect.none;
                });

        // Hurl Rock
        final String ability_hurl_rock = "Hurl Rock";
        events.ability.addEventHandler(ability_hurl_rock, "GenerateAbilityEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Events.GenerateAbilityEvent e = (Events.GenerateAbilityEvent) event;
                    e.blob.desc = String.format("Ranged attack with chance to stun");
                    // TODO implement me
                    return SideEffect.none;
                });

        // Life Aura
        final String ability_life_aura = "Life Aura";
        events.ability.addEventHandler(ability_life_aura, "GenerateAbilityEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Events.GenerateAbilityEvent e = (Events.GenerateAbilityEvent) event;
                    e.blob.desc = String.format("Generates unit points");
                    // TODO implement me
                    return SideEffect.none;
                });

        // Liquifying Presence
        final String ability_liquifying_presence = "Liquifying Presence";
        events.ability.addEventHandler(ability_liquifying_presence, "GenerateAbilityEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Events.GenerateAbilityEvent e = (Events.GenerateAbilityEvent) event;
                    e.blob.desc = String.format("Deals damage to an occupied passive building");
                    // TODO implement me
                    return SideEffect.none;
                });

        // Local Defender
        final String ability_local_defender = "Local Defender";
        events.ability.addEventHandler(ability_local_defender, "GenerateAbilityEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Events.GenerateAbilityEvent e = (Events.GenerateAbilityEvent) event;
                    e.blob.desc = String.format("Adjacent passive buildings are treated as active");
                    // TODO implement me
                    return SideEffect.none;
                });

        // Market Boom
        final String ability_market_boom = "Market Boom";
        events.ability.addEventHandler(ability_market_boom, "GenerateAbilityEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Events.GenerateAbilityEvent e = (Events.GenerateAbilityEvent) event;
                    e.blob.desc = String.format("Attacks generate auction points");
                    // TODO implement me
                    return SideEffect.none;
                });

        // Market Indicator
        final String ability_market_indicator = "Market Indicator";
        events.ability.addEventHandler(ability_market_indicator, "GenerateAbilityEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Events.GenerateAbilityEvent e = (Events.GenerateAbilityEvent) event;
                    e.blob.desc = String.format("Generates auction points when adjacent to a vault");
                    // TODO implement me
                    return SideEffect.none;
                });

        // Mine Gems
        final String ability_mine_gems = "Mine Gems";
        events.ability.addEventHandler(ability_mine_gems, "GenerateAbilityEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Events.GenerateAbilityEvent e = (Events.GenerateAbilityEvent) event;
                    e.blob.desc = String.format("Harvests gems from mines");
                    // TODO implement me
                    return SideEffect.none;
                });

        // Mine Gold
        final String ability_mine_gold = "Mine Gold";
        events.ability.addEventHandler(ability_mine_gold, "GenerateAbilityEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Events.GenerateAbilityEvent e = (Events.GenerateAbilityEvent) event;
                    e.blob.desc = "Harvests gold coins from mines every 4 turns";
                    return SideEffect.none;
                });
        events.ability.addEventHandler(ability_mine_gold, "SpawnEvent",
                (GameView view, Ability receiver, Event event) -> {
                    view.game.mechanics.turns.addFutureTick("TickEvent", receiver, 4, true);
                    return SideEffect.none;
                });
        events.ability.addEventHandler(ability_mine_gold, "TickEvent",
                (GameView view, Ability receiver, Event event) -> AbilityLogic.harvest(view, receiver.wielder,
                        item_gold_coin, (Building b) -> b.name.equals("Mine")));

        // Mountain Strider
        final String ability_mountain_strider = "Mountain Strider";
        events.ability.addEventHandler(ability_mountain_strider, "GenerateAbilityEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Events.GenerateAbilityEvent e = (Events.GenerateAbilityEvent) event;
                    e.blob.desc = String.format("This unit can traverse mountains");
                    // TODO implement me
                    return SideEffect.none;
                });

        // Night Vision
        final String ability_night_vision = "Night Vision";
        events.ability.addEventHandler(ability_night_vision, "GenerateAbilityEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Events.GenerateAbilityEvent e = (Events.GenerateAbilityEvent) event;
                    e.blob.desc = String.format("This unit can see normally at night");
                    // TODO implement me
                    return SideEffect.none;
                });

        // Pick Apples
        final String ability_pick_apples = "Pick Apples";
        events.ability.addEventHandler(ability_pick_apples, "GenerateAbilityEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Events.GenerateAbilityEvent e = (Events.GenerateAbilityEvent) event;
                    e.blob.desc = "Harvests apples from forests every 4 turns";
                    return SideEffect.none;
                });
        events.ability.addEventHandler(ability_pick_apples, "SpawnEvent",
                (GameView view, Ability receiver, Event event) -> {
                    view.game.mechanics.turns.addFutureTick("TickEvent", receiver, 4, true);
                    return SideEffect.none;
                });
        events.ability.addEventHandler(ability_pick_apples, "TickEvent",
                (GameView view, Ability receiver, Event event) -> AbilityLogic.harvest(view, receiver.wielder, "Apple",
                        (Building b) -> b.name.equals("Forest")));

        // Pick Flowers
        final String ability_pick_flowers = "Pick Flowers";
        events.ability.addEventHandler(ability_pick_flowers, "GenerateAbilityEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Events.GenerateAbilityEvent e = (Events.GenerateAbilityEvent) event;
                    e.blob.desc = String.format("Harvests flowers from meadows every 4 turns");
                    // TODO implement me
                    return SideEffect.none;
                });

        // Plant Forest
        final String ability_plant_forest = "Plant Forest";
        events.ability.addEventHandler(ability_plant_forest, "GenerateAbilityEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Events.GenerateAbilityEvent e = (Events.GenerateAbilityEvent) event;
                    e.blob.desc = "Plants a forest";
                    return SideEffect.none;
                });
        events.ability.addEventHandler(ability_plant_forest, "AbilityActivatedEvent",
                (GameView view, Ability receiver, Event event) -> AbilityLogic.build(view, receiver.wielder,
                        building_forest, (Tile t) -> t.name.equals(tile_grass)));

        // Plant Meadow
        final String ability_plant_meadow = "Plant Meadow";
        events.ability.addEventHandler(ability_plant_meadow, "GenerateAbilityEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Events.GenerateAbilityEvent e = (Events.GenerateAbilityEvent) event;
                    e.blob.desc = "Plants a meadow";
                    return SideEffect.none;
                });
        events.ability.addEventHandler(ability_plant_meadow, "AbilityActivatedEvent",
                (GameView view, Ability receiver, Event event) -> AbilityLogic.build(view, receiver.wielder,
                        building_meadow, (Tile t) -> t.name.equals(tile_grass)));

        // Plate Mail
        final String ability_plate_mail = "Plate Mail";
        events.ability.addEventHandler(ability_plate_mail, "GenerateAbilityEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Events.GenerateAbilityEvent e = (Events.GenerateAbilityEvent) event;
                    e.blob.desc = String.format("Extra defense");
                    return SideEffect.none;
                });
        events.ability.addEventHandler(ability_plate_mail, "TakeDamageEvent",
                (GameView view, Ability receiver, Event event) -> AbilityLogic.defense(event, 2));

        // Regeneration
        final String ability_regeneration = "Regeneration";
        events.ability.addEventHandler(ability_regeneration, "GenerateAbilityEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Events.GenerateAbilityEvent e = (Events.GenerateAbilityEvent) event;
                    e.blob.desc = String.format("This unit heals a little each turn");
                    // TODO implement me
                    return SideEffect.none;
                });
        events.ability.addEventHandler(ability_regeneration, "SpawnEvent",
                (GameView view, Ability receiver, Event event) -> {
                    view.game.mechanics.turns.addFutureTick("TickEvent", receiver, 1, true);
                    return SideEffect.none;
                });
        events.ability.addEventHandler(ability_regeneration, "TickEvent",
                (GameView view, Ability receiver, Event event) -> () -> receiver.caster.combat.health.heal(1));

        // Revenge of the Forest
        final String ability_revenge_of_the_forest = "Revenge of the Forest";
        events.ability.addEventHandler(ability_revenge_of_the_forest, "GenerateAbilityEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Events.GenerateAbilityEvent e = (Events.GenerateAbilityEvent) event;
                    e.blob.desc = String.format("Attack that deals more damage when on a forest");
                    // TODO implement me
                    return SideEffect.none;
                });

        // Running Through Nature
        final String ability_running_through_nature = "Running Through Nature";
        events.ability.addEventHandler(ability_running_through_nature, "GenerateAbilityEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Events.GenerateAbilityEvent e = (Events.GenerateAbilityEvent) event;
                    e.blob.desc = String.format("This unit is faster on passive buildings");
                    return SideEffect.none;
                });
        events.ability.addEventHandler(ability_running_through_nature, "UnitMoveDistanceEvent",
                (GameView view, Ability receiver, Event event) -> {
                    UnitMoveDistanceEvent e = (UnitMoveDistanceEvent) event;
                    Optional<Building> b = view.game.world.getTile(receiver.wielder.getPoint())
                            .map((Tile t) -> t.building);
                    if (b.map((Building b) -> !b.isActive()).orElse(false)) {
                        e.distance++;
                    }
                });

        // Self Sacrifice
        final String ability_self_sacrifice = "Self Sacrifice";
        events.ability.addEventHandler(ability_self_sacrifice, "GenerateAbilityEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Events.GenerateAbilityEvent e = (Events.GenerateAbilityEvent) event;
                    e.blob.desc = String.format("Transfers all their health but 1 to the target unit");
                    return SideEffect.none;
                });
        events.ability.addEventHandler(ability_self_sacrifice, "AbilityActivatedEvent",
                (GameView view, Ability receiver, Event event) -> {
                    final int hitPoints = receiver.wielder.combat.hitPoints.get() - 1;
                    return SideEffect.all(AbilityLogic.healUnit(view, receiver.wielder, hitpoints),
                            () -> receiver.combat.health.set(1));
                });

        // Sacred Seeds
        final String ability_sacred_seeds = "Sacred Seeds";
        events.ability.addEventHandler(ability_sacred_seeds, "GenerateAbilityEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Events.GenerateAbilityEvent e = (Events.GenerateAbilityEvent) event;
                    e.blob.desc = String.format("Harvests seeds from meadows that can be consumed to generate favor");
                    // TODO implement me
                    return SideEffect.none;
                });

        // Shell Defense
        final String ability_shell_defense = "Shell Defense";
        events.ability.addEventHandler(ability_shell_defense, "GenerateAbilityEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Events.GenerateAbilityEvent e = (Events.GenerateAbilityEvent) event;
                    e.blob.desc = String.format("Extra defense");
                    return SideEffect.none;
                });
        events.ability.addEventHandler(ability_shell_defense, "TakeDamageEvent",
                (GameView view, Ability receiver, Event event) -> AbilityLogic.defense(event, 2));

        // Slime Shot
        final String ability_slime_shot = "Slime Shot";
        events.ability.addEventHandler(ability_slime_shot, "GenerateAbilityEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Events.GenerateAbilityEvent e = (Events.GenerateAbilityEvent) event;
                    e.blob.desc = String.format("Ranged attack");
                    return SideEffect.none;
                });
        events.ability.addEventHandler(ability_slime_shot, "AbilityActivatedEvent", (GameView view, Ability receiver,
                Event event) -> AbilityLogic.attack(view, receiver.wielder, new Damage(4), 3));

        // Smash
        final String ability_smash = "Smash";
        events.ability.addEventHandler(ability_smash, "GenerateAbilityEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Events.GenerateAbilityEvent e = (Events.GenerateAbilityEvent) event;
                    e.blob.desc = String.format("Attack with a chance to stun");
                    return SideEffect.none;
                });
        events.ability.addEventHandler(ability_smash, "AbilityActivatedEvent",
                (GameView view, Ability receiver, Event event) -> AbilityLogic.attackAndEffect(view, receiver.wielder,
                        new Damage(5), 1, Optional.of((Point p) -> {
                            Optional<Unit> u = view.game.world.getTile(p).flatMap((Tile t) -> t.unit);
                            if (u.isPresent()) {
                                return u.get().addStatusEffect(view, status_effect_stun);
                            }
                        })));

        // Stone Defense
        final String ability_stone_defense = "Stone Defense";
        events.ability.addEventHandler(ability_stone_defense, "GenerateAbilityEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Events.GenerateAbilityEvent e = (Events.GenerateAbilityEvent) event;
                    e.blob.desc = String.format("Extra defense");
                    return SideEffect.none;
                });
        events.ability.addEventHandler(ability_stone_defense, "TakeDamageEvent",
                (GameView view, Ability receiver, Event event) -> AbilityLogic.defense(event, 2));

        // Subterranean Potions
        final String ability_subterranean_potions = "Subterranean Potions";
        events.ability.addEventHandler(ability_subterranean_potions, "GenerateAbilityEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Events.GenerateAbilityEvent e = (Events.GenerateAbilityEvent) event;
                    e.blob.desc = String.format("Generates Health Potions from Mines");
                    return SideEffect.none;
                });
        events.ability.addEventHandler(ability_subterranean_potions, "SpawnEvent",
                (GameView view, Ability receiver, Event event) -> {
                    view.game.mechanics.turns.addFutureTick("TickEvent", receiver, 4, true);
                    return SideEffect.none;
                });
        events.ability.addEventHandler(ability_subterranean_potions, "TickEvent",
                (GameView view, Ability receiver, Event event) -> AbilityLogic.harvest(view, receiver.wielder,
                        item_health_potion, (Building b) -> b.name.equals("Mine")));

        // Swim
        final String ability_swim = "Swim";
        events.ability.addEventHandler(ability_swim, "GenerateAbilityEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Events.GenerateAbilityEvent e = (Events.GenerateAbilityEvent) event;
                    e.blob.desc = "This unit can swim on water tiles";
                    return SideEffect.none;
                });
        events.ability.addEventHandler(ability_swim, "CanUnitMoveEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Events.CanUnitMoveEvent e = (Events.CanUnitMoveEvent) event;
                    if (!e.canWalkOnTile && e.tile.name.equals("Water")) {
                        e.canWalkOnTile = true;
                    }
                    return SideEffect.none;
                });

        // Sword Slash
        final String ability_sword_slash = "Sword Slash";
        events.ability.addEventHandler(ability_sword_slash, "GenerateAbilityEvent",
                (GameView view, Ability receiver, Event event) -> {
                    Events.GenerateAbilityEvent e = (Events.GenerateAbilityEvent) event;
                    Damage dmg = new Damage(5);
                    e.blob.desc = String.format("Deals %s", dmg);
                    return SideEffect.none;
                });
        events.ability.addEventHandler(ability_sword_slash, "AbilityActivatedEvent", (GameView view, Ability receiver,
                Event event) -> AbilityLogic.attack(view, receiver.wielder, new Damage(5), 1));

        /**
         * SECTION 09 Units
         */

        // Knuckleheads
        // Gorax the Dragon Knight
        // Equinox
        // Elder Chumsa
        // Gemrock
        // Glittersnout
        // Sir Tlatec
        events.unit.addEventHandler("Sir Tlatec", "GenerateUnitEvent", (GameView view, Unit receiver, Event event) -> {
            Events.GenerateUnitEvent e = (Events.GenerateUnitEvent) event;
            e.blob.setModelInstance(view.av, "axolotl");
            e.blob.desc = "Tlatec the Axolotl-man has travelled far from his home in search of worthy opponents";
            e.blob.setActiveAbilities(view.game.generator, Optional.of(ability_sword_slash), Optional.empty());
            e.blob.setPassiveAbilities(view.game.generator, ability_swim, ability_hunt_fish, ability_plate_mail,
                    ability_regeneration);
            e.blob.glyphs.set(Glyph.BATTLE);
            e.blob.race = SALAMANDER;
            return SideEffect.none;
        });

        // Cenuok the Battle Grue
        // Beetlemoss
        events.unit.addEventHandler("Beetlemoss", "GenerateUnitEvent", (GameView view, Unit receiver, Event event) -> {
            Events.GenerateUnitEvent e = (Events.GenerateUnitEvent) event;
            e.blob.desc = "This nature spirit guards an ancient forest in Eaglehaven";
            e.blob.setModelInstance(view.av, "beetlemoss");
            e.blob.setActiveAbilities(view.game.generator, Optional.of(ability_fire_cannon),
                    Optional.of(ability_plant_forest));
            e.blob.setPassiveAbilities(view.game.generator, ability_pick_apples, ability_mine_gems);
            e.blob.glyphs.set(Glyph.BATTLE, Glyph.NATURE);
            e.blob.race = SPRITE;
            return SideEffect.none;
        });

        // Gloop the Adventurer
        events.unit.addEventHandler("Gloop the Adventurer", "GenerateUnitEvent",
                (GameView view, Unit receiver, Event event) -> {
                    Events.GenerateUnitEvent e = (Events.GenerateUnitEvent) event;
                    e.blob.setModelInstance(view.av, "gloop");
                    e.blob.desc = "This Plasmoid adventurer is eager to prove themself in the dungeons";
                    e.blob.setActiveAbilities(view.game.generator, Optional.of(ability_sword_slash),
                            Optional.of(ability_dungeon_delve));
                    e.blob.setPassiveAbilities(view.game.generator, ability_combat_loot, ability_night_vision,
                            ability_regeneration);
                    e.blob.glyphs.set(Glyph.BATTLE, Glyph.MINING);
                    e.blob.combat.health.setMax(40);
                    e.blob.haul.setMax(12);
                    e.blob.race = PLASMOID;
                    return SideEffect.none;
                });

        // Dominus the Lich
        // Graymaw
        // Roseris Thorn-hoof
        // Nitu Sodfoot
        // Nebaneba
        // Kamiena
        // Faustus
        // Maekuro the Mighty
        // Garudee
        // Pebbles
        // Magdalena
        // Lost Golem
        // Samara
        // Golem of the Grotto
        events.unit.addEventHandler("Golem of the Grotto", "GenerateUnitEvent",
                (GameView view, Unit receiver, Event event) -> {
                    Events.GenerateUnitEvent e = (Events.GenerateUnitEvent) event;
                    e.blob.desc = "This golem wanders the rocky peaks where it was forged long ago";
                    e.blob.setModelInstance(view.av, "golem-grotto");
                    e.blob.setActiveAbilities(view.game.generator, Optional.of(ability_smash),
                            Optional.of(ability_plant_meadow));
                    e.blob.setPassiveAbilities(view.game.generator, ability_mountain_strider, ability_local_defender);
                    e.blob.glyphs.set(Glyph.DEFENSE, Glyph.NATURE);
                    e.blob.combat.health.setMax(80);
                    e.blob.race = GOLEM;
                    return SideEffect.none;
                });

        // Puffshroom
        // Lord Tyson
        // Courrier Grog
        // Nizhaad Windwalker
        // Condylure of the Star Nose
        events.unit.addEventHandler("Condylure of the Star Nose", "GenerateUnitEvent",
                (GameView view, Unit receiver, Event event) -> {
                    Events.GenerateUnitEvent e = (Events.GenerateUnitEvent) event;
                    e.blob.desc = "This Brownie is blind, but traverses the subterranean world with the aid of his nose";
                    e.blob.setModelInstance(view.av, "condylure");
                    e.blob.setActiveAbilities(view.game.generator, Optional.of(ability_build_healing_fountain),
                            Optional.of(ability_dig_mine));
                    e.blob.setPassiveAbilities(view.game.generator, ability_night_vision, ability_mine_gems);
                    e.blob.glyphs.set(Glyph.HEALING, Glyph.MINING);
                    e.blob.race = BROWNIE;
                    return SideEffect.none;
                });

        // Huiying the Alchemist
        // Lady Daumia
        events.unit.addEventHandler("Lady Daumia", "GenerateUnitEvent", (GameView view, Unit receiver, Event event) -> {
            Events.GenerateUnitEvent e = (Events.GenerateUnitEvent) event;
            e.blob.desc = "Elven high missionary to Surgarde";
            e.blob.setModelInstance(view.av, "daumia");
            e.blob.setActiveAbilities(view.game.generator, Optional.of(ability_heal_wounds),
                    Optional.of(ability_self_sacrifice));
            e.blob.setPassiveAbilities(view.game.generator, ability_night_vision, ability_life_aura);
            e.blob.glyphs.set(Glyph.HEALING);
            e.blob.race = ELF;
            return SideEffect.none;
        });

        // Zen Hito the Kappa
        // Gibrax the Everlasting
        // Passiflor
        // Frogger the Gnome
        events.unit.addEventHandler("Frogger the Gnome", "GenerateUnitEvent",
                (GameView view, Unit receiver, Event event) -> {
                    Events.GenerateUnitEvent e = (Events.GenerateUnitEvent) event;
                    e.blob.desc = "Just a little Gnome and his frog";
                    e.blob.setModelInstance(view.av, "frog-gnome");
                    e.blob.setActiveAbilities(view.game.generator, Optional.of(ability_heal_wounds),
                            Optional.of(ability_hungry_frog_magic));
                    e.blob.setPassiveAbilities(view.game.generator, ability_pick_flowers, ability_swim);
                    e.blob.glyphs.set(Glyph.HEALING);
                    e.blob.haul.setMax(12);
                    e.blob.race = GNOME;
                    return SideEffect.none;
                });

        // Teragalor
        // Stalagmus
        events.unit.addEventHandler("Stalagmus", "GenerateUnitEvent", (GameView view, Unit receiver, Event event) -> {
            Events.GenerateUnitEvent e = (Events.GenerateUnitEvent) event;
            e.blob.desc = "Enchanted waters accumulate into this Golem's bowl-shaped body";
            e.blob.setModelInstance(view.av, "stalagmus");
            e.blob.setActiveAbilities(view.game.generator, Optional.of(ability_dig_mine),
                    Optional.of(ability_hurl_rock));
            e.blob.setPassiveAbilities(view.game.generator, ability_night_vision, ability_stone_defense,
                    ability_mine_gems, ability_mine_gold, ability_subterranean_potions);
            e.blob.glyphs.set(Glyph.MINING);
            e.blob.race = GOLEM;
            return SideEffect.none;
        });

        // Glimmer
        // Grizzlemane the Mycoweaver
        // Magicad
        // The Druid
        events.unit.addEventHandler("The Druid", "GenerateUnitEvent", (GameView view, Unit receiver, Event event) -> {
            Events.GenerateUnitEvent e = (Events.GenerateUnitEvent) event;
            e.blob.desc = "A mysterious Druid who rarely speaks";
            e.blob.setModelInstance(view.av, "druid");
            e.blob.setActiveAbilities(view.game.generator, Optional.of(ability_plant_forest),
                    Optional.of(ability_revenge_of_the_forest));
            e.blob.setPassiveAbilities(view.game.generator, ability_pick_apples, ability_night_vision,
                    ability_green_fortress);
            e.blob.glyphs.set(Glyph.NATURE);
            e.blob.visibleRadius = 4;
            e.blob.race = SPRITE;
            return SideEffect.none;
        });

        // Bluefeathers
        // Broker Quercia
        // Oystermane
        // Matilda the Merchant
        // Thoughtform
        // Akatash the Trader
        // Ansuagion the Gilded
        // Blorp the Burning
        events.unit.addEventHandler("Blorp the Burning", "GenerateUnitEvent",
                (GameView view, Unit receiver, Event event) -> {
                    Events.GenerateUnitEvent e = (Events.GenerateUnitEvent) event;
                    e.blob.desc = "A ravenous Plasmoid with an acidic body";
                    e.blob.setModelInstance(view.av, "blob");
                    e.blob.setActiveAbilities(view.game.generator, Optional.of(ability_slime_shot), Optional.empty());
                    e.blob.setPassiveAbilities(view.game.generator, ability_acid_skin, ability_liquifying_presence);
                    e.blob.glyphs.set(Glyph.BATTLE, Glyph.DEFENSE);
                    e.blob.combat.health.setMax(80);
                    e.blob.race = PLASMOID;
                    return SideEffect.none;
                });
        // Sathra the Flame Caster
        // Dendra Ivy
        // Trina the Ettin
        // Prismar
        events.unit.addEventHandler("Prismar", "GenerateUnitEvent", (GameView view, Unit receiver, Event event) -> {
            Events.GenerateUnitEvent e = (Events.GenerateUnitEvent) event;
            e.blob.setModelInstance(view.av, "crystal");
            e.blob.desc = "This Gemstone can focus light into powerful attacks";
            e.blob.setActiveAbilities(view.game.generator, Optional.of(ability_fire_laser),
                    Optional.of(ability_collapse_mine));
            e.blob.setPassiveAbilities(view.game.generator, ability_crystal_skin, ability_night_vision,
                    ability_mine_gems);
            e.blob.glyphs.set(Glyph.BATTLE, Glyph.MINING);
            e.blob.race = GEMSTONE;
            return SideEffect.none;
        });

        //
        // Ariala the Mage
        //
        //
        //
        // Halifax
        // Glub Glub
        // Galygos the Juggernaut
        // Defender Cuauhtli
        // Gilded Cho'chal
        // Soothing Gills
        // Pumpkin Boy
        events.unit.addEventHandler("Pumpkin Boy", "GenerateUnitEvent", (GameView view, Unit receiver, Event event) -> {
            Events.GenerateUnitEvent e = (Events.GenerateUnitEvent) event;
            e.blob.desc = "He doesn't say much, he's just a little guy";
            e.blob.setModelInstance(view.av, "pumpkin-boy");
            e.blob.setActiveAbilities(view.game.generator, Optional.of(ability_plant_meadow), Optional.of(ability_hug));
            e.blob.setPassiveAbilities(view.game.generator, ability_night_vision, ability_regeneration,
                    ability_running_through_nature, ability_sacred_seeds);
            e.blob.glyphs.set(Glyph.NATURE);
            e.blob.haul.setMax(12);
            e.blob.race = SPRITE;
            return SideEffect.none;
        });

        // Barometz
        events.unit.addEventHandler("Barometz", "GenerateUnitEvent", (GameView view, Unit receiver, Event event) -> {
            Events.GenerateUnitEvent e = (Events.GenerateUnitEvent) event;
            e.blob.desc = "This sheep-like Sprite blooms with delicious fruit";
            e.blob.setModelInstance(view.av, "barometz");
            e.blob.setActiveAbilities(view.game.generator, Optional.of(ability_bite), Optional.empty());
            e.blob.setPassiveAbilities(view.game.generator, ability_regeneration, ability_edible,
                    ability_deposit_seeds);
            e.blob.glyphs.set(Glyph.NATURE);
            e.blob.haul.setMax(12);
            e.blob.race = SPRITE;
            return SideEffect.none;
        });

        // Xella the Accursed
        // Svelta Luktegress
        // Al-Fikra
        events.unit.addEventHandler("Al-Fikra", "GenerateUnitEvent", (GameView view, Unit receiver, Event event) -> {
            Events.GenerateUnitEvent e = (Events.GenerateUnitEvent) event;
            e.blob.desc = "This being aids the great merchant kings of Eastern Bycidia";
            e.blob.setModelInstance(view.av, "alfikra");
            e.blob.setActiveAbilities(view.game.generator, Optional.empty(), Optional.empty());
            e.blob.setPassiveAbilities(view.game.generator, ability_regeneration, ability_market_indicator);
            e.blob.glyphs.set(Glyph.TRADE);
            e.blob.haul.setMax(12);
            e.blob.visibleRadius = 4;
            e.blob.race = TULPA;
            UnitLogic.speed(events, e.blob, 3);
            return SideEffect.none;
        });

        // Goldtooth
        // The Necromancer
        // Lurch
        // Garulax
        // Patagan
        // The Pumpkin King
        // Little Buck
        // Viraqa Under the Mountain
        // Castros Waterpaw
        // Champion Jenid
        // Badroch the Pack Grue
        // Guard Captain Sentrina
        // Barbs
        // Yalitza
        // Old Man Mosscloak
        //
        //
        // King Gargantos
        events.unit.addEventHandler("King Gargantos", "GenerateUnitEvent",
                (GameView view, Unit receiver, Event event) -> {
                    Events.GenerateUnitEvent e = (Events.GenerateUnitEvent) event;
                    e.blob.desc = "Warrior-king of the Tortoise Kingdom";
                    e.blob.setModelInstance(view.av, "gargantos");
                    e.blob.setActiveAbilities(view.game.generator, Optional.of(ability_smash),
                            Optional.of(ability_build_vault));
                    e.blob.setPassiveAbilities(view.game.generator, ability_shell_defense, ability_market_boom,
                            ability_swim);
                    e.blob.glyphs.set(Glyph.DEFENSE, Glyph.TRADE);
                    e.blob.combat.health.setMax(80);
                    e.blob.setTimeToHunger(view, 10);
                    e.blob.race = TORTUGAN;
                    return SideEffect.none;
                });

        // Sir Rootbeard
        // Wuraj the Blessed
        // Karina Brightfeather
        // Photali
        //
        // Razma
        // Theressa the Rover
        //
        // Illapa
        // Disastra
        // Chicao
        // Alaistar and Wurmdel
        // Mi'chalb Lightfoot

        /**
         * SECTION 10 Artifacts
         */

        // Cho's Sigil of Haste
        final String artifact_chos_sigil_of_haste = "Cho's Sigil of Haste";
        events.artifact.addEventHandler(artifact_chos_sigil_of_haste, "GenerateArtifactEvent",
                (GameView view, Artifact receiver, Event event) -> {
                    Events.GenerateArtifactEvent e = (Events.GenerateArtifactEvent) event;
                    e.blob.desc = "Your healing glyph units get +1 movement speed";
                    e.blob.image = Optional.of("golden feather");
                    return SideEffect.none;
                });
        events.artifact.addEventHandler(artifact_chos_sigil_of_haste, "ArtifactClaimedEvent",
                (GameView view, Artifact receiver, Event event) -> {
                    Events.ArtifactClaimedEvent e = (Events.ArtifactClaimedEvent) event;
                    view.game.events.signals.addListener("UnitMoveDistanceEvent", e.artifact);
                    return SideEffect.none;
                });
        events.artifact.addEventHandler(artifact_chos_sigil_of_haste, "UnitMoveDistanceEvent",
                (GameView view, Artifact receiver, Event event) -> {
                    Events.UnitMoveDistanceEvent e = (Events.UnitMoveDistanceEvent) event;
                    if (e.unit.getLeader().equals(receiver.getOwner()) && e.unit.glyphs.has(Glyph.HEALING)) {
                        e.distance++;
                    }
                    return SideEffect.none;
                });

        // Urdin's Scroll of Agility
        final String artifact_urdins_scroll_of_agility = "Urdin's Scroll of Agility";
        events.artifact.addEventHandler(artifact_urdins_scroll_of_agility, "GenerateArtifactEvent",
                (GameView view, Artifact receiver, Event event) -> {
                    Events.GenerateArtifactEvent e = (Events.GenerateArtifactEvent) event;
                    e.blob.desc = "Your defense glyph units get +1 movement speed";
                    e.blob.image = Optional.of("golden feather");
                    return SideEffect.none;
                });
        events.artifact.addEventHandler(artifact_urdins_scroll_of_agility, "ArtifactClaimedEvent",
                (GameView view, Artifact receiver, Event event) -> {
                    Events.ArtifactClaimedEvent e = (Events.ArtifactClaimedEvent) event;
                    view.game.events.signals.addListener("UnitMoveDistanceEvent", e.artifact);
                    return SideEffect.none;
                });
        events.artifact.addEventHandler(artifact_urdins_scroll_of_agility, "UnitMoveDistanceEvent",
                (GameView view, Artifact receiver, Event event) -> {
                    Events.UnitMoveDistanceEvent e = (Events.UnitMoveDistanceEvent) event;
                    if (e.unit.getLeader().equals(receiver.getOwner()) && e.unit.glyphs.has(Glyph.DEFENSE)) {
                        e.distance++;
                    }
                    return SideEffect.none;
                });

        // Sword of Aesethos
        events.artifact.addEventHandler("Sword of Aesethos", "GenerateArtifactEvent",
                (GameView view, Artifact receiver, Event event) -> {
                    Events.GenerateArtifactEvent e = (Events.GenerateArtifactEvent) event;
                    e.blob.desc = "Your units have additional critical hit chance";
                    e.blob.image = Optional.of("golden feather");
                    return SideEffect.none;
                });

        // Kauna's Amulet
        events.artifact.addEventHandler("Kauna's Amulet", "GenerateArtifactEvent",
                (GameView view, Artifact receiver, Event event) -> {
                    Events.GenerateArtifactEvent e = (Events.GenerateArtifactEvent) event;
                    e.blob.desc = "Your units within a patron's domain have extra defense";
                    e.blob.image = Optional.of("golden feather");
                    return SideEffect.none;
                });

        // Staff of Wurmdel
        events.artifact.addEventHandler("Staff of Wurmdel", "GenerateArtifactEvent",
                (GameView view, Artifact receiver, Event event) -> {
                    Events.GenerateArtifactEvent e = (Events.GenerateArtifactEvent) event;
                    e.blob.desc = "Your healing spells restore more health";
                    e.blob.image = Optional.of("golden feather");
                    return SideEffect.none;
                });

        // Tome of Morun
        events.artifact.addEventHandler("Tome of Morun", "GenerateArtifactEvent",
                (GameView view, Artifact receiver, Event event) -> {
                    Events.GenerateArtifactEvent e = (Events.GenerateArtifactEvent) event;
                    e.blob.desc = "Chance to spawn a glyph under your unit when it kills an enemy";
                    e.blob.image = Optional.of("golden feather");
                    return SideEffect.none;
                });

        // Orb of Nerketo
        events.artifact.addEventHandler("Orb of Nerketo", "GenerateArtifactEvent",
                (GameView view, Artifact receiver, Event event) -> {
                    Events.GenerateArtifactEvent e = (Events.GenerateArtifactEvent) event;
                    e.blob.desc = "Your units have additional visibility";
                    e.blob.image = Optional.of("golden feather");
                    return SideEffect.none;
                });

        // Shada's Flute
        events.artifact.addEventHandler("Shada's Flute", "GenerateArtifactEvent",
                (GameView view, Artifact receiver, Event event) -> {
                    Events.GenerateArtifactEvent e = (Events.GenerateArtifactEvent) event;
                    e.blob.desc = "Your patrons generate unit points";
                    e.blob.image = Optional.of("golden feather");
                    return SideEffect.none;
                });

        // Stones of Thudin
        events.artifact.addEventHandler("Stones of Thudin", "GenerateArtifactEvent",
                (GameView view, Artifact receiver, Event event) -> {
                    Events.GenerateArtifactEvent e = (Events.GenerateArtifactEvent) event;
                    e.blob.desc = "Your vaults take less damage";
                    e.blob.image = Optional.of("golden feather");
                    return SideEffect.none;
                });

        // The Chasi Bones
        events.artifact.addEventHandler("The Chasi Bones", "GenerateArtifactEvent",
                (GameView view, Artifact receiver, Event event) -> {
                    Events.GenerateArtifactEvent e = (Events.GenerateArtifactEvent) event;
                    e.blob.desc = "Your nature glyph units have a chance to harvest an additional item";
                    e.blob.image = Optional.of("golden feather");
                    return SideEffect.none;
                });

        // Ucha's Bowl of Plenty
        final String artifact_uchas_bowl_of_plenty = "Ucha's Bowl of Plenty";
        events.artifact.addEventHandler(artifact_uchas_bowl_of_plenty, "GenerateArtifactEvent",
                (GameView view, Artifact receiver, Event event) -> {
                    Events.GenerateArtifactEvent e = (Events.GenerateArtifactEvent) event;
                    e.blob.desc = "+1 option when selecting a new unit";
                    e.blob.image = Optional.of("golden feather");
                    e.blob.chips = 2;
                    return SideEffect.none;
                });
        events.artifact.addEventHandler(artifact_uchas_bowl_of_plenty, "ArtifactClaimedEvent",
                (GameView view, Artifact receiver, Event event) -> {
                    Events.ArtifactClaimedEvent e = (Events.ArtifactClaimedEvent) event;
                    e.player.numRecruitmentOptions++;
                    return SideEffect.none;
                });

        // Nerketo's Helm
        events.artifact.addEventHandler("Nerketo's Helm", "GenerateArtifactEvent",
                (GameView view, Artifact receiver, Event event) -> {
                    Events.GenerateArtifactEvent e = (Events.GenerateArtifactEvent) event;
                    e.blob.desc = "Critical hits against your units are less effective (e.g. 1.1x damage rather than 1.5x)";
                    e.blob.image = Optional.of("golden feather");
                    e.blob.chips = 2;
                    return SideEffect.none;
                });

        // Bounty of Ahn-June
        events.artifact.addEventHandler("Bounty of Ahn-June", "GenerateArtifactEvent",
                (GameView view, Artifact receiver, Event event) -> {
                    Events.GenerateArtifactEvent e = (Events.GenerateArtifactEvent) event;
                    e.blob.desc = "Trade glyph units on your vaults generate more auction points";
                    e.blob.image = Optional.of("golden feather");
                    e.blob.chips = 2;
                    return SideEffect.none;
                });

        // Mark of Kung
        events.artifact.addEventHandler("Mark of Kung", "GenerateArtifactEvent",
                (GameView view, Artifact receiver, Event event) -> {
                    Events.GenerateArtifactEvent e = (Events.GenerateArtifactEvent) event;
                    e.blob.desc = "Your battle glyph units get +1 movement speed";
                    e.blob.image = Optional.of("golden feather");
                    e.blob.chips = 2;
                    return SideEffect.none;
                });

        // Chalco's Seal of Protection
        events.artifact.addEventHandler("Chalco's Seal of Protection", "GenerateArtifactEvent",
                (GameView view, Artifact receiver, Event event) -> {
                    Events.GenerateArtifactEvent e = (Events.GenerateArtifactEvent) event;
                    e.blob.desc = "Your travel glyph units take less damage";
                    e.blob.image = Optional.of("golden feather");
                    e.blob.chips = 2;
                    return SideEffect.none;
                });

        // Poda's Elixir
        events.artifact.addEventHandler("Poda's Elixir", "GenerateArtifactEvent",
                (GameView view, Artifact receiver, Event event) -> {
                    Events.GenerateArtifactEvent e = (Events.GenerateArtifactEvent) event;
                    e.blob.desc = "Some chance to not spend the glyph when you recruit a unit";
                    e.blob.image = Optional.of("golden feather");
                    e.blob.chips = 2;
                    return SideEffect.none;
                });

        // Gaia's Effigy
        events.artifact.addEventHandler("Gaia's Effigy", "GenerateArtifactEvent",
                (GameView view, Artifact receiver, Event event) -> {
                    Events.GenerateArtifactEvent e = (Events.GenerateArtifactEvent) event;
                    e.blob.desc = "Extra unit points each turn";
                    e.blob.image = Optional.of("golden feather");
                    e.blob.chips = 3;
                    return SideEffect.none;
                });

        // Rod of Adelon
        events.artifact.addEventHandler("Rod of Adelon", "GenerateArtifactEvent",
                (GameView view, Artifact receiver, Event event) -> {
                    Events.GenerateArtifactEvent e = (Events.GenerateArtifactEvent) event;
                    e.blob.desc = "Chance to immediately recruit an enemy unit when you kill it";
                    e.blob.image = Optional.of("golden feather");
                    e.blob.chips = 3;
                    return SideEffect.none;
                });

        // Blade of Sanguinor
        events.artifact.addEventHandler("Blade of Sanguinor", "GenerateArtifactEvent",
                (GameView view, Artifact receiver, Event event) -> {
                    Events.GenerateArtifactEvent e = (Events.GenerateArtifactEvent) event;
                    e.blob.desc = "Your battle glyph units deal extra damage";
                    e.blob.image = Optional.of("golden feather");
                    e.blob.chips = 3;
                    return SideEffect.none;
                });

        // Cask of Amontior
        events.artifact.addEventHandler("Cask of Amontior", "GenerateArtifactEvent",
                (GameView view, Artifact receiver, Event event) -> {
                    Events.GenerateArtifactEvent e = (Events.GenerateArtifactEvent) event;
                    e.blob.desc = "Unoccupied tiles under your control also provide favor in a patron's domain";
                    e.blob.image = Optional.of("golden feather");
                    e.blob.chips = 3;
                    return SideEffect.none;
                });

        /**
         * SECTION 11 Fates
         */

        // The Raider
        events.fate.addEventHandler("The Raider", "GenerateFateEvent", (GameView view, Fate receiver, Event event) -> {
            Events.GenerateFateEvent e = (Events.GenerateFateEvent) event;
            e.blob.image = Optional.of("raider");
            e.blob.desc.add("Playstyle: High-risk aggro");
            return SideEffect.none;
        });

        // The Merchant
        events.fate.addEventHandler("The Merchant", "GenerateFateEvent",
                (GameView view, Fate receiver, Event event) -> {
                    Events.GenerateFateEvent e = (Events.GenerateFateEvent) event;
                    e.blob.image = Optional.of("merchant");
                    e.blob.desc.add("Playstyle: Market control");
                    return SideEffect.none;
                });

        // The Veteran
        events.fate.addEventHandler("The Veteran", "GenerateFateEvent", (GameView view, Fate receiver, Event event) -> {
            Events.GenerateFateEvent e = (Events.GenerateFateEvent) event;
            e.blob.image = Optional.of("veteran");
            e.blob.desc.add("Playstyle: Military production");
            return SideEffect.none;
        });

        // The Devout
        events.fate.addEventHandler("The Devout", "GenerateFateEvent", (GameView view, Fate receiver, Event event) -> {
            Events.GenerateFateEvent e = (Events.GenerateFateEvent) event;
            e.blob.image = Optional.of("devout");
            e.blob.desc.add("Playstyle: Patron collection");
            return SideEffect.none;
        });

        // The Sentinel
        events.fate.addEventHandler("The Sentinel", "GenerateFateEvent",
                (GameView view, Fate receiver, Event event) -> {
                    Events.GenerateFateEvent e = (Events.GenerateFateEvent) event;
                    e.blob.image = Optional.of("sentinel");
                    e.blob.desc.add("Playstyle: Defensive expansion");
                    return SideEffect.none;
                });

        // The Usurper
        events.fate.addEventHandler("The Usurper", "GenerateFateEvent", (GameView view, Fate receiver, Event event) -> {
            Events.GenerateFateEvent e = (Events.GenerateFateEvent) event;
            e.blob.image = Optional.of("usurper");
            e.blob.desc.add("Playstyle: Early market bonus into unit production");
            return SideEffect.none;
        });

        // The Forager
        events.fate.addEventHandler("The Forager", "GenerateFateEvent", (GameView view, Fate receiver, Event event) -> {
            Events.GenerateFateEvent e = (Events.GenerateFateEvent) event;
            e.blob.image = Optional.of("forager");
            e.blob.desc.add("Playstyle: Resource accumulation");
            return SideEffect.none;
        });
    }
}
