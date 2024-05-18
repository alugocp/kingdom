import std/sets
import std/math
import std/sugar
import std/random
import std/options
import std/strformat
import kingdom/headers
import kingdom/views/types
import kingdom/models/types
import kingdom/entities/types
import kingdom/builtin/types
import kingdom/builtin/signals
import kingdom/builtin/channels
import kingdom/models/types
import kingdom/mods/types

#
# LABELS FOR MOD CONTENT
#

# Stats
const STAT_CONSTITUTION = "Constitution"
const STAT_CHARISMA = "Charisma"
const STAT_AGILITY = "Agility"
const STAT_DEXTERITY = "Dexterity"
const STAT_STRENGTH = "Strength"
const STAT_WISDOM = "Wisdom"
const STAT_INTELLIGENCE = "Intelligence"

# Tags
const TAG_PLANT = "Plant"

# Borders
const BORDER_WATER = "water"

# Species
const SPECIES_PLESIOSAUR = "Plesiosaur"
const SPECIES_DRAGONOID = "Dragonoid"
const SPECIES_HUMANOID = "Humanoid"
const SPECIES_UNKNOWN = "Unknown"
const SPECIES_MERFOLK = "Merfolk"
const SPECIES_REPTILE = "Reptile"
const SPECIES_GREMLIN = "Gremlin"
const SPECIES_BANSHEE = "Banshee"
const SPECIES_SPIRIT = "Spirit"
const SPECIES_MAMMAL = "Mammal"
const SPECIES_BEETLE = "Beetle"
const SPECIES_INSECT = "Insect"
const SPECIES_KOBOLD = "Kobold"
const SPECIES_HUMAN = "Human"
const SPECIES_SLIME = "Slime"
const SPECIES_DWARF = "Dwarf"
const SPECIES_BEAST = "Beast"
const SPECIES_TROLL = "Troll"
const SPECIES_SHADE = "Shade"
const SPECIES_DEER = "Deer"
const SPECIES_ELF = "Elf"

# Tiles
const TILE_GRASS = "Grass"
const TILE_WATER = "Water"
const TILE_COAST = "Coast"
const TILE_DESERT = "Desert"
const TILE_CACTUS = "Cactus"
const TILE_ISLAND_FORTRESS = "Island Fortress"
const TILE_WARLOCK_TOWER = "Warlock Tower"
const TILE_FOREST = "Forest"

# Units
const UNIT_PIKE_GREMLIN = "Pike Gremlin"
const UNIT_SHADE = "Shade"
const UNIT_IRON_BEETLE = "Iron Beetle"
const UNIT_SLIME_CUBE = "Slime Cube"
const UNIT_ACOLYTE_OF_CTHOS = "Acolyte of C'thos"
const UNIT_KOBOLD_SYCOPHANT = "Kobold Sycophant"
const UNIT_BANSHEE = "Banshee"
const UNIT_BUCK = "Buck"
const UNIT_SIR_EOINN = "Sir Eoinn"
const UNIT_IXTOLOLOTLI = "Ixtololotli"
const UNIT_ELDER_USQUANIGODI = "Elder Usquanigodi"
const UNIT_LADY_MARIA = "Lady Maria"
const UNIT_JACK_THE_SCOUNDREL = "Jack the Scoundrel"
const UNIT_GLUB_STRONGFIN = "Glub Strongfin"
const UNIT_DORRIE = "Dorrie"
const UNIT_DRUIDIC_HERMIT = "Druidic Hermit"
const UNIT_BATO = "Bato"
const UNIT_HARDIN_REDBEARD = "Hardin Redbeard"
const UNIT_TUNDE_THE_SORCEROR = "Tunde the Sorceror"
const UNIT_RANGER_DAWISGALA = "Ranger Dawisgala"
const UNIT_MALLARD_THE_MAGE = "Mallard the Mage"
const UNIT_MIZTON_OF_THE_WASTES = "Mizton of the Wastes"
const UNIT_GUROCH_THE_IMPENETRABLE = "Guroch the Impenetrable"
const UNIT_HUGINN_BLACKFEATHER = "Huginn Blackfeather"
const UNIT_AZDWAGIT_HALF_DJINN = "Azdwagit Half-Djinn"
const UNIT_ISSLIS_THE_SEARING_DEATH = "Iss'lis the Searing Death"

# Abilities
const ABILITY_STAB = "Stab"
const ABILITY_ZAP = "Zap"
const ABILITY_CURE_WOUNDS = "Cure Wounds"
const ABILITY_CHANT_OF_STRENGTH = "Chant of Strength"
const ABILITY_CURSE_OF_WEAKNESS = "Curse of Weakness"
const ABILITY_HARVEST_CHESTNUT = "Harvest Chestnut"
const ABILITY_HARVEST_NOPAL = "Harvest Nopal"
const ABILITY_HARVEST_SALMON = "Harvest Salmon"

# Status effects
const STATUS_DAMAGE_DEBUFF = "Damage Debuff"
const STATUS_DAMAGE_BUFF = "Damage Buff"

