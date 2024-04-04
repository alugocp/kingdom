import std/options
import std/sequtils
import std/strformat
import kingdom/game
import kingdom/world
import kingdom/math/types
import kingdom/math/hexagons
import kingdom/wrapper/sprites
import kingdom/builtin/values
import kingdom/builtin/signals
import kingdom/builtin/channels
import kingdom/entities/types
import kingdom/entities/stats
import kingdom/entities/item
import kingdom/entities/unit
import kingdom/entities/tile
import kingdom/entities/ability
import kingdom/entities/signals
import kingdom/generation/manager
import kingdom/controls/targeting

# Labels for mod content
const ABILITY_MOVE = "Move"
const ITEM_RING_OF_STRENGTH = "Ring of Strength"
const UNIT_PLASMOID_ADVENTURER = "Plasmoid Adventurer"
const TILE_GRASS = "Grass"
const TILE_WATER = "Water"

# Mod initialization procedure
proc initKingdomMod(game: Game): void {.exportc, dynlib.} =
    # Register spritesheets
    let unitSprites = game.sprites.registerSheet("vanilla", "units")
    let tileSprites = game.sprites.registerSheet("vanilla", "tile")

    # Unit generators
    game.unitGeneration.addGenerator(UNIT_PLASMOID_ADVENTURER, proc (): Unit =
        let unit = newUnit()
        unit.name = UNIT_PLASMOID_ADVENTURER
        unit.desc = some("Slimy guy")
        unit.sprite = game.sprites.getSpriteHandle(unitSprites, 0, 0)
        unit.abilities.add(game.abilityGeneration.generate(ABILITY_MOVE))
        return unit
    )

    # Ability generators
    game.abilityGeneration.addGenerator(ABILITY_MOVE, proc(): Ability =
        let ability = newAbility()
        ability.name = ABILITY_MOVE
        ability.addSignalHandler(ABILITY_CLICKED_CHANNEL, proc (this: Ability, ctx: SignalContext, args: BaseSignalArgs): void =
            let a = cast[AbilityClickedSignalArgs](args)
            let targets = a.host.pos.getAdjacentHexagonCoords(game.world.getBounds())
            let filtered = targets.filterIt(game.world.canUnitTravelAcrossTiles(a.host, a.host.pos, it))
            game.targeter.target(filtered, proc (c: Coord): void = game.world.moveUnit(a.host, c))
        )
        return ability
    )

    # Item generators
    game.itemGeneration.addGenerator(ITEM_RING_OF_STRENGTH, proc(): Item =
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
    game.tileGeneration.addGenerator(TILE_GRASS, proc(): Tile =
        let tile = newTile()
        tile.sprite = game.sprites.getSpriteHandle(tileSprites, 0, 0, 96, 110)
        return tile
    )
    game.tileGeneration.addGenerator(TILE_WATER, proc(): Tile =
        let tile = newTile()
        tile.sprite = game.sprites.getSpriteHandle(tileSprites, 96, 0, 96, 110)
        tile.setAllBorders("water")
        return tile
    )
