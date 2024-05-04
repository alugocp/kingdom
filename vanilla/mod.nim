import std/options
import std/strformat
import kingdom/headers
import kingdom/entities/types
import kingdom/builtin/values
import kingdom/builtin/signals
import kingdom/builtin/channels
import kingdom/models/types
import kingdom/mods/types

# Labels for mod content
const ITEM_RING_OF_STRENGTH = "Ring of Strength"
const UNIT_GLOOP = "Gloop the Adventurer"
const UNIT_BARNACLEHEAD = "Barnaclehead"
const UNIT_FERNANDO_UNFALTERING_GAZE = "Fernando of the Unfaltering Gaze"
const UNIT_HENRIETTA = "Henrietta"
const UNIT_DRUID = "Druid"
const UNIT_HOKA_AND_TATANKA = "Hoka and Tatanka"
const TILE_GRASS = "Grass"
const TILE_WATER = "Water"
const TILE_COAST = "Coast"
const TILE_DESERT = "Desert"
const TILE_CACTUS = "Cactus"
const TILE_ISLAND_FORTRESS = "Island Fortress"
const TILE_WARLOCK_TOWER = "Warlock Tower"
const TILE_FOREST = "Forest"

# Mod initialization procedure
proc initKingdomMod(game: ModCoreInterface): void {.exportc, dynlib.} =
    # Register spritesheets and set the edgeTileSprite
    let unitSprites = game.rules.sprites.registerSheet("vanilla", "units")
    let tileSprites = game.rules.sprites.registerSheet("vanilla", "tile")
    game.rules.edgeTileSprite = game.rules.sprites.getSpriteHandle(tileSprites, 96, 0, 96, 110)

    # Unit generators
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

    # Ability generators

    # Item generators
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

    # Tile generators
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