# Items
const ITEM_DAGGER_OF_AKHTHEMES = "Dagger of Akhthemes"
const ITEM_HEROS_HEART = "Hero's Heart"
const ITEM_CHESTNUT = "Chestnut"
const ITEM_NOPAL = "Nopal"
const ITEM_VEAL = "Veal"
const ITEM_SALMON = "Salmon"
const ITEM_BAG_OF_GOLD = "Bag of Gold"
const ITEM_GOLD_COIN = "Gold Coin"
const ITEM_NOVICES_CHARM = "Novice's Charm"
const ITEM_SCHOLARLY_AMULET = "Scholarly Amulet"
const ITEM_STONE_RING = "Stone Ring"
const ITEM_CRYSTAL_ROSE = "Crystal Rose"
const ITEM_SLIMEBREAKER = "Slimebreaker"
const ITEM_SORCEROUS_MEDALLION = "Sorcerous Medallion"
const ITEM_LUCKY_FISHING_ROD = "Lucky Fishing Rod"
const ITEM_TOME_OF_GEOMANCY = "Tome of Geomancy"
const ITEM_IRON_SWORD = "Iron Sword"
const ITEM_CHAIN_MAIL = "Chain Mail"
const ITEM_WIZARD_ROBES = "Wizard Robes"
const ITEM_NOPAL_KNIFE = "Nopal Knife"
const ITEM_TELESCOPE = "Telescope"
const ITEM_ENCHANTED_BOOTS = "Enchanted Boots"
const ITEM_AMETHYST_CROWN = "Amethyst Crown"
const ITEM_GEM_ENCRUSTED_MALLET = "Gem-Encrusted Mallet"
const ITEM_FEY_WROUGHT_PLATE = "Fey-Wrought Plate"
const ITEM_CLAMSHELL_OF_FAR_SIGHT = "Clamshell of Far Sight"
const ITEM_BAG_OF_MIRTH = "Bag of Mirth"
const ITEM_KINGSLAYER = "Kingslayer"

#
# MOD-SPECIFIC SIGNALS
#

const FISHED_CHANNEL = "Fished"
type FishedSignalArgs = ref object of BaseSignalArgs
    host: Unit
    haul: string
proc newFishedSignalArgs(host: Unit, haul: string): FishedSignalArgs =
    new result
    result.channel = FISHED_CHANNEL
    result.host = host
    result.haul = haul

#
# MOD INITIALIZATION PROCEDURE
#

