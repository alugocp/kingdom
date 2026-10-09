package net.lugocorp.kingdom.content.vanilla;
import net.lugocorp.kingdom.builtin.Events;
import net.lugocorp.kingdom.builtin.logic.AbilityLogic;
import net.lugocorp.kingdom.builtin.logic.ItemLogic;
import net.lugocorp.kingdom.content.Labels;
import net.lugocorp.kingdom.game.model.Item;
import net.lugocorp.kingdom.game.model.Tile;
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
        // Gold Coin
        new Stratified<Item>(events.item, Labels.item_gold_coin)
                .add(Events.GenerateItemEvent.class, (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "Consume to increase your gold";
                    e.blob.icon = Optional.of(Labels.asset_coin);
                    e.blob.gold = 1;
                    return new SideEffect();
                }).add(ItemLogic.valuable());

        // Emerald
        new Stratified<Item>(events.item, Labels.item_emerald)
                .add(Events.GenerateItemEvent.class, (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "Consume to increase your gold";
                    e.blob.icon = Optional.of(Labels.asset_crystal);
                    e.blob.gold = 10;
                    e.blob.tags.add(Labels.tag_gem);
                    return new SideEffect();
                }).add(ItemLogic.valuable());

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

        // Cacao
        new Stratified<Item>(events.item, Labels.item_cacao)
                .add(Events.GenerateItemEvent.class, (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "Consume to stave off hunger";
                    e.blob.icon = Optional.of(Labels.asset_harvest_cacao);
                    e.blob.gold = 3;
                    e.blob.tags.add(Labels.tag_fruit);
                    e.blob.tags.add(Labels.tag_natural);
                    return new SideEffect();
                }).add(Events.ItemConsumedEvent.class,
                        (GameView view, Item receiver, Events.ItemConsumedEvent e) -> ItemLogic.food(view, e));

        // Mesquite
        new Stratified<Item>(events.item, Labels.item_mesquite)
                .add(Events.GenerateItemEvent.class, (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "Consume to stave off hunger";
                    e.blob.icon = Optional.of(Labels.asset_harvest_mesquite);
                    e.blob.gold = 1;
                    e.blob.tags.add(Labels.tag_fruit);
                    e.blob.tags.add(Labels.tag_natural);
                    return new SideEffect();
                }).add(Events.ItemConsumedEvent.class,
                        (GameView view, Item receiver, Events.ItemConsumedEvent e) -> ItemLogic.food(view, e));

        // Batata
        new Stratified<Item>(events.item, Labels.item_batata)
                .add(Events.GenerateItemEvent.class, (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "Consume to stave off hunger and restore 1 health";
                    e.blob.icon = Optional.of(Labels.asset_harvest_batatas);
                    e.blob.gold = 1;
                    e.blob.tags.add(Labels.tag_fruit);
                    e.blob.tags.add(Labels.tag_natural);
                    return new SideEffect();
                }).add(Events.ItemConsumedEvent.class,
                        (GameView view, Item receiver, Events.ItemConsumedEvent e) -> new SideEffect()
                                .add(ItemLogic.food(view, e)).add(e.consumer.combat.heal(view, e.consumer, 1)));

        // Pumpkin
        new Stratified<Item>(events.item, Labels.item_pumpkin)
                .add(Events.GenerateItemEvent.class, (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "Consume to stave off hunger";
                    e.blob.icon = Optional.of(Labels.asset_harvest_pumpkins);
                    e.blob.gold = 1;
                    e.blob.tags.add(Labels.tag_fruit);
                    e.blob.tags.add(Labels.tag_natural);
                    return new SideEffect();
                }).add(Events.ItemConsumedEvent.class, (GameView view, Item receiver,
                        Events.ItemConsumedEvent e) -> new SideEffect().add(ItemLogic.food(view, e)));

        // Fig
        new Stratified<Item>(events.item, Labels.item_fig)
                .add(Events.GenerateItemEvent.class, (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "A sweet edible fruit resembling a tiny satchel";
                    e.blob.icon = Optional.of(Labels.asset_harvest_figs);
                    e.blob.gold = 1;
                    e.blob.tags.add(Labels.tag_natural);
                    e.blob.tags.add(Labels.tag_fruit);
                    return new SideEffect();
                }).add(Events.ItemConsumedEvent.class,
                        (GameView view, Item receiver, Events.ItemConsumedEvent e) -> ItemLogic.food(view, e));

        // Truffle
        new Stratified<Item>(events.item, Labels.item_truffle)
                .add(Events.GenerateItemEvent.class, (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "A tasty and expensive fungus";
                    // TODO add a real icon
                    e.blob.icon = Optional.of(Labels.asset_mushroom);
                    e.blob.gold = 10;
                    e.blob.tags.add(Labels.tag_mushroom);
                    e.blob.tags.add(Labels.tag_natural);
                    e.blob.tags.add(Labels.tag_fruit);
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
        // Bag of Gold
        new Stratified<Item>(events.item, Labels.item_bag_of_gold)
                .add(Events.GenerateItemEvent.class, (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "Consume to increase your gold";
                    e.blob.icon = Optional.of(Labels.asset_pouch);
                    e.blob.gold = 5;
                    return new SideEffect();
                }).add(ItemLogic.valuable());

        // Capital
        new Stratified<Item>(events.item, Labels.item_capital)
                .add(Events.GenerateItemEvent.class, (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "Consume to generate 6 auction points";
                    e.blob.icon = Optional.of(Labels.asset_paper);
                    e.blob.gold = 6;
                    return new SideEffect();
                }).add(Events.ItemConsumedEvent.class, (GameView view, Item receiver,
                        Events.ItemConsumedEvent e) -> AbilityLogic.generateAuctionPoints(view, e.consumer, 10));

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
        // Baked Bread
        // Sacred Charm
        // Warrior Blade
        // Iron Aegis
        // Staff of Vitality
        // Warlord Totem
        // Spoils of War
        // Victor's Cache
        // Cudgel
        // Leather Armor
        // Hearty Truffle
        // Stag's Antler Pendant
        // Merchant's Sign
        // Exquisite Jewels
        // Blessed Charm
        // Bloody Totem
        // Phoenix Blossom
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
        // Wooden Armor
        // Net Bag
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
        // Warlock's Staff
        // Antidote
        // Courrier's Boots
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
                        (GameView view, Item receiver, Events.ItemConsumedEvent e) -> ItemLogic.build(view, e.consumer,
                                Labels.building_meadow, (Tile t) -> t.name.equals(Labels.tile_grass)));

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
                        (GameView view, Item receiver, Events.ItemConsumedEvent e) -> ItemLogic.build(view, e.consumer,
                                Labels.building_forest, (Tile t) -> t.name.equals(Labels.tile_grass)));

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
                        (GameView view, Item receiver, Events.ItemConsumedEvent e) -> ItemLogic.build(view, e.consumer,
                                Labels.building_taiga, (Tile t) -> t.name.equals(Labels.tile_snow)));

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
                        (GameView view, Item receiver, Events.ItemConsumedEvent e) -> ItemLogic.build(view, e.consumer,
                                Labels.building_shrubland, (Tile t) -> t.name.equals(Labels.tile_sand)));

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
                        (GameView view, Item receiver, Events.ItemConsumedEvent e) -> ItemLogic.build(view, e.consumer,
                                Labels.building_oasis, (Tile t) -> t.name.equals(Labels.tile_sand)));

        // Digging Kit
        new Stratified<Item>(events.item, Labels.item_digging_kit)
                .add(Events.GenerateItemEvent.class, (GameView view, Item receiver, Events.GenerateItemEvent e) -> {
                    e.blob.desc = "Consume to dig a mine";
                    e.blob.icon = Optional.of(Labels.asset_shovel);
                    e.blob.rarity = Rarity.RARE;
                    e.blob.gold = 10;
                    return new SideEffect();
                }).add(Events.ItemConsumedEvent.class,
                        (GameView view, Item receiver, Events.ItemConsumedEvent e) -> ItemLogic.build(view, e.consumer,
                                Labels.building_mine, (Tile t) -> true));

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
        // Sanguine Blade
        // Necrotic Tome
        // Self-Sustaining Soulstone
        // Hero's Call
    }
}
