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
const UNIT_PLASMOID_ADVENTURER = "Plasmoid Adventurer"
const UNIT_FERNANDO_UNFALTERING_GAZE = "Fernando of the Unfaltering Gaze"
const TILE_GRASS = "Grass"
const TILE_WATER = "Water"

# Mod initialization procedure
proc initKingdomMod(game: ModCoreInterface): void {.exportc, dynlib.} =
    # Register spritesheets and set the edgeTileSprite
    let unitSprites = game.rules.sprites.registerSheet("vanilla", "units")
    let tileSprites = game.rules.sprites.registerSheet("vanilla", "tile")
    game.rules.edgeTileSprite = game.rules.sprites.getSpriteHandle(tileSprites, 96, 0, 96, 110)

    # Unit generators
    game.rules.unitGeneration.addGenerator(UNIT_PLASMOID_ADVENTURER, proc (): Unit =
        let unit = newUnit()
        unit.name = UNIT_PLASMOID_ADVENTURER
        unit.desc = some("Slimy guy")
        unit.sprite = game.rules.sprites.getSpriteHandle(unitSprites, 0, 0)
        unit.addSignalHandler(GET_MOVEMENT_CHANNEL, proc (this: Unit, ctx: SignalContext, args: BaseSignalArgs): void =
            let payload = cast[GetMovementSignalArgs](args)
            payload.movement = 2
        )
        return unit
    )
    game.rules.unitGeneration.addGenerator(UNIT_FERNANDO_UNFALTERING_GAZE, proc (): Unit =
        let unit = newUnit()
        unit.name = UNIT_FERNANDO_UNFALTERING_GAZE
        unit.desc = some("He's a notorious Garuda warlock")
        unit.sprite = game.rules.sprites.getSpriteHandle(unitSprites, 48, 0)
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
        tile.setAllBorders("water")
        return tile
    )
