package net.lugocorp.kingdom.content.vanilla;
import net.lugocorp.kingdom.builtin.Events;
import net.lugocorp.kingdom.builtin.logic.AbilityLogic;
import net.lugocorp.kingdom.builtin.logic.ItemLogic;
import net.lugocorp.kingdom.content.Defs;
import net.lugocorp.kingdom.content.Labels;
import net.lugocorp.kingdom.game.model.Item;
import net.lugocorp.kingdom.game.model.Tile;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.game.player.Player;
import net.lugocorp.kingdom.game.properties.Rarity;
import net.lugocorp.kingdom.gameplay.events.AllEventHandlers;
import net.lugocorp.kingdom.gameplay.events.Stratified;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.SideEffect;
import java.util.Optional;

/**
 * SECTION Items
 */
class VanillaModItems {

    /**
     * Registers all Items for the Vanilla mod
     */
    static void registerEvents(AllEventHandlers events) {
        // Goo
        new Stratified<Item>(events.item, Labels.item_goo)
                .add(Events.GenerateItemEvent.class, (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "Consume to squish the goo";
                    e.blob.icon = Optional.of(Labels.asset_slime);
                    e.blob.gold = 1;
                    e.blob.tags.add(Labels.tag_goo);
                    return new SideEffect();
                }).add(Events.ItemConsumedEvent.class,
                        (GameView view, Item receiver, Events.ItemConsumedEvent e) -> new SideEffect());

        // Slime Armor
        new Stratified<Item>(events.item, Labels.item_slime_armor)
                .add(Events.GenerateItemEvent.class, (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "+2 defense";
                    e.blob.icon = Optional.of(Labels.asset_chestplate);
                    e.blob.gold = 1;
                    return new SideEffect();
                }).add(Events.TakeDamageEvent.class, (GameView view, Item receiver, Events.TakeDamageEvent e) -> {
                    e.dmg.base -= 2;
                    return new SideEffect();
                });

        // Golden Spear
        new Stratified<Item>(events.item, Labels.item_golden_spear)
                .add(Events.GenerateItemEvent.class, (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "+1 damage";
                    e.blob.icon = Optional.of(Labels.asset_spear);
                    e.blob.gold = 10;
                    return new SideEffect();
                }).add(Events.AttackEvent.class, (GameView view, Item receiver, Events.AttackEvent e) -> {
                    e.dmg.base += 2;
                    return new SideEffect();
                });

        // Mushroom
        new Stratified<Item>(events.item, Labels.item_mushroom)
                .add(Events.GenerateItemEvent.class, (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "Consume to look at this mushroom";
                    e.blob.icon = Optional.of(Labels.asset_mushroom);
                    e.blob.gold = 1;
                    e.blob.tags.add(Labels.tag_mushroom);
                    e.blob.tags.add(Labels.tag_natural);
                    return new SideEffect();
                }).add(Events.ItemConsumedEvent.class,
                        (GameView view, Item receiver, Events.ItemConsumedEvent e) -> new SideEffect());

        // Sacred seed
        new Stratified<Item>(events.item, Labels.item_sacred_seed)
                .add(Events.GenerateItemEvent.class, (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "Consume to generate extra favor";
                    e.blob.icon = Optional.of(Labels.asset_seeds);
                    e.blob.gold = 1;
                    e.blob.tags.add(Labels.tag_natural);
                    return new SideEffect();
                }).add(Events.ItemConsumedEvent.class, (GameView view, Item receiver, Events.ItemConsumedEvent e) -> {
                    return e.consumer.abilities.addStatusEffect(view, Labels.status_effect_more_favor);
                });

        // Flower
        new Stratified<Item>(events.item, Labels.item_flower)
                .add(Events.GenerateItemEvent.class, (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "Consume to smell a sweet flower";
                    e.blob.icon = Optional.of(Labels.asset_flower);
                    e.blob.gold = 1;
                    e.blob.tags.add(Labels.tag_natural);
                    return new SideEffect();
                }).add(Events.ItemConsumedEvent.class,
                        (GameView view, Item receiver, Events.ItemConsumedEvent e) -> new SideEffect());

        // Fish
        new Stratified<Item>(events.item, Labels.item_fish)
                .add(Events.GenerateItemEvent.class, (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "Consume to stave off hunger";
                    e.blob.icon = Optional.of(Labels.asset_fish);
                    e.blob.gold = 1;
                    e.blob.tags.add(Labels.tag_meat);
                    return new SideEffect();
                }).add(Events.ItemConsumedEvent.class,
                        (GameView view, Item receiver, Events.ItemConsumedEvent e) -> ItemLogic.food(view, e));

        // Gold Coin
        new Stratified<Item>(events.item, Labels.item_gold_coin)
                .add(Events.GenerateItemEvent.class, (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "Consume to increase your gold";
                    e.blob.icon = Optional.of(Labels.asset_coin);
                    e.blob.gold = 1;
                    return new SideEffect();
                }).add(Events.ItemConsumedEvent.class,
                        (GameView view, Item receiver, Events.ItemConsumedEvent e) -> ItemLogic.valuable(view, e));

        // Emerald
        new Stratified<Item>(events.item, Labels.item_emerald)
                .add(Events.GenerateItemEvent.class, (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "Consume to increase your gold";
                    e.blob.icon = Optional.of(Labels.asset_crystal);
                    e.blob.gold = 10;
                    e.blob.tags.add(Labels.tag_gem);
                    return new SideEffect();
                }).add(Events.ItemConsumedEvent.class,
                        (GameView view, Item receiver, Events.ItemConsumedEvent e) -> ItemLogic.valuable(view, e));

        // Apple
        new Stratified<Item>(events.item, Labels.item_apple)
                .add(Events.GenerateItemEvent.class, (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "Consume to stave off hunger";
                    e.blob.icon = Optional.of(Labels.asset_apple);
                    e.blob.gold = 1;
                    e.blob.tags.add(Labels.tag_fruit);
                    e.blob.tags.add(Labels.tag_natural);
                    return new SideEffect();
                }).add(Events.ItemConsumedEvent.class,
                        (GameView view, Item receiver, Events.ItemConsumedEvent e) -> ItemLogic.food(view, e));

        // Health Potion
        new Stratified<Item>(events.item, Labels.item_health_potion)
                .add(Events.GenerateItemEvent.class, (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "Consume to heal by 5 hit points";
                    e.blob.icon = Optional.of(Labels.asset_potion);
                    e.blob.gold = 1;
                    return new SideEffect();
                }).add(Events.ItemConsumedEvent.class,
                        (GameView view, Item receiver, Events.ItemConsumedEvent e) -> ItemLogic.potion(view, e, 5));

        // Incense
        // Sack of Gold
        new Stratified<Item>(events.item, Labels.item_sack_of_gold)
                .add(Events.GenerateItemEvent.class, (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "Consume to generate 10 gold";
                    e.blob.icon = Optional.of(Labels.asset_pouch);
                    e.blob.gold = 10;
                    return new SideEffect();
                }).add(Events.ItemConsumedEvent.class,
                        (GameView view, Item receiver, Events.ItemConsumedEvent e) -> new SideEffect().add(() -> {
                            e.consumer.getLeader().ifPresent((Player p) -> p.gold += 10);
                            view.hud.top.update(view.game);
                        }));

        // Capital
        new Stratified<Item>(events.item, Labels.item_capital)
                .add(Events.GenerateItemEvent.class, (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "Consume to generate 6 auction points";
                    e.blob.icon = Optional.of(Labels.asset_paper);
                    e.blob.gold = 6;
                    return new SideEffect();
                }).add(Events.ItemConsumedEvent.class, (GameView view, Item receiver,
                        Events.ItemConsumedEvent e) -> AbilityLogic.generateAuctionPoints(view, e.consumer, 10));

        // Shellcap Armor
        // Stones
        // Sword
        new Stratified<Item>(events.item, Labels.item_sword)
                .add(Events.GenerateItemEvent.class, (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "+1 damage";
                    e.blob.icon = Optional.of(Labels.asset_sword);
                    e.blob.gold = 1;
                    return new SideEffect();
                }).add(Events.AttackEvent.class, (GameView view, Item receiver, Events.AttackEvent e) -> {
                    ItemLogic.boostDamage(e, 1, true);
                    return new SideEffect();
                });

        // Shield
        new Stratified<Item>(events.item, Labels.item_shield)
                .add(Events.GenerateItemEvent.class, (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "+1 armor";
                    e.blob.icon = Optional.of(Labels.asset_shield);
                    e.blob.gold = 1;
                    return new SideEffect();
                }).add(Events.TakeDamageEvent.class, (GameView view, Item receiver, Events.TakeDamageEvent e) -> {
                    ItemLogic.boostArmor(e, 1, true);
                    return new SideEffect();
                });

        // Staff
        new Stratified<Item>(events.item, Labels.item_staff)
                .add(Events.GenerateItemEvent.class, (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "+1 healing";
                    e.blob.icon = Optional.of(Labels.asset_staff);
                    e.blob.gold = 1;
                    return new SideEffect();
                }).add(Events.HealEntityEvent.class, (GameView view, Item receiver, Events.HealEntityEvent e) -> {
                    ItemLogic.boostHealing(e, 1, true);
                    return new SideEffect();
                });

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
        new Stratified<Item>(events.item, Labels.item_acidic_solute)
                .add(Events.GenerateItemEvent.class, (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "+2 damage for Plasmoids";
                    e.blob.icon = Optional.of(Labels.asset_slime);
                    e.blob.gold = 3;
                    return new SideEffect();
                }).add(Events.AttackEvent.class, (GameView view, Item receiver, Events.AttackEvent e) -> {
                    ItemLogic.boostDamage(e, 2, ((Unit) e.attacker).species.counts(Defs.species_plasmoid));
                    return new SideEffect();
                });

        // Binding Solute
        new Stratified<Item>(events.item, Labels.item_binding_solute)
                .add(Events.GenerateItemEvent.class, (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "+2 armor for Plasmoids";
                    e.blob.icon = Optional.of(Labels.asset_slime);
                    e.blob.gold = 3;
                    return new SideEffect();
                }).add(Events.TakeDamageEvent.class, (GameView view, Item receiver, Events.TakeDamageEvent e) -> {
                    ItemLogic.boostArmor(e, 2, ((Unit) e.target).species.counts(Defs.species_plasmoid));
                    return new SideEffect();
                });

        // Life-Giving Solute
        new Stratified<Item>(events.item, Labels.item_life_giving_solute)
                .add(Events.GenerateItemEvent.class, (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "+2 healing for Plasmoids";
                    e.blob.icon = Optional.of(Labels.asset_slime);
                    e.blob.gold = 3;
                    return new SideEffect();
                }).add(Events.HealEntityEvent.class, (GameView view, Item receiver, Events.HealEntityEvent e) -> {
                    ItemLogic.boostHealing(e, 2, ((Unit) e.healer).species.counts(Defs.species_plasmoid));
                    return new SideEffect();
                });

        // Blessed Solute
        // Feather of Bravery
        new Stratified<Item>(events.item, Labels.item_feather_of_bravery)
                .add(Events.GenerateItemEvent.class, (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "+2 damage for Garudas";
                    e.blob.icon = Optional.of(Labels.asset_feather);
                    e.blob.gold = 3;
                    return new SideEffect();
                }).add(Events.AttackEvent.class, (GameView view, Item receiver, Events.AttackEvent e) -> {
                    ItemLogic.boostDamage(e, 2, ((Unit) e.attacker).species.counts(Defs.species_garuda));
                    return new SideEffect();
                });

        // Iron Beak Brace
        new Stratified<Item>(events.item, Labels.item_iron_beak_brace)
                .add(Events.GenerateItemEvent.class, (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "+2 armor for Garudas";
                    e.blob.icon = Optional.of(Labels.asset_helmet);
                    e.blob.gold = 3;
                    return new SideEffect();
                }).add(Events.TakeDamageEvent.class, (GameView view, Item receiver, Events.TakeDamageEvent e) -> {
                    ItemLogic.boostArmor(e, 2, ((Unit) e.target).species.counts(Defs.species_garuda));
                    return new SideEffect();
                });

        // Hollow Bone Rattle
        new Stratified<Item>(events.item, Labels.item_hollow_bone_rattle)
                .add(Events.GenerateItemEvent.class, (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "+2 healing for Garudas";
                    e.blob.icon = Optional.of(Labels.asset_rattle);
                    e.blob.gold = 3;
                    return new SideEffect();
                }).add(Events.HealEntityEvent.class, (GameView view, Item receiver, Events.HealEntityEvent e) -> {
                    ItemLogic.boostHealing(e, 2, ((Unit) e.healer).species.counts(Defs.species_garuda));
                    return new SideEffect();
                });

        // Bag of Shiny Pebbles
        // Thorny Rose Staff
        new Stratified<Item>(events.item, Labels.item_thorny_rose_staff)
                .add(Events.GenerateItemEvent.class, (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "+2 damage for Sprites";
                    e.blob.icon = Optional.of(Labels.asset_staff);
                    e.blob.gold = 3;
                    return new SideEffect();
                }).add(Events.AttackEvent.class, (GameView view, Item receiver, Events.AttackEvent e) -> {
                    ItemLogic.boostDamage(e, 2, ((Unit) e.attacker).species.counts(Defs.species_sprite));
                    return new SideEffect();
                });

        // Overgrown Shield
        new Stratified<Item>(events.item, Labels.item_overgrown_shield)
                .add(Events.GenerateItemEvent.class, (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "+2 armor for Sprites";
                    e.blob.icon = Optional.of(Labels.asset_shield);
                    e.blob.gold = 3;
                    return new SideEffect();
                }).add(Events.TakeDamageEvent.class, (GameView view, Item receiver, Events.TakeDamageEvent e) -> {
                    ItemLogic.boostArmor(e, 2, ((Unit) e.target).species.counts(Defs.species_sprite));
                    return new SideEffect();
                });

        // Sap of Unbreaking
        new Stratified<Item>(events.item, Labels.item_sap_of_unbreaking)
                .add(Events.GenerateItemEvent.class, (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "+2 healing for Sprites";
                    e.blob.icon = Optional.of(Labels.asset_slime);
                    e.blob.gold = 3;
                    return new SideEffect();
                }).add(Events.HealEntityEvent.class, (GameView view, Item receiver, Events.HealEntityEvent e) -> {
                    ItemLogic.boostHealing(e, 2, ((Unit) e.healer).species.counts(Defs.species_sprite));
                    return new SideEffect();
                });

        // Sacred Pollen
        // Advanced Spear
        new Stratified<Item>(events.item, Labels.item_advanced_spear)
                .add(Events.GenerateItemEvent.class, (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "+2 damage for Elves";
                    e.blob.icon = Optional.of(Labels.asset_spear);
                    e.blob.gold = 3;
                    return new SideEffect();
                }).add(Events.AttackEvent.class, (GameView view, Item receiver, Events.AttackEvent e) -> {
                    ItemLogic.boostDamage(e, 2, ((Unit) e.attacker).species.counts(Defs.species_elf));
                    return new SideEffect();
                });

        // Sentinel's Shield
        new Stratified<Item>(events.item, Labels.item_sentinels_shield)
                .add(Events.GenerateItemEvent.class, (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "+2 armor for Elves";
                    e.blob.icon = Optional.of(Labels.asset_shield);
                    e.blob.gold = 3;
                    return new SideEffect();
                }).add(Events.TakeDamageEvent.class, (GameView view, Item receiver, Events.TakeDamageEvent e) -> {
                    ItemLogic.boostArmor(e, 2, ((Unit) e.target).species.counts(Defs.species_elf));
                    return new SideEffect();
                });

        // Healing Incantation
        new Stratified<Item>(events.item, Labels.item_healing_incantation)
                .add(Events.GenerateItemEvent.class, (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "+2 healing for Elves";
                    e.blob.icon = Optional.of(Labels.asset_paper);
                    e.blob.gold = 3;
                    return new SideEffect();
                }).add(Events.HealEntityEvent.class, (GameView view, Item receiver, Events.HealEntityEvent e) -> {
                    ItemLogic.boostHealing(e, 2, ((Unit) e.healer).species.counts(Defs.species_elf));
                    return new SideEffect();
                });

        // Devout Incantation
        // Great Hammer
        new Stratified<Item>(events.item, Labels.item_great_hammer)
                .add(Events.GenerateItemEvent.class, (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "+2 damage for Humans";
                    e.blob.icon = Optional.of(Labels.asset_hammer);
                    e.blob.gold = 3;
                    return new SideEffect();
                }).add(Events.AttackEvent.class, (GameView view, Item receiver, Events.AttackEvent e) -> {
                    ItemLogic.boostDamage(e, 2, ((Unit) e.attacker).species.counts(Defs.species_human));
                    return new SideEffect();
                });

        // Battle Armaments
        new Stratified<Item>(events.item, Labels.item_battle_armaments)
                .add(Events.GenerateItemEvent.class, (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "+2 armor for Humans";
                    e.blob.icon = Optional.of(Labels.asset_chestplate);
                    e.blob.gold = 3;
                    return new SideEffect();
                }).add(Events.TakeDamageEvent.class, (GameView view, Item receiver, Events.TakeDamageEvent e) -> {
                    ItemLogic.boostArmor(e, 2, ((Unit) e.target).species.counts(Defs.species_human));
                    return new SideEffect();
                });

        // Bandage Kit
        new Stratified<Item>(events.item, Labels.item_bandage_kit)
                .add(Events.GenerateItemEvent.class, (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "+2 healing for Humans";
                    e.blob.icon = Optional.of(Labels.asset_pouch);
                    e.blob.gold = 3;
                    return new SideEffect();
                }).add(Events.HealEntityEvent.class, (GameView view, Item receiver, Events.HealEntityEvent e) -> {
                    ItemLogic.boostHealing(e, 2, ((Unit) e.healer).species.counts(Defs.species_human));
                    return new SideEffect();
                });

        // Dearly Held Idol
        // Stoneshell Mace
        new Stratified<Item>(events.item, Labels.item_stoneshell_mace)
                .add(Events.GenerateItemEvent.class, (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "+2 damage for Tortugans";
                    e.blob.icon = Optional.of(Labels.asset_mace);
                    e.blob.gold = 3;
                    return new SideEffect();
                }).add(Events.AttackEvent.class, (GameView view, Item receiver, Events.AttackEvent e) -> {
                    ItemLogic.boostDamage(e, 2, ((Unit) e.attacker).species.counts(Defs.species_tortugan));
                    return new SideEffect();
                });

        // Shell Salve
        new Stratified<Item>(events.item, Labels.item_shell_salve)
                .add(Events.GenerateItemEvent.class, (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "+2 armor for Tortugans";
                    e.blob.icon = Optional.of(Labels.asset_potion);
                    e.blob.gold = 3;
                    return new SideEffect();
                }).add(Events.TakeDamageEvent.class, (GameView view, Item receiver, Events.TakeDamageEvent e) -> {
                    ItemLogic.boostArmor(e, 2, ((Unit) e.target).species.counts(Defs.species_tortugan));
                    return new SideEffect();
                });

        // Shell-Sealing Goo
        new Stratified<Item>(events.item, Labels.item_shell_sealing_goo)
                .add(Events.GenerateItemEvent.class, (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "+2 healing for Tortugans";
                    e.blob.icon = Optional.of(Labels.asset_slime);
                    e.blob.gold = 3;
                    return new SideEffect();
                }).add(Events.HealEntityEvent.class, (GameView view, Item receiver, Events.HealEntityEvent e) -> {
                    ItemLogic.boostHealing(e, 2, ((Unit) e.healer).species.counts(Defs.species_tortugan));
                    return new SideEffect();
                });

        // Sacred Shell Rattle
        // Blessed Charm
        // Bloody Totem
        // Phoenix Blossom
        // Sling and Stone
        // Life-Giving Elixir
        new Stratified<Item>(events.item, Labels.item_life_giving_elixir)
                .add(Events.GenerateItemEvent.class, (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "Consume to generate 6 unit points";
                    e.blob.icon = Optional.of(Labels.asset_potion);
                    e.blob.rarity = Rarity.UNCOMMON;
                    e.blob.gold = 6;
                    return new SideEffect();
                }).add(Events.ItemConsumedEvent.class,
                        (GameView view, Item receiver, Events.ItemConsumedEvent e) -> new SideEffect()
                                .add(() -> e.consumer.getLeader()
                                        .ifPresent((Player p) -> p.addUnitPoints(view, e.consumer.getPoint(), 10))));

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
        // Floral Seeds
        new Stratified<Item>(events.item, Labels.item_floral_seeds)
                .add(Events.GenerateItemEvent.class, (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "Consume to plant a meadow on a grass tile";
                    e.blob.icon = Optional.of(Labels.asset_seeds);
                    e.blob.rarity = Rarity.RARE;
                    e.blob.gold = 10;
                    e.blob.tags.add(Labels.tag_natural);
                    return new SideEffect();
                }).add(Events.ItemConsumedEvent.class,
                        (GameView view, Item receiver, Events.ItemConsumedEvent e) -> AbilityLogic.build(view,
                                e.consumer, Labels.building_meadow, (Tile t) -> t.name.equals(Labels.tile_grass)));

        // Arboreal Seeds
        new Stratified<Item>(events.item, Labels.item_arboreal_seeds)
                .add(Events.GenerateItemEvent.class, (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "Consume to plant a forest on a grass tile";
                    e.blob.icon = Optional.of(Labels.asset_seeds);
                    e.blob.rarity = Rarity.RARE;
                    e.blob.gold = 10;
                    e.blob.tags.add(Labels.tag_natural);
                    return new SideEffect();
                }).add(Events.ItemConsumedEvent.class,
                        (GameView view, Item receiver, Events.ItemConsumedEvent e) -> AbilityLogic.build(view,
                                e.consumer, Labels.building_forest, (Tile t) -> t.name.equals(Labels.tile_grass)));

        // Arctic Seeds
        new Stratified<Item>(events.item, Labels.item_arctic_seeds)
                .add(Events.GenerateItemEvent.class, (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "Consume to plant a taiga on a snow tile";
                    e.blob.icon = Optional.of(Labels.asset_seeds);
                    e.blob.rarity = Rarity.RARE;
                    e.blob.gold = 10;
                    e.blob.tags.add(Labels.tag_natural);
                    return new SideEffect();
                }).add(Events.ItemConsumedEvent.class,
                        (GameView view, Item receiver, Events.ItemConsumedEvent e) -> AbilityLogic.build(view,
                                e.consumer, Labels.building_taiga, (Tile t) -> t.name.equals(Labels.tile_snow)));

        // Cactus Seeds
        new Stratified<Item>(events.item, Labels.item_cactus_seeds)
                .add(Events.GenerateItemEvent.class, (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "Consume to plant a shrubland on a sand tile";
                    e.blob.icon = Optional.of(Labels.asset_seeds);
                    e.blob.rarity = Rarity.RARE;
                    e.blob.gold = 10;
                    e.blob.tags.add(Labels.tag_natural);
                    return new SideEffect();
                }).add(Events.ItemConsumedEvent.class,
                        (GameView view, Item receiver, Events.ItemConsumedEvent e) -> AbilityLogic.build(view,
                                e.consumer, Labels.building_shrubland, (Tile t) -> t.name.equals(Labels.tile_sand)));

        // Pioneering Seeds
        new Stratified<Item>(events.item, Labels.item_pioneering_seeds)
                .add(Events.GenerateItemEvent.class, (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "Consume to plant an oasis on a sand tile";
                    e.blob.icon = Optional.of(Labels.asset_seeds);
                    e.blob.rarity = Rarity.RARE;
                    e.blob.gold = 10;
                    e.blob.tags.add(Labels.tag_natural);
                    return new SideEffect();
                }).add(Events.ItemConsumedEvent.class,
                        (GameView view, Item receiver, Events.ItemConsumedEvent e) -> AbilityLogic.build(view,
                                e.consumer, Labels.building_oasis, (Tile t) -> t.name.equals(Labels.tile_sand)));

        // Digging Kit
        new Stratified<Item>(events.item, Labels.item_digging_kit)
                .add(Events.GenerateItemEvent.class, (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "Consume to dig a mine";
                    e.blob.icon = Optional.of(Labels.asset_shovel);
                    e.blob.rarity = Rarity.RARE;
                    e.blob.gold = 10;
                    return new SideEffect();
                }).add(Events.ItemConsumedEvent.class,
                        (GameView view, Item receiver, Events.ItemConsumedEvent e) -> AbilityLogic.build(view,
                                e.consumer, Labels.building_mine, (Tile t) -> true));

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
        new Stratified<Item>(events.item, Labels.item_thoughtform_sword)
                .add(Events.GenerateItemEvent.class, (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "+2 damage for Tulpas";
                    e.blob.icon = Optional.of(Labels.asset_sword);
                    e.blob.gold = 3;
                    return new SideEffect();
                }).add(Events.AttackEvent.class, (GameView view, Item receiver, Events.AttackEvent e) -> {
                    ItemLogic.boostDamage(e, 2, ((Unit) e.attacker).species.counts(Defs.species_tulpa));
                    return new SideEffect();
                });

        // Shield of the Subconscious
        new Stratified<Item>(events.item, Labels.item_shield_of_the_subconscious)
                .add(Events.GenerateItemEvent.class, (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "+2 armor for Tulpas";
                    e.blob.icon = Optional.of(Labels.asset_shield);
                    e.blob.gold = 3;
                    return new SideEffect();
                }).add(Events.TakeDamageEvent.class, (GameView view, Item receiver, Events.TakeDamageEvent e) -> {
                    ItemLogic.boostArmor(e, 2, ((Unit) e.target).species.counts(Defs.species_tulpa));
                    return new SideEffect();
                });

        // Staff of Inner Peace
        new Stratified<Item>(events.item, Labels.item_staff_of_inner_peace)
                .add(Events.GenerateItemEvent.class, (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "+2 healing for Tulpas";
                    e.blob.icon = Optional.of(Labels.asset_staff);
                    e.blob.gold = 3;
                    return new SideEffect();
                }).add(Events.HealEntityEvent.class, (GameView view, Item receiver, Events.HealEntityEvent e) -> {
                    ItemLogic.boostHealing(e, 2, ((Unit) e.healer).species.counts(Defs.species_tulpa));
                    return new SideEffect();
                });

        // Rod of Psychic Devotion
        // Tree Trunk Club
        new Stratified<Item>(events.item, Labels.item_tree_trunk_club)
                .add(Events.GenerateItemEvent.class, (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "+2 damage for Giants";
                    e.blob.icon = Optional.of(Labels.asset_club);
                    e.blob.gold = 3;
                    return new SideEffect();
                }).add(Events.AttackEvent.class, (GameView view, Item receiver, Events.AttackEvent e) -> {
                    ItemLogic.boostDamage(e, 2, ((Unit) e.attacker).species.counts(Defs.species_giant));
                    return new SideEffect();
                });

        // Castle Gate Aegis
        new Stratified<Item>(events.item, Labels.item_castle_gate_aegis)
                .add(Events.GenerateItemEvent.class, (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "+2 armor for Giants";
                    e.blob.icon = Optional.of(Labels.asset_shield);
                    e.blob.gold = 3;
                    return new SideEffect();
                }).add(Events.TakeDamageEvent.class, (GameView view, Item receiver, Events.TakeDamageEvent e) -> {
                    ItemLogic.boostArmor(e, 2, ((Unit) e.target).species.counts(Defs.species_giant));
                    return new SideEffect();
                });

        // Vase of Sacred Waters
        new Stratified<Item>(events.item, Labels.item_vase_of_sacred_waters)
                .add(Events.GenerateItemEvent.class, (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "+2 healing for Giants";
                    e.blob.icon = Optional.of(Labels.asset_vase);
                    e.blob.gold = 3;
                    return new SideEffect();
                }).add(Events.HealEntityEvent.class, (GameView view, Item receiver, Events.HealEntityEvent e) -> {
                    ItemLogic.boostHealing(e, 2, ((Unit) e.healer).species.counts(Defs.species_giant));
                    return new SideEffect();
                });

        // Amulet of the Progenitors
        // Sharpened Quartz
        new Stratified<Item>(events.item, Labels.item_sharpened_quartz)
                .add(Events.GenerateItemEvent.class, (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "+2 damage for Golems";
                    e.blob.icon = Optional.of(Labels.asset_crystal);
                    e.blob.gold = 3;
                    return new SideEffect();
                }).add(Events.AttackEvent.class, (GameView view, Item receiver, Events.AttackEvent e) -> {
                    ItemLogic.boostDamage(e, 2, ((Unit) e.attacker).species.counts(Defs.species_golem));
                    return new SideEffect();
                });

        // Igneous Armaments
        new Stratified<Item>(events.item, Labels.item_igneous_armaments)
                .add(Events.GenerateItemEvent.class, (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "+2 armor for Golems";
                    e.blob.icon = Optional.of(Labels.asset_chestplate);
                    e.blob.gold = 3;
                    return new SideEffect();
                }).add(Events.TakeDamageEvent.class, (GameView view, Item receiver, Events.TakeDamageEvent e) -> {
                    ItemLogic.boostArmor(e, 2, ((Unit) e.target).species.counts(Defs.species_golem));
                    return new SideEffect();
                });

        // Moss-covered Stone
        new Stratified<Item>(events.item, Labels.item_moss_covered_stone)
                .add(Events.GenerateItemEvent.class, (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "+2 healing for Golems";
                    e.blob.icon = Optional.of(Labels.asset_stone);
                    e.blob.gold = 3;
                    return new SideEffect();
                }).add(Events.HealEntityEvent.class, (GameView view, Item receiver, Events.HealEntityEvent e) -> {
                    ItemLogic.boostHealing(e, 2, ((Unit) e.healer).species.counts(Defs.species_golem));
                    return new SideEffect();
                });

        // Glyphic Geode
        // Self-Sustaining Soulstone
        // Hero's Call
    }
}
