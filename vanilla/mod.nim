import std/sugar
import std/options
import std/strformat
import kingdom/headers
import kingdom/views/types
import kingdom/models/types
import kingdom/entities/types
import kingdom/builtin/types
import kingdom/builtin/values
import kingdom/builtin/signals
import kingdom/builtin/channels
import kingdom/models/types
import kingdom/mods/types

# Unofficial/test content
const ITEM_RING_OF_STRENGTH = "Ring of Strength"
const UNIT_GLOOP = "Gloop the Adventurer"
const UNIT_BARNACLEHEAD = "Barnaclehead"
const UNIT_FERNANDO_UNFALTERING_GAZE = "Fernando of the Unfaltering Gaze"
const UNIT_HENRIETTA = "Henrietta"
const UNIT_DRUID = "Druid"
const UNIT_HOKA_AND_TATANKA = "Hoka and Tatanka"

#
# LABELS FOR MOD CONTENT
#

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
const ITEM_CHESTNUT = "Chestnut"
const ITEM_NOPAL = "Nopal"
const ITEM_VEAL = "Veal"
const ITEM_SALMON = "Salmon"

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

    game.rules.unitGeneration.addGenerator(UNIT_GLOOP, proc (): Unit =
        let unit = newUnit()
        unit.name = UNIT_GLOOP
        unit.desc = some("Just a slimy guy")
        unit.classification = @["Slime", "Plasmoid"]
        unit.sprite = game.rules.sprites.getSpriteHandle(unitSprites, 0, 0)
        unit.stats.setStat("Courage", 3)
        unit.stats.setStat("Constitution", 3)
        unit.stats.setStat("Dexterity", 3)
        unit.addSignalHandler(GET_MOVEMENT_CHANNEL, proc (this: Unit, ctx: SignalContext, args: BaseSignalArgs): void =
            let payload = cast[GetMovementSignalArgs](args)
            payload.movement = 2
        )
        return unit
    )
    game.rules.unitGeneration.addGenerator(UNIT_BARNACLEHEAD, proc (): Unit =
        let unit = newUnit()
        unit.name = UNIT_BARNACLEHEAD
        unit.desc = some("A coast-dwelling golem crafted by an island wizard")
        unit.classification = @["Homunculus", "Golem"]
        unit.sprite = game.rules.sprites.getSpriteHandle(unitSprites, 24, 0)
        unit.stats.setStat("Constitution", 5)
        return unit
    )
    game.rules.unitGeneration.addGenerator(UNIT_FERNANDO_UNFALTERING_GAZE, proc (): Unit =
        let unit = newUnit()
        unit.name = UNIT_FERNANDO_UNFALTERING_GAZE
        unit.desc = some("He's a notorious Garuda warlock")
        unit.classification = @["Humanoid", "Garuda"]
        unit.sprite = game.rules.sprites.getSpriteHandle(unitSprites, 48, 0)
        unit.stats.setStat("Wickedness", 8)
        unit.stats.setStat("Intellect", 8)
        return unit
    )
    game.rules.unitGeneration.addGenerator(UNIT_HENRIETTA, proc (): Unit =
        let unit = newUnit()
        unit.name = UNIT_HENRIETTA
        unit.desc = some("She was once a knight but has been stuck in polymorph as a chicken")
        unit.classification = @["Beast", "Bird", "Chicken"]
        unit.sprite = game.rules.sprites.getSpriteHandle(unitSprites, 72, 0)
        return unit
    )
    game.rules.unitGeneration.addGenerator(UNIT_DRUID, proc (): Unit =
        let unit = newUnit()
        unit.name = UNIT_DRUID
        unit.desc = some("Mysterious druid that wields nature magic")
        unit.classification = @["Humanoid", "Unknown"]
        unit.sprite = game.rules.sprites.getSpriteHandle(unitSprites, 0, 24)
        unit.stats.setStat("Wisdom", 6)
        unit.stats.setStat("Agility", 4)
        return unit
    )
    game.rules.unitGeneration.addGenerator(UNIT_HOKA_AND_TATANKA, proc (): Unit =
        let unit = newUnit()
        unit.name = UNIT_HOKA_AND_TATANKA
        unit.desc = some("This duo roams the plains in search of good causes")
        unit.classification = @["Humanoid", "Human"]
        unit.sprite = game.rules.sprites.getSpriteHandle(unitSprites, 24, 24)
        unit.stats.setStat("Constitution", 3)
        unit.stats.setStat("Agility", 3)
        unit.stats.setStat("Charisma", 3)
        return unit
    )

    # Pike Gremlin
    game.rules.unitGeneration.addGenerator(UNIT_PIKE_GREMLIN, proc (): Unit =
        let unit = newUnit()
        unit.name = UNIT_PIKE_GREMLIN
        unit.classification = @["Beast", "Reptile", "Gremlin"]
        unit.sprite = game.getUnitSprite(unitSprites, 4, 0)
        unit.stats.setStat("Agility", 3)
        game.giveAbility(unit, ABILITY_STAB)
        return unit
    )

    # Shade
    game.rules.unitGeneration.addGenerator(UNIT_SHADE, proc (): Unit =
        let unit = newUnit()
        unit.name = UNIT_SHADE
        unit.classification = @["Spirit", "Shade"]
        unit.sprite = game.getUnitSprite(unitSprites, 5, 0)
        game.giveAbility(unit, ABILITY_ZAP)
        return unit
    )

    # Iron Beetle
    game.rules.unitGeneration.addGenerator(UNIT_IRON_BEETLE, proc (): Unit =
        let unit = newUnit()
        unit.name = UNIT_IRON_BEETLE
        unit.classification = @["Beast", "Insect", "Beetle"]
        unit.sprite = game.getUnitSprite(unitSprites, 6, 0)
        unit.stats.setStat("Constitution", 5)
        game.giveAbility(unit, ABILITY_STAB)
        unit.addArmor(DamageType.PHYSICAL, 3)
        return unit
    )

    # Slime Cube
    game.rules.unitGeneration.addGenerator(UNIT_SLIME_CUBE, proc (): Unit =
        let unit = newUnit()
        unit.name = UNIT_SLIME_CUBE
        unit.classification = @["Slime"]
        unit.sprite = game.getUnitSprite(unitSprites, 7, 0)
        game.giveAbility(unit, ABILITY_ZAP)
        unit.addArmor(DamageType.MAGICAL, 3)
        return unit
    )

    # Acolyte of C'thos
    game.rules.unitGeneration.addGenerator(UNIT_ACOLYTE_OF_CTHOS, proc (): Unit =
        let unit = newUnit()
        unit.name = UNIT_ACOLYTE_OF_CTHOS
        unit.classification = @["Humanoid", "Unknown"]
        unit.sprite = game.getUnitSprite(unitSprites, 4, 1)
        game.giveAbility(unit, ABILITY_CURE_WOUNDS)
        game.giveAbility(unit, ABILITY_ZAP)
        return unit
    )

    # Kobold Sycophant
    game.rules.unitGeneration.addGenerator(UNIT_KOBOLD_SYCOPHANT, proc (): Unit =
        let unit = newUnit()
        unit.name = UNIT_KOBOLD_SYCOPHANT
        unit.classification = @["Humanoid", "Kobold"]
        unit.sprite = game.getUnitSprite(unitSprites, 5, 1)
        game.giveAbility(unit, ABILITY_CHANT_OF_STRENGTH)
        game.giveAbility(unit, ABILITY_STAB)
        return unit
    )

    # Banshee
    game.rules.unitGeneration.addGenerator(UNIT_BANSHEE, proc (): Unit =
        let unit = newUnit()
        unit.name = UNIT_BANSHEE
        unit.classification = @["Spirit", "Banshee"]
        unit.sprite = game.getUnitSprite(unitSprites, 6, 1)
        game.giveAbility(unit, ABILITY_CURSE_OF_WEAKNESS)
        game.giveAbility(unit, ABILITY_ZAP)
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
            view.targeter.target(allies, (u: Unit) => u.heal(6))
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
            game.harvest(args, TILE_WATER, ITEM_SALMON)
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
            if a.dmg < 0:
                a.dmg = 0
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

    game.rules.itemGeneration.addGenerator(ITEM_RING_OF_STRENGTH, proc(): Item =
        let item = newItem()
        item.name = ITEM_RING_OF_STRENGTH
        item.desc = fmt"+2 {STRENGTH}"
        item.addSignalHandler(GET_STATS_CHANNEL, proc (this: Item, ctx: SignalContext, args: BaseSignalArgs): void =
            let a = cast[GetStatsSignalArgs](args)
            a.stats.incStat(STRENGTH, 2)
        )
        return item
    )

    # Food items
    game.rules.itemGeneration.addGenerator(ITEM_CHESTNUT, proc(): Item = game.createFoodItem(ITEM_CHESTNUT))
    game.rules.itemGeneration.addGenerator(ITEM_NOPAL, proc(): Item = game.createFoodItem(ITEM_NOPAL))
    game.rules.itemGeneration.addGenerator(ITEM_VEAL, proc(): Item = game.createFoodItem(ITEM_VEAL))
    game.rules.itemGeneration.addGenerator(ITEM_SALMON, proc(): Item = game.createFoodItem(ITEM_SALMON))

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
        tile.setAllBorders("water")
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
