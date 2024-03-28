import std/options
import std/strformat
import kingdom/game
import kingdom/world
import kingdom/math/types
import kingdom/math/hexagons
import kingdom/builtin/values
import kingdom/builtin/signals
import kingdom/builtin/channels
import kingdom/entities/types
import kingdom/entities/stats
import kingdom/entities/item
import kingdom/entities/unit
import kingdom/entities/ability
import kingdom/entities/signals
import kingdom/generation/manager
import kingdom/controls/targeting

# Labels for mod content
const ABILITY_MOVE = "Move"
const ITEM_RING_OF_STRENGTH = "Ring of Strength"
const UNIT_PLASMOID_ADVENTURER = "Plasmoid Adventurer"

# Mod initialization procedure
proc initKingdomMod(game: Game): void {.exportc, dynlib.} =
    game.unitGeneration.addGenerator(UNIT_PLASMOID_ADVENTURER, proc (): Unit =
        let unit = newUnit()
        unit.name = UNIT_PLASMOID_ADVENTURER
        unit.desc = some("Slimy guy")
        unit.abilities.add(game.abilityGeneration.generate(ABILITY_MOVE))
        return unit
    )
    game.abilityGeneration.addGenerator(ABILITY_MOVE, proc(): Ability =
        let ability = newAbility()
        ability.name = ABILITY_MOVE
        ability.addSignalHandler(ABILITY_CLICKED_CHANNEL, proc (this: Ability, ctx: SignalContext, args: BaseSignalArgs): void =
            let a = cast[AbilityClickedSignalArgs](args)
            let targets = a.host.pos.getAdjacentHexagonCoords(game.world.getBounds())
            game.targeter.target(targets, proc (c: Coord): void = game.world.moveUnit(a.host, c))
        )
        return ability
    )
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
