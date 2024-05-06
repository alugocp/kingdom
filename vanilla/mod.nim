import std/options
import std/sequtils
import std/strformat
import kingdom/headers
import kingdom/views/types
import kingdom/models/types
import kingdom/wrapper/types
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

#
# HELPER FUNCTIONS
#

# Collects the code for a basic attack ability
proc attack(game: ModCoreInterface, args: BaseSignalArgs, dtype: DamageType, dmg: int): void =
    let a = cast[AbilityClickedSignalArgs](args)
    let view = game.getGameView()
    let units = view.world.getUnits(a.host.pos)
    let filtered = units.filterIt(it.player != a.host.player)
    view.targeter.target(filtered, proc (u: Unit): void =
        let payload = newTakeDamageSignalArgs(dtype, dmg)
        u.damageTaken += payload.dmg
    )
# Shorthand to give some Unit armor against a certain DamageType
proc addArmor(u: Unit, dtype: DamageType, dmg: int): void =
    u.addSignalHandler(TAKE_DAMAGE_CHANNEL, proc (this: Unit, ctx: SignalContext, args: BaseSignalArgs): void =
        let a = cast[TakeDamageSignalArgs](args)
        if a.dtype == dtype:
            a.dmg -= dmg
            if a.dmg < 0:
                a.dmg = 0
    )

# Shorthand to give some Unit an Ability
proc giveAbility(game: ModCoreInterface, unit: Unit, ability: string): void =
    unit.abilities.add(game.rules.abilityGeneration.generate(ability))

# Shorthand to grab a tilesheet sprite
proc getUnitSprite(game: ModCoreInterface, sheet: SheetHandle, ix: uint16, iy: uint16): SpriteHandle =
    game.rules.sprites.getSpriteHandle(sheet, ix * 24, iy * 24, 24, 24)

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

    #
    # ABILITY GENERATORS
    #

    game.rules.abilityGeneration.addGenerator(ABILITY_STAB, proc(): Ability =
        let ability = newAbility()
        ability.name = ABILITY_STAB
        ability.desc = some("Deals 5 physical damage")
        ability.addSignalHandler(ABILITY_CLICKED_CHANNEL, proc (this: Ability, ctx: SignalContext, args: BaseSignalArgs): void =
            game.attack(args, DamageType.PHYSICAL, 5)
        )
        return ability
    )

    game.rules.abilityGeneration.addGenerator(ABILITY_ZAP, proc(): Ability =
        let ability = newAbility()
        ability.name = ABILITY_ZAP
        ability.desc = some("Deals 5 magical damage")
        ability.addSignalHandler(ABILITY_CLICKED_CHANNEL, proc (this: Ability, ctx: SignalContext, args: BaseSignalArgs): void =
            game.attack(args, DamageType.MAGICAL, 5)
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

    #
    # TILE GENERATORS
    #

    game.rules.tileGeneration.addGenerator(TILE_GRASS, proc(): Tile =
        let tile = newTile()
        tile.sprite = game.rules.sprites.getSpriteHandle(tileSprites, 0, 0, 96, 110)
        return tile
    )
    game.rules.tileGeneration.addGenerator(TILE_WATER, proc(): Tile =
        let tile = newTile()
        tile.sprite = game.rules.sprites.getSpriteHandle(tileSprites, 96, 0, 96, 110)
        tile.desc = some("Water that units must swim across")
        tile.setAllBorders("water")
        return tile
    )
    game.rules.tileGeneration.addGenerator(TILE_WARLOCK_TOWER, proc(): Tile =
        let tile = newTile()
        tile.sprite = game.rules.sprites.getSpriteHandle(tileSprites, 192, 0, 96, 110)
        return tile
    )
    game.rules.tileGeneration.addGenerator(TILE_DESERT, proc(): Tile =
        let tile = newTile()
        tile.sprite = game.rules.sprites.getSpriteHandle(tileSprites, 288, 0, 96, 110)
        return tile
    )
    game.rules.tileGeneration.addGenerator(TILE_FOREST, proc(): Tile =
        let tile = newTile()
        tile.sprite = game.rules.sprites.getSpriteHandle(tileSprites, 0, 110, 96, 110)
        return tile
    )
    game.rules.tileGeneration.addGenerator(TILE_COAST, proc(): Tile =
        let tile = newTile()
        tile.sprite = game.rules.sprites.getSpriteHandle(tileSprites, 96, 110, 96, 110)
        return tile
    )
    game.rules.tileGeneration.addGenerator(TILE_ISLAND_FORTRESS, proc(): Tile =
        let tile = newTile()
        tile.sprite = game.rules.sprites.getSpriteHandle(tileSprites, 192, 110, 96, 110)
        return tile
    )
    game.rules.tileGeneration.addGenerator(TILE_CACTUS, proc(): Tile =
        let tile = newTile()
        tile.sprite = game.rules.sprites.getSpriteHandle(tileSprites, 288, 110, 96, 110)
        return tile
    )