proc initKingdomMod(game: ModCoreInterface): void {.exportc, dynlib.} =

    # Register spritesheets and set the edgeTileSprite
    let unitSprites = game.rules.sprites.registerSheet("vanilla", "units")
    let tileSprites = game.rules.sprites.registerSheet("vanilla", "tiles")
    game.rules.edgeTileSprite = game.rules.sprites.getSpriteHandle(tileSprites, 96, 0, 96, 110)

    #
    # UNIT GENERATORS
    #

    # Pike Gremlin
    game.rules.unitGeneration.addGenerator(UNIT_PIKE_GREMLIN, proc (): Unit =
        let unit = newUnit()
        unit.name = UNIT_PIKE_GREMLIN
        unit.desc = some("Standard foot soldiers in dark armies")
        unit.classification = @[SPECIES_BEAST, SPECIES_REPTILE, SPECIES_GREMLIN]
        unit.sprite = game.getUnitSprite(unitSprites, 4, 0)
        unit.setStat("Agility", 3)
        game.giveAbility(unit, ABILITY_STAB)
        return unit
    )

    # Shade
    game.rules.unitGeneration.addGenerator(UNIT_SHADE, proc (): Unit =
        let unit = newUnit()
        unit.name = UNIT_SHADE
        unit.desc = some("A malevolent spirit which is bound by some master's whim")
        unit.classification = @[SPECIES_SPIRIT, SPECIES_SHADE]
        unit.sprite = game.getUnitSprite(unitSprites, 5, 0)
        game.giveAbility(unit, ABILITY_ZAP)
        return unit
    )

    # Iron Beetle
    game.rules.unitGeneration.addGenerator(UNIT_IRON_BEETLE, proc (): Unit =
        let unit = newUnit()
        unit.name = UNIT_IRON_BEETLE
        unit.desc = some("Nearly impenetrable to physical damage")
        unit.classification = @[SPECIES_BEAST, SPECIES_INSECT, SPECIES_BEETLE]
        unit.sprite = game.getUnitSprite(unitSprites, 6, 0)
        unit.setStat(STAT_CONSTITUTION, 5)
        game.giveAbility(unit, ABILITY_STAB)
        unit.addArmor(DamageType.PHYSICAL, 3)
        return unit
    )

    # Slime Cube
    game.rules.unitGeneration.addGenerator(UNIT_SLIME_CUBE, proc (): Unit =
        let unit = newUnit()
        unit.name = UNIT_SLIME_CUBE
        unit.desc = some("Magic has little effect on this strange creature")
        unit.classification = @[SPECIES_SLIME]
        unit.sprite = game.getUnitSprite(unitSprites, 7, 0)
        game.giveAbility(unit, ABILITY_ZAP)
        unit.addArmor(DamageType.MAGICAL, 3)
        return unit
    )

    # Acolyte of C'thos
    game.rules.unitGeneration.addGenerator(UNIT_ACOLYTE_OF_CTHOS, proc (): Unit =
        let unit = newUnit()
        unit.name = UNIT_ACOLYTE_OF_CTHOS
        unit.desc = some("A dark hooded figure who awaits the return of ancient and terrible elder gods")
        unit.classification = @[SPECIES_HUMANOID, SPECIES_UNKNOWN]
        unit.sprite = game.getUnitSprite(unitSprites, 4, 1)
        game.giveAbility(unit, ABILITY_CURE_WOUNDS)
        game.giveAbility(unit, ABILITY_ZAP)
        return unit
    )

    # Kobold Sycophant
    game.rules.unitGeneration.addGenerator(UNIT_KOBOLD_SYCOPHANT, proc (): Unit =
        let unit = newUnit()
        unit.name = UNIT_KOBOLD_SYCOPHANT
        unit.desc = some("This small dragonoid riles its comrades through song and dance")
        unit.classification = @[SPECIES_DRAGONOID, SPECIES_KOBOLD]
        unit.sprite = game.getUnitSprite(unitSprites, 5, 1)
        game.giveAbility(unit, ABILITY_CHANT_OF_STRENGTH)
        game.giveAbility(unit, ABILITY_STAB)
        return unit
    )

    # Banshee
    game.rules.unitGeneration.addGenerator(UNIT_BANSHEE, proc (): Unit =
        let unit = newUnit()
        unit.name = UNIT_BANSHEE
        unit.desc = some("A vengeful spirit cursed to roam the earth and inflict suffering to whomever crosses its path")
        unit.classification = @[SPECIES_SPIRIT, SPECIES_BANSHEE]
        unit.sprite = game.getUnitSprite(unitSprites, 6, 1)
        game.giveAbility(unit, ABILITY_CURSE_OF_WEAKNESS)
        game.giveAbility(unit, ABILITY_ZAP)
        return unit
    )

    # Buck
    game.rules.unitGeneration.addGenerator(UNIT_BUCK, proc (): Unit =
        let unit = newUnit()
        unit.name = UNIT_BUCK
        unit.desc = some("An adult male deer that can provide meat for your armies")
        unit.classification = @[SPECIES_BEAST, SPECIES_MAMMAL, SPECIES_DEER]
        unit.sprite = game.getUnitSprite(unitSprites, 7, 1)
        game.giveAbility(unit, ABILITY_STAB)
        game.dropLoot(unit, @[ITEM_VEAL])
        return unit
    )

    # Sir Eoinn
    game.rules.unitGeneration.addGenerator(UNIT_SIR_EOINN, proc (): Unit =
        let unit = newUnit()
        unit.name = UNIT_SIR_EOINN
        unit.desc = some("He's a paladin of great standing with the Dwarven high council")
        unit.classification = @[SPECIES_HUMANOID, SPECIES_DWARF]
        unit.sprite = game.getUnitSprite(unitSprites, 2, 2)
        unit.setStat(STAT_CONSTITUTION, 8)
        unit.setStat(STAT_STRENGTH, 5)
        unit.setStat(STAT_WISDOM, 5)
        unit.addArmor(DamageType.PHYSICAL, 3)
        game.giveAbility(unit, ABILITY_STAB)
        unit.setVision(1)
        unit.setSpeed(1)
        return unit
    )

    # Ixtololotli
    game.rules.unitGeneration.addGenerator(UNIT_IXTOLOLOTLI, proc (): Unit =
        let unit = newUnit()
        unit.name = UNIT_IXTOLOLOTLI
        unit.desc = some("An elf ranger from the steaming jungles of the far West")
        unit.classification = @[SPECIES_HUMANOID, SPECIES_ELF]
        unit.sprite = game.getUnitSprite(unitSprites, 2, 2)
        unit.setStat(STAT_AGILITY, 7)
        unit.setStat(STAT_CHARISMA, 4)
        unit.setStat(STAT_WISDOM, 4)
        game.giveAbility(unit, ABILITY_STAB)
        unit.setMaxHunger(20)
        unit.setVision(3)
        unit.setSpeed(3)
        return unit
    )

    # Elder Usquanigodi
    game.rules.unitGeneration.addGenerator(UNIT_ELDER_USQUANIGODI, proc (): Unit =
        let unit = newUnit()
        unit.name = UNIT_ELDER_USQUANIGODI
        unit.desc = some("She's a medicine woman from the chestnut woods of the far West. +3 damage on forest tiles")
        unit.classification = @[SPECIES_HUMANOID, SPECIES_HUMAN]
        unit.sprite = game.getUnitSprite(unitSprites, 1, 2)
        unit.setStat(STAT_WISDOM, 8)
        unit.setStat(STAT_INTELLIGENCE, 5)
        game.giveAbility(unit, ABILITY_ZAP)
        game.giveAbility(unit, ABILITY_HARVEST_CHESTNUT)
        unit.setVision(2)
        unit.addSignalHandler(DEAL_DAMAGE_CHANNEL, proc (this: Unit, ctx: SignalContext, args: BaseSignalArgs): void =
            let a = cast[DealDamageSignalArgs](args)
            let tile = game.getGameView().world.getTile(this.pos)
            if tile.name == TILE_FOREST:
                a.dmg += 3
        )
        return unit
    )

    # Lady Maria
    game.rules.unitGeneration.addGenerator(UNIT_LADY_MARIA, proc (): Unit =
        let unit = newUnit()
        unit.name = UNIT_LADY_MARIA
        unit.desc = some("A cleric of the holy light who rides to vanquish evil")
        unit.classification = @[SPECIES_HUMANOID, SPECIES_HUMAN]
        unit.sprite = game.getUnitSprite(unitSprites, 1, 2)
        unit.setStat(STAT_INTELLIGENCE, 6)
        unit.setStat(STAT_AGILITY, 8)
        game.giveAbility(unit, ABILITY_ZAP)
        game.giveAbility(unit, ABILITY_CURE_WOUNDS)
        unit.addArmor(DamageType.PHYSICAL, -3)
        unit.setSpeed(3)
        return unit
    )

    # Jack the Scoundrel
    game.rules.unitGeneration.addGenerator(UNIT_JACK_THE_SCOUNDREL, proc (): Unit =
        let unit = newUnit()
        unit.name = UNIT_JACK_THE_SCOUNDREL
        unit.desc = some("This human has cheated death on nearly a hundred different adventures at sea")
        unit.classification = @[SPECIES_HUMANOID, SPECIES_HUMAN]
        unit.sprite = game.getUnitSprite(unitSprites, 2, 2)
        unit.setStat(STAT_DEXTERITY, 8)
        unit.setStat(STAT_CHARISMA, 10)
        unit.setStat(STAT_WISDOM, 7)
        game.giveAbility(unit, ABILITY_STAB)
        game.giveAbility(unit, ABILITY_HARVEST_SALMON)
        unit.addArmor(DamageType.MAGICAL, 3)
        unit.setSpeed(2)
        return unit
    )

    # Glub Strongfin
    game.rules.unitGeneration.addGenerator(UNIT_GLUB_STRONGFIN, proc (): Unit =
        let unit = newUnit()
        unit.name = UNIT_GLUB_STRONGFIN
        unit.desc = some("A highly skilled and experienced Merfolk warrior")
        unit.classification = @[SPECIES_HUMANOID, SPECIES_MERFOLK]
        unit.sprite = game.getUnitSprite(unitSprites, 2, 2)
        unit.setStat(STAT_STRENGTH, 8)
        unit.setStat(STAT_DEXTERITY, 4)
        game.giveAbility(unit, ABILITY_STAB)
        game.giveAbility(unit, ABILITY_HARVEST_SALMON)
        unit.setSpeed(2)
        unit.addSignalHandler(TAKE_DAMAGE_CHANNEL, proc (this: Unit, ctx: SignalContext, args: BaseSignalArgs): void =
            let a = cast[TakeDamageSignalArgs](args)
            let tile = game.getGameView().world.getTile(this.pos)
            if tile.name != TILE_WATER:
                a.dmg -= 3
        )
        unit.addSignalHandler(GET_MOVEMENT_CHANNEL, proc (this: Unit, ctx: SignalContext, args: BaseSignalArgs): void =
            let a = cast[GetMovementSignalArgs](args)
            let tile = game.getGameView().world.getTile(this.pos)
            if tile.name == TILE_WATER:
                a.movement += 2
        )
        unit.addSignalHandler(GET_VISIBILITY_CHANNEL, proc (this: Unit, ctx: SignalContext, args: BaseSignalArgs): void =
            let a = cast[GetVisibilitySignalArgs](args)
            let tile = game.getGameView().world.getTile(this.pos)
            if tile.name == TILE_WATER:
                a.visibility += 3
        )
        unit.addSignalHandler(CAN_CROSS_BORDER_CHANNEL, proc (this: Unit, ctx: SignalContext, args: BaseSignalArgs): void =
            let a = cast[CanCrossBorderSignalArgs](args)
            if a.border == BORDER_WATER:
                a.canCross = MovementType.CROSS
        )
        return unit
    )

    # Dorrie
    game.rules.unitGeneration.addGenerator(UNIT_DORRIE, proc (): Unit =
        let unit = newUnit()
        unit.name = UNIT_DORRIE
        unit.desc = some("A gentle giant of the seas who can carry land creatures on her back")
        unit.classification = @[SPECIES_BEAST, SPECIES_PLESIOSAUR]
        unit.sprite = game.getUnitSprite(unitSprites, 2, 2)
        unit.maxHaul = 20
        unit.baseHealth = 150
        unit.setStat(STAT_STRENGTH, 10)
        unit.setStat(STAT_CONSTITUTION, 10)
        unit.setSpeed(2)
        unit.addSignalHandler(CAN_CROSS_BORDER_CHANNEL, proc (this: Unit, ctx: SignalContext, args: BaseSignalArgs): void =
            let a = cast[CanCrossBorderSignalArgs](args)
            if a.border == BORDER_WATER:
                a.canCross = MovementType.OVERRIDE
            else:
                a.canCross = MovementType.BLOCKED
        )

        return unit
    )

    # Druidic Hermit
    game.rules.unitGeneration.addGenerator(UNIT_DRUIDIC_HERMIT, proc (): Unit =
        let unit = newUnit()
        unit.name = UNIT_DRUIDIC_HERMIT
        unit.desc = some("This druid keeps to themself and is seldom seen outside the forest")
        unit.classification = @[SPECIES_HUMANOID, SPECIES_UNKNOWN]
        unit.sprite = game.getUnitSprite(unitSprites, 0, 1)
        unit.setStat(STAT_WISDOM, 10)
        game.giveAbility(unit, ABILITY_ZAP)
        unit.setVision(4)
        unit.setSpeed(2)
        unit.addSignalHandler(DEAL_DAMAGE_CHANNEL, proc (this: Unit, ctx: SignalContext, args: BaseSignalArgs): void =
            let a = cast[DealDamageSignalArgs](args)
            var plants = initHashSet[string]()
            for item in unit.haul:
                if item.tags.contains(TAG_PLANT):
                    plants.incl(item.name)
            a.dmg += plants.len
        )
        return unit
    )

    # Bato
    game.rules.unitGeneration.addGenerator(UNIT_BATO, proc (): Unit =
        let unit = newUnit()
        unit.name = UNIT_BATO
        unit.desc = some("This lumbering Troll is slow but can haul large amounts of loot")
        unit.classification = @[SPECIES_HUMANOID, SPECIES_TROLL]
        unit.sprite = game.getUnitSprite(unitSprites, 0, 1)
        unit.maxHaul = 20
        unit.setStat(STAT_CONSTITUTION, 10)
        unit.setStat(STAT_STRENGTH, 8)
        game.giveAbility(unit, ABILITY_STAB)
        unit.setMaxHunger(100)
        unit.setVision(2)
        unit.setSpeed(1)
        return unit
    )

    #
    # ABILITY GENERATORS
    #

    # Stab
    game.rules.abilityGeneration.addGenerator(ABILITY_STAB, proc(): Ability =
        let ability = newAbility()
        ability.name = ABILITY_STAB
        ability.desc = some("Deals 5 physical damage")
        ability.addSignalHandler(ABILITY_CLICKED_CHANNEL, proc (this: Ability, ctx: SignalContext, args: BaseSignalArgs): void =
            game.attack(args, DamageType.PHYSICAL, 5)
        )
        return ability
    )

    # Zap
    game.rules.abilityGeneration.addGenerator(ABILITY_ZAP, proc(): Ability =
        let ability = newAbility()
        ability.name = ABILITY_ZAP
        ability.desc = some("Deals 5 magical damage")
        ability.addSignalHandler(ABILITY_CLICKED_CHANNEL, proc (this: Ability, ctx: SignalContext, args: BaseSignalArgs): void =
            game.attack(args, DamageType.MAGICAL, 5)
        )
        return ability
    )

    # Cure Wounds
    game.rules.abilityGeneration.addGenerator(ABILITY_CURE_WOUNDS, proc(): Ability =
        let ability = newAbility()
        ability.name = ABILITY_CURE_WOUNDS
        ability.desc = some("Heals 6 damage")
        ability.addSignalHandler(ABILITY_CLICKED_CHANNEL, proc (this: Ability, ctx: SignalContext, args: BaseSignalArgs): void =
            let a = cast[AbilityClickedSignalArgs](args)
            let view = game.getGameView()
            let allies = view.world.getAllies(a.host)
            view.targeter.target(allies, (u: Unit) => a.host.heal(u, 6))
        )
        return ability
    )

    # Curse of Weakness
    game.rules.abilityGeneration.addGenerator(ABILITY_CURSE_OF_WEAKNESS, proc(): Ability =
        let ability = newAbility()
        ability.name = ABILITY_CURSE_OF_WEAKNESS
        ability.desc = some("Debuffs the target's damage")
        ability.addSignalHandler(ABILITY_CLICKED_CHANNEL, proc (this: Ability, ctx: SignalContext, args: BaseSignalArgs): void =
            let a = cast[AbilityClickedSignalArgs](args)
            let view = game.getGameView()
            let enemies = view.world.getEnemies(a.host)
            view.targeter.target(enemies, (u: Unit) => u.addStatus(3, game.rules.abilityGeneration.generate(STATUS_DAMAGE_DEBUFF)))
        )
        return ability
    )

    # Chant of Strength
    game.rules.abilityGeneration.addGenerator(ABILITY_CHANT_OF_STRENGTH, proc(): Ability =
        let ability = newAbility()
        ability.name = ABILITY_CHANT_OF_STRENGTH
        ability.desc = some("Buffs the target's damage")
        ability.addSignalHandler(ABILITY_CLICKED_CHANNEL, proc (this: Ability, ctx: SignalContext, args: BaseSignalArgs): void =
            let a = cast[AbilityClickedSignalArgs](args)
            let view = game.getGameView()
            let allies = view.world.getAllies(a.host)
            view.targeter.target(allies, (u: Unit) => u.addStatus(3, game.rules.abilityGeneration.generate(STATUS_DAMAGE_BUFF)))
        )
        return ability
    )

    # Harvest Chestnut
    game.rules.abilityGeneration.addGenerator(ABILITY_HARVEST_CHESTNUT, proc(): Ability =
        let ability = newAbility()
        ability.name = ABILITY_HARVEST_CHESTNUT
        ability.desc = some("Can harvest chestnuts from forest tiles")
        ability.addSignalHandler(ABILITY_CLICKED_CHANNEL, proc (this: Ability, ctx: SignalContext, args: BaseSignalArgs): void =
            game.harvest(args, TILE_FOREST, ITEM_CHESTNUT)
        )
        return ability
    )

    # Harvest Nopal
    game.rules.abilityGeneration.addGenerator(ABILITY_HARVEST_NOPAL, proc(): Ability =
        let ability = newAbility()
        ability.name = ABILITY_HARVEST_NOPAL
        ability.desc = some("Can harvest nopales from cactus patches")
        ability.addSignalHandler(ABILITY_CLICKED_CHANNEL, proc (this: Ability, ctx: SignalContext, args: BaseSignalArgs): void =
            game.harvest(args, TILE_CACTUS, ITEM_NOPAL)
        )
        return ability
    )

    game.rules.abilityGeneration.addGenerator(ABILITY_HARVEST_SALMON, proc(): Ability =
        let ability = newAbility()
        ability.name = ABILITY_HARVEST_SALMON
        ability.desc = some("Can harvest salmon from the water")
        ability.addSignalHandler(ABILITY_CLICKED_CHANNEL, proc (this: Ability, ctx: SignalContext, args: BaseSignalArgs): void =
            let a = cast[AbilityClickedSignalArgs](args)
            game.harvest(args, TILE_WATER, ITEM_SALMON)
            a.host.handleSignal(@[], newFishedSignalArgs(a.host, ITEM_SALMON))
        )
        return ability
    )

    #
    # STATUS GENERATORS
    #

    # Damage Debuff
    game.rules.abilityGeneration.addGenerator(STATUS_DAMAGE_DEBUFF, proc(): Ability =
        let ability = newAbility()
        ability.name = STATUS_DAMAGE_DEBUFF
        ability.desc = some("This unit deals -3 damage")
        ability.addSignalHandler(DEAL_DAMAGE_CHANNEL, proc (this: Ability, ctx: SignalContext, args: BaseSignalArgs): void =
            let a = cast[DealDamageSignalArgs](args)
            a.dmg -= 3
        )
        return ability
    )

    # Damage Buff
    game.rules.abilityGeneration.addGenerator(STATUS_DAMAGE_BUFF, proc(): Ability =
        let ability = newAbility()
        ability.name = STATUS_DAMAGE_BUFF
        ability.desc = some("This unit deals +3 damage")
        ability.addSignalHandler(DEAL_DAMAGE_CHANNEL, proc (this: Ability, ctx: SignalContext, args: BaseSignalArgs): void =
            let a = cast[DealDamageSignalArgs](args)
            a.dmg += 3
        )
        return ability
    )

    #
    # ITEM GENERATORS
    #

    # Food items
    game.rules.itemGeneration.addGenerator(ITEM_CHESTNUT, proc(): Item =
        let item = game.createFoodItem(ITEM_CHESTNUT)
        item.addTag(TAG_PLANT)
    )
    game.rules.itemGeneration.addGenerator(ITEM_NOPAL, proc(): Item =
        let item = game.createFoodItem(ITEM_NOPAL)
        item.addTag(TAG_PLANT)
    )
    game.rules.itemGeneration.addGenerator(ITEM_VEAL, proc(): Item = game.createFoodItem(ITEM_VEAL))
    game.rules.itemGeneration.addGenerator(ITEM_SALMON, proc(): Item = game.createFoodItem(ITEM_SALMON))

    # Dagger of Akhthemes
    game.rules.itemGeneration.addGenerator(ITEM_DAGGER_OF_AKHTHEMES, proc(): Item =
        let item = newItem()
        item.name = ITEM_DAGGER_OF_AKHTHEMES
        item.desc = fmt"Physical attacks deal +5 damage to targets with a {STAT_CONSTITUTION} stat"
        item.addSignalHandler(DEAL_DAMAGE_CHANNEL, proc (this: Item, ctx: SignalContext, args: BaseSignalArgs): void =
            let a = cast[DealDamageSignalArgs](args)
            if a.dtype == DamageType.PHYSICAL and a.target.hasStat(STAT_CONSTITUTION):
                a.dmg += 5
        )
    )

    # Hero's Heart
    game.rules.itemGeneration.addGenerator(ITEM_HEROS_HEART, proc(): Item =
        let item = newItem()
        item.name = ITEM_HEROS_HEART
        item.desc = "+10 max health"
        item.addSignalHandler(GET_MAX_HEALTH_CHANNEL, proc (this: Item, ctx: SignalContext, args: BaseSignalArgs): void =
            let a = cast[GetMaxHealthSignalArgs](args)
            a.health += 10
        )
    )

    # Gold items
    game.rules.itemGeneration.addGenerator(ITEM_BAG_OF_GOLD, proc(): Item = game.createGoldItem(ITEM_BAG_OF_GOLD, 10))
    game.rules.itemGeneration.addGenerator(ITEM_GOLD_COIN, proc(): Item = game.createGoldItem(ITEM_GOLD_COIN, 1))

    # Novice's Charm
    game.rules.itemGeneration.addGenerator(ITEM_NOVICES_CHARM, proc(): Item =
        let item = newItem()
        item.name = ITEM_NOVICES_CHARM
        item.desc = "+5 damage for a unit under level 4"
        item.addSignalHandler(DEAL_DAMAGE_CHANNEL, proc (this: Item, ctx: SignalContext, args: BaseSignalArgs): void =
            let a = cast[DealDamageSignalArgs](args)
            if a.attacker.level < 4:
                a.dmg += 5
        )
    )

    # Scholarly Amulet
    game.rules.itemGeneration.addGenerator(ITEM_SCHOLARLY_AMULET, proc(): Item =
        let item = newItem()
        item.name = ITEM_SCHOLARLY_AMULET
        item.desc = "+25% XP gain"
        item.addSignalHandler(GAIN_XP_CHANNEL, proc (this: Item, ctx: SignalContext, args: BaseSignalArgs): void =
            let a = cast[GainXpSignalArgs](args)
            a.xp += int(floor(float(a.xp) * 0.25))
        )
    )

    # Ring of Satisfaction
    game.rules.itemGeneration.addGenerator(ITEM_STONE_RING, proc(): Item =
        let item = newItem()
        item.name = ITEM_STONE_RING
        item.desc = fmt"+2 {STAT_CONSTITUTION}"
        game.modifyUserStat(item, STAT_CONSTITUTION, 2)
    )

    # Crystal Rose
    game.rules.itemGeneration.addGenerator(ITEM_CRYSTAL_ROSE, proc(): Item =
        let item = newItem()
        item.name = ITEM_CRYSTAL_ROSE
        item.desc = fmt"+2 {STAT_CHARISMA}"
        game.modifyUserStat(item, STAT_CHARISMA, 2)
    )

    # Slimebreaker
    game.rules.itemGeneration.addGenerator(ITEM_SLIMEBREAKER, proc(): Item =
        let item = newItem()
        item.name = ITEM_SLIMEBREAKER
        item.desc = fmt"+5 damage against {SPECIES_SLIME} enemies"
        item.addSignalHandler(DEAL_DAMAGE_CHANNEL, proc (this: Item, ctx: SignalContext, args: BaseSignalArgs): void =
            let a = cast[DealDamageSignalArgs](args)
            if SPECIES_SLIME in a.target.classification:
                a.dmg += 5
        )
    )

    # Sorcerous Medallion
    game.rules.itemGeneration.addGenerator(ITEM_SORCEROUS_MEDALLION, proc(): Item =
        let item = newItem()
        item.name = ITEM_SORCEROUS_MEDALLION
        item.desc = fmt"-1 magical armor, +1 magical damage per {STAT_CHARISMA} point"
        item.addSignalHandler(DEAL_DAMAGE_CHANNEL, proc (this: Item, ctx: SignalContext, args: BaseSignalArgs): void =
            let a = cast[DealDamageSignalArgs](args)
            if a.dtype == DamageType.MAGICAL and a.attacker.hasStat(STAT_CHARISMA):
                a.dmg += a.attacker.getStat(STAT_CHARISMA)
        )
        item.addSignalHandler(TAKE_DAMAGE_CHANNEL, proc (this: Item, ctx: SignalContext, args: BaseSignalArgs): void =
            let a = cast[TakeDamageSignalArgs](args)
            if a.dtype == DamageType.MAGICAL:
                a.dmg -= 1
        )
    )

    # Lucky Fishing Rod
    game.rules.itemGeneration.addGenerator(ITEM_LUCKY_FISHING_ROD, proc(): Item =
        let item = newItem()
        item.name = ITEM_LUCKY_FISHING_ROD
        item.desc = fmt"20% chance to catch an extra fish when fishing"
        item.addSignalHandler(FISHED_CHANNEL, proc (this: Item, ctx: SignalContext, args: BaseSignalArgs): void =
            let a = cast[FishedSignalArgs](args)
            if rand(1..100) <= 20:
                discard game.getGameView().addNewItem(a.haul, a.host.pos)
        )
    )

    # Tome of Geomancy
    game.rules.itemGeneration.addGenerator(ITEM_TOME_OF_GEOMANCY, proc(): Item =
        let item = newItem()
        item.name = ITEM_TOME_OF_GEOMANCY
        item.desc = "Magical attacks become physical"
        item.addSignalHandler(DEAL_DAMAGE_CHANNEL, proc (this: Item, ctx: SignalContext, args: BaseSignalArgs): void =
            let a = cast[DealDamageSignalArgs](args)
            if a.dtype == DamageType.MAGICAL:
                a.dtype = DamageType.PHYSICAL
        )
    )

    # Iron Sword
    game.rules.itemGeneration.addGenerator(ITEM_IRON_SWORD, proc(): Item =
        let item = newItem()
        item.name = ITEM_IRON_SWORD
        item.desc = "+2 damage"
        item.addSignalHandler(DEAL_DAMAGE_CHANNEL, proc (this: Item, ctx: SignalContext, args: BaseSignalArgs): void =
            let a = cast[DealDamageSignalArgs](args)
            a.dmg += 2
        )
    )

    # Chain Mail
    game.rules.itemGeneration.addGenerator(ITEM_CHAIN_MAIL, proc(): Item =
        let item = newItem()
        item.name = ITEM_CHAIN_MAIL
        item.desc = "+3 physical armor"
        item.addSignalHandler(TAKE_DAMAGE_CHANNEL, proc (this: Item, ctx: SignalContext, args: BaseSignalArgs): void =
            let a = cast[TakeDamageSignalArgs](args)
            if a.dtype == DamageType.PHYSICAL:
                a.dmg -= 3
        )
    )

    # Wizard Robes
    game.rules.itemGeneration.addGenerator(ITEM_WIZARD_ROBES, proc(): Item =
        let item = newItem()
        item.name = ITEM_WIZARD_ROBES
        item.desc = "+3 magical armor"
        item.addSignalHandler(TAKE_DAMAGE_CHANNEL, proc (this: Item, ctx: SignalContext, args: BaseSignalArgs): void =
            let a = cast[TakeDamageSignalArgs](args)
            if a.dtype == DamageType.MAGICAL:
                a.dmg -= 3
        )
    )

    # Nopal Knife
    game.rules.itemGeneration.addGenerator(ITEM_NOPAL_KNIFE, proc(): Item =
        let item = newItem()
        item.name = ITEM_NOPAL_KNIFE
        item.desc = "Activate to harvest nopales on desert tiles"
        item.addSignalHandler(ITEM_ACTIVATED_CHANNEL, proc (this: Item, ctx: SignalContext, args: BaseSignalArgs): void =
            game.harvest(args, TILE_CACTUS, ITEM_NOPAL)
        )
    )

    # Telescope
    game.rules.itemGeneration.addGenerator(ITEM_TELESCOPE, proc(): Item =
        let item = newItem()
        item.name = ITEM_TELESCOPE
        item.desc = "+1 vision"
        item.addSignalHandler(GET_VISIBILITY_CHANNEL, proc (this: Item, ctx: SignalContext, args: BaseSignalArgs): void =
            let a = cast[GetVisibilitySignalArgs](args)
            a.visibility += 1
        )
    )

    # Enchanted Boots
    game.rules.itemGeneration.addGenerator(ITEM_ENCHANTED_BOOTS, proc(): Item =
        let item = newItem()
        item.name = ITEM_ENCHANTED_BOOTS
        item.desc = "+1 movement"
        item.addSignalHandler(GET_MOVEMENT_CHANNEL, proc (this: Item, ctx: SignalContext, args: BaseSignalArgs): void =
            let a = cast[GetMovementSignalArgs](args)
            a.movement += 1
        )
    )

    # Amethyst Crown
    game.rules.itemGeneration.addGenerator(ITEM_AMETHYST_CROWN, proc(): Item =
        let item = newItem()
        item.name = ITEM_AMETHYST_CROWN
        item.desc = "+2 restoration from heals"
        item.addSignalHandler(TAKE_HEAL_CHANNEL, proc (this: Item, ctx: SignalContext, args: BaseSignalArgs): void =
            let a = cast[TakeHealSignalArgs](args)
            a.health += 2
        )
    )

    # Gem-Encrusted Mallet
    game.rules.itemGeneration.addGenerator(ITEM_GEM_ENCRUSTED_MALLET, proc(): Item =
        let item = newItem()
        item.name = ITEM_GEM_ENCRUSTED_MALLET
        item.desc = "Heals 1 damage when the user deals physical damage"
        item.addSignalHandler(DEAL_DAMAGE_CHANNEL, proc (this: Item, ctx: SignalContext, args: BaseSignalArgs): void =
            let a = cast[DealDamageSignalArgs](args)
            if a.dtype == DamageType.PHYSICAL:
                a.attacker.heal(a.attacker, 1)
        )
    )

    # Fey-Wrought Plate
    game.rules.itemGeneration.addGenerator(ITEM_FEY_WROUGHT_PLATE, proc(): Item =
        let item = newItem()
        item.name = ITEM_FEY_WROUGHT_PLATE
        item.desc = "+1 physical armor, +1 magical damage"
        item.addSignalHandler(DEAL_DAMAGE_CHANNEL, proc (this: Item, ctx: SignalContext, args: BaseSignalArgs): void =
            let a = cast[DealDamageSignalArgs](args)
            if a.dtype == DamageType.MAGICAL:
                a.dmg += 1
        )
        item.addSignalHandler(TAKE_DAMAGE_CHANNEL, proc (this: Item, ctx: SignalContext, args: BaseSignalArgs): void =
            let a = cast[TakeDamageSignalArgs](args)
            if a.dtype == DamageType.PHYSICAL:
                a.dmg -= 1
        )
    )

    # Clamshell of Far Sight
    game.rules.itemGeneration.addGenerator(ITEM_CLAMSHELL_OF_FAR_SIGHT, proc(): Item =
        let item = newItem()
        item.name = ITEM_CLAMSHELL_OF_FAR_SIGHT
        item.desc = "+2 vision on water tiles"
        item.addSignalHandler(GET_VISIBILITY_CHANNEL, proc (this: Item, ctx: SignalContext, args: BaseSignalArgs): void =
            let a = cast[GetVisibilitySignalArgs](args)
            let tile = game.getGameView().world.getTile(a.host.pos)
            if tile.name == TILE_WATER:
                a.visibility += 2
        )
    )

    # Bag of Mirth
    game.rules.itemGeneration.addGenerator(ITEM_BAG_OF_MIRTH, proc(): Item =
        let item = newItem()
        item.name = ITEM_BAG_OF_MIRTH
        item.desc = fmt"+1 {STAT_CHARISMA} when the user levels up"
        item.addSignalHandler(CAN_BE_EQUIPPED_CHANNEL, proc (this: Item, ctx: SignalContext, args: BaseSignalArgs): void =
            let a = cast[CanBeEquippedSignalArgs](args)
            a.equippable = a.host.hasStat(STAT_CHARISMA)
        )
        item.addSignalHandler(LEVEL_UP_CHANNEL, proc (this: Item, ctx: SignalContext, args: BaseSignalArgs): void =
            let a = cast[LevelUpSignalArgs](args)
            if a.host.hasStat(STAT_CHARISMA):
                a.host.incStat(STAT_CHARISMA, 1)
        )
    )

    # Kingslayer
    game.rules.itemGeneration.addGenerator(ITEM_KINGSLAYER, proc(): Item =
        let item = newItem()
        item.name = ITEM_KINGSLAYER
        item.desc = "+10 damage against targets above level 5"
        item.addSignalHandler(DEAL_DAMAGE_CHANNEL, proc (this: Item, ctx: SignalContext, args: BaseSignalArgs): void =
            let a = cast[DealDamageSignalArgs](args)
            if a.target.level > 5:
                a.dmg += 10
        )
    )

    #
    # TILE GENERATORS
    #

    game.rules.tileGeneration.addGenerator(TILE_GRASS, proc(): Tile =
        let tile = newTile(TILE_GRASS)
        tile.sprite = game.rules.sprites.getSpriteHandle(tileSprites, 0, 0, 96, 110)
        return tile
    )
    game.rules.tileGeneration.addGenerator(TILE_WATER, proc(): Tile =
        let tile = newTile(TILE_WATER)
        tile.sprite = game.rules.sprites.getSpriteHandle(tileSprites, 96, 0, 96, 110)
        tile.desc = some("Water that units must swim across")
        tile.setAllBorders(BORDER_WATER)
        return tile
    )
    game.rules.tileGeneration.addGenerator(TILE_WARLOCK_TOWER, proc(): Tile =
        let tile = newTile(TILE_WARLOCK_TOWER)
        tile.sprite = game.rules.sprites.getSpriteHandle(tileSprites, 192, 0, 96, 110)
        return tile
    )
    game.rules.tileGeneration.addGenerator(TILE_DESERT, proc(): Tile =
        let tile = newTile(TILE_DESERT)
        tile.sprite = game.rules.sprites.getSpriteHandle(tileSprites, 288, 0, 96, 110)
        return tile
    )
    game.rules.tileGeneration.addGenerator(TILE_FOREST, proc(): Tile =
        let tile = newTile(TILE_FOREST)
        tile.sprite = game.rules.sprites.getSpriteHandle(tileSprites, 0, 110, 96, 110)
        return tile
    )
    game.rules.tileGeneration.addGenerator(TILE_COAST, proc(): Tile =
        let tile = newTile(TILE_COAST)
        tile.sprite = game.rules.sprites.getSpriteHandle(tileSprites, 96, 110, 96, 110)
        return tile
    )
    game.rules.tileGeneration.addGenerator(TILE_ISLAND_FORTRESS, proc(): Tile =
        let tile = newTile(TILE_ISLAND_FORTRESS)
        tile.sprite = game.rules.sprites.getSpriteHandle(tileSprites, 192, 110, 96, 110)
        return tile
    )
    game.rules.tileGeneration.addGenerator(TILE_CACTUS, proc(): Tile =
        let tile = newTile(TILE_CACTUS)
        tile.sprite = game.rules.sprites.getSpriteHandle(tileSprites, 288, 110, 96, 110)
        return tile
    )
